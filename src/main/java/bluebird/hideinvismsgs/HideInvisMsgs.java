package bluebird.hideinvismsgs;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.scores.PlayerTeam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HideInvisMsgs implements ModInitializer {
    public static final String MODID = "hideinvismsgs";
    public static Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static MinecraftServer minecraftServer;

    public static final GameRule<Boolean> OBFUSCATED_INVIS_DEATHS = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(of("obfuscated_invis_deaths"));
    public static final GameRule<Boolean> OBFUSCATED_INVIS_KILLS = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(of("obfuscated_invis_kills"));
    public static final GameRule<Boolean> SHOW_INVIS_TEAM_NAME = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(of("show_invis_team_name"));
    public static final GameRule<Boolean> OBFUSCATED_WEAPON_NAME = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(of("obfuscated_weapon_name"));
    public static final GameRule<Boolean> OBFUSCATED_LEAVE_MSG = GameRuleBuilder
            .forBoolean(false)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(of("obfuscated_dead_leave_msg"));
    public static final GameRule<Boolean> HIDE_DEATH_CAUSE = GameRuleBuilder
            .forBoolean(false)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(of("hide_death_cause"));
    public static final GameRule<Boolean> HIDE_WEAPON_NAME = GameRuleBuilder
            .forBoolean(false)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(of("hide_weapon_info"));

    public static Map<Identifier, GameRule<?>> RULES = Map.of(OBFUSCATED_INVIS_DEATHS.getIdentifier(), OBFUSCATED_INVIS_DEATHS, OBFUSCATED_INVIS_KILLS.getIdentifier(), OBFUSCATED_INVIS_KILLS, SHOW_INVIS_TEAM_NAME.getIdentifier(), SHOW_INVIS_TEAM_NAME, OBFUSCATED_WEAPON_NAME.getIdentifier(), OBFUSCATED_WEAPON_NAME, OBFUSCATED_LEAVE_MSG.getIdentifier(), OBFUSCATED_LEAVE_MSG, HIDE_DEATH_CAUSE.getIdentifier(), HIDE_DEATH_CAUSE, HIDE_WEAPON_NAME.getIdentifier(), HIDE_WEAPON_NAME);
    public static final SuggestionProvider<CommandSourceStack> OPTIONS = SuggestionProviders.register(of("options"), (c, p) -> SharedSuggestionProvider.suggest(RULES.keySet().stream().map(String::valueOf).map(str -> str.substring(str.indexOf(':') + 1)), p));

    public void onInitialize() {
        ServerTickEvents.START_SERVER_TICK.register(server -> minecraftServer = server);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(Commands.literal("hideinvismsgs")
                .requires(commandSourceStack -> commandSourceStack.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))
                .then(Commands.literal("preset")
                        .then(Commands.argument("preset", StringArgumentType.string()).suggests(InvisPresets.OPTIONS).executes(HideInvisMsgsCommands::setPreset)))
                .then(Commands.literal("rule")
                        .then(Commands.argument("gamerule", StringArgumentType.word()).suggests(OPTIONS)
                                .then(Commands.argument("value", BoolArgumentType.bool()).executes(HideInvisMsgsCommands::setValue))
                                .executes(HideInvisMsgsCommands::getValue)))
        ));
    }

    public static Component hideinvismsgs$ObfuscateOrNormalDeaths(LivingEntity livingEntity) {
        return hideinvismsgs$ObfuscateOrNormalDeaths((Entity) livingEntity);
    }

    public static Component hideinvismsgs$ObfuscateOrNormalItem(ItemStack item, Entity mob) {
        boolean showItem = false;
        boolean hideWeapon = false;
        if (mob == null) return null;
        if (mob.level() instanceof ServerLevel serverLevel) {
            showItem = serverLevel
                    .getGameRules()
                    .get(HideInvisMsgs.OBFUSCATED_WEAPON_NAME);
            hideWeapon = serverLevel
                    .getGameRules()
                    .get(HideInvisMsgs.HIDE_WEAPON_NAME);
        }
        if (mob instanceof Player && mob.isInvisible()) {
            if (hideWeapon) {
                MutableComponent a = ComponentUtils.wrapInSquareBrackets(Component.literal("Obfuscated").withStyle(ChatFormatting.OBFUSCATED));
                return item.isEnchanted() ? a.withStyle(ChatFormatting.AQUA) : a;
            } else if (showItem) {
                ItemStack newItem = item.copy();
                MutableComponent hoverItem = Component.literal("Obfuscated").withStyle(ChatFormatting.OBFUSCATED);
                if (newItem.has(DataComponents.CUSTOM_NAME)) {
                    newItem.set(DataComponents.CUSTOM_NAME, Component.literal("Obfuscated").withStyle(ChatFormatting.OBFUSCATED));
                    hoverItem.withStyle(ChatFormatting.ITALIC);
                }
                MutableComponent result = ComponentUtils.wrapInSquareBrackets(hoverItem);
                if (!newItem.isEmpty()) {
                    result.withStyle(newItem.getRarity().color()).withStyle((s) -> s.withHoverEvent(new HoverEvent.ShowItem(newItem)));
                }
                return result;
            }
        }
        return item.getDisplayName();
    }


    public static Component hideinvismsgs$ObfuscateOrNormalDeaths(Entity livingEntity) {
        boolean enabled = false;
        boolean showTeam = false;
        if (livingEntity == null) return null;
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            enabled = serverLevel
                    .getGameRules()
                    .get(HideInvisMsgs.OBFUSCATED_INVIS_DEATHS);
            showTeam = serverLevel
                    .getGameRules()
                    .get(HideInvisMsgs.SHOW_INVIS_TEAM_NAME);
        }
        if (enabled && livingEntity instanceof Player && livingEntity.isInvisible()) {
            if (showTeam) {
                return PlayerTeam.formatNameForTeam(livingEntity.getTeam(), Component.literal("Obfuscated").withStyle(ChatFormatting.OBFUSCATED));
            }
            return Component.literal("Obfuscated").withStyle(ChatFormatting.OBFUSCATED);
        }
        return livingEntity.getDisplayName();
    }

    public static Identifier of(String string) {
        return Identifier.fromNamespaceAndPath(MODID, string);
    }
}