package bluebird.hideinvismsgs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.scores.PlayerTeam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HideInvisMsgs implements ModInitializer {

    public static Logger LOGGER = LoggerFactory.getLogger("hideinvismsgs");

    private static final Identifier GAMERULE_IDENTIFIER = Identifier.fromNamespaceAndPath("hideinvismsgs","obfuscate_invis_deaths");
    private static final Identifier GAMERULE_IDENTIFIER_2 = Identifier.fromNamespaceAndPath("hideinvismsgs","obfuscate_invis_kills");
    private static final Identifier GAMERULE_IDENTIFIER_3 = Identifier.fromNamespaceAndPath("hideinvismsgs","show_invis_team_name");

    public static final GameRule<Boolean> OBFUSCATED_INVIS_DEATHS = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(GAMERULE_IDENTIFIER);
    public static final GameRule<Boolean> OBFUSCATED_INVIS_KILLS = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(GAMERULE_IDENTIFIER_2);
    public static final GameRule<Boolean> SHOW_INVIS_TEAM_NAME = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(GAMERULE_IDENTIFIER_3);

    public void onInitialize() {
        LOGGER.info("HideInvisDeaths initialed");
    }

    public static Component hideinvismsgs$ObfuscateOrNormalDeaths(LivingEntity livingEntity) {
        return hideinvismsgs$ObfuscateOrNormalDeaths((Entity) livingEntity);
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
}