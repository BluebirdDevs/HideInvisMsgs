package bluebird.hideinvismsgs;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRule;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SuppressWarnings("unchecked")
public class HideInvisMsgsCommands {
    public static void setValue(String key, Boolean value) {
        MinecraftServer server = HideInvisMsgs.minecraftServer;
        GameRule<?> rule = getGameRule(key);
        try {
            server.getWorldData().getGameRules().set((GameRule<Boolean>) rule, value, server);
        } catch (Exception e) {
            HideInvisMsgs.LOGGER.warn("Failed to set preset value for " + key + ": " + e.getMessage());
        }
    }

    public static @Nullable GameRule<?> getGameRule(String rule) {
        return HideInvisMsgs.RULES.get(HideInvisMsgs.of(rule));
    }

    public static int setValue(CommandContext<CommandSourceStack> source) {
        String gamerule = StringArgumentType.getString(source, "gamerule");
        boolean value = BoolArgumentType.getBool(source, "value");
        GameRule<?> rule = getGameRule(gamerule);

        if (rule == null) {
            source.getSource().sendFailure(Component.literal(String.format("%s is not a valid rule", gamerule)));
            return 0;
        }

        MinecraftServer server = HideInvisMsgs.minecraftServer;
        server.getWorldData().getGameRules().set((GameRule<Boolean>) rule, value, server);

        source.getSource().sendSuccess(() -> Component.literal(String.format("Set %s to: %b", gamerule, value)), true);
        return 1;
    }

    public static int getValue(CommandContext<CommandSourceStack> source) {
        String gamerule = StringArgumentType.getString(source, "gamerule");
        if (getGameRule(gamerule) == null) {
            source.getSource().sendFailure(Component.literal(String.format("%s is not a valid rule", gamerule)));
            return 0;
        }
        source.getSource().sendSuccess(() -> Component.literal(String.format("%s is currently: %b", gamerule, HideInvisMsgs.minecraftServer.getWorldData().getGameRules().get((GameRule<Boolean>) getGameRule(gamerule)))), false);
        return 1;
    }

    public static int setPreset(CommandContext<CommandSourceStack> source) {
        String preset = StringArgumentType.getString(source, "preset");
        switch(InvisPresets.type.valueOf(preset)) {
            case DEFAULT: setPreset(InvisPresets.DEFAULT); break;
            case STRENGTH: setPreset(InvisPresets.STRENGTH); break;
            case SCRIPTED: setPreset(InvisPresets.SCRIPTED); break;
            case SMP: setPreset(InvisPresets.SMP); break;
            case DISABLED: setPreset(InvisPresets.DISABLED); break;
            default: source.getSource().sendFailure(Component.literal("Invalid preset type: " + preset)); return 0;
        }
        source.getSource().sendSuccess(() -> Component.literal(String.format("Preset Loaded: %s", preset)), true);
        return 1;
    }

    public static void setPreset(Map<String, Boolean> preset) {
        for (Map.Entry<String, Boolean> entry : preset.entrySet()) {
            String key = entry.getKey();
            boolean value = entry.getValue();
            setValue(key, value);
        }
    }
}
