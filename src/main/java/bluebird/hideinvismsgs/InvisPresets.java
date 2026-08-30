package bluebird.hideinvismsgs;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;

import java.util.Arrays;
import java.util.Map;

public class InvisPresets {
    public enum type {
        DEFAULT,
        STRENGTH,
        SCRIPTED,
        SMP,
        DISABLED
    }

    public static final Map<String, Boolean> DEFAULT = Map.of("obfuscated_invis_deaths", true, "obfuscated_invis_kills", true, "show_invis_team_name", true, "obfuscated_weapon_name", true, "obfuscated_dead_leave_msg", false, "hide_death_cause", false, "hide_weapon_info", false);
    public static final Map<String, Boolean> STRENGTH = Map.of("obfuscated_invis_deaths", false, "obfuscated_invis_kills", true, "show_invis_team_name", false, "obfuscated_weapon_name", false, "obfuscated_dead_leave_msg", false, "hide_death_cause", true, "hide_weapon_info", false);
    public static final Map<String, Boolean> SCRIPTED = Map.of("obfuscated_invis_deaths", false, "obfuscated_invis_kills", true, "show_invis_team_name", true, "obfuscated_weapon_name", true, "obfuscated_dead_leave_msg", true, "hide_death_cause", false, "hide_weapon_info", false);
    public static final Map<String, Boolean> SMP = Map.of("obfuscated_invis_deaths", true, "obfuscated_invis_kills", true, "show_invis_team_name", false, "obfuscated_weapon_name", true, "obfuscated_dead_leave_msg", false, "hide_death_cause", false, "hide_weapon_info", false);
    public static final Map<String, Boolean> DISABLED = Map.of("obfuscated_invis_deaths", false, "obfuscated_invis_kills", false, "show_invis_team_name", false, "obfuscated_weapon_name", false, "obfuscated_dead_leave_msg", false, "hide_death_cause", false, "hide_weapon_info", false);

    public static final SuggestionProvider<CommandSourceStack> OPTIONS = SuggestionProviders.register(HideInvisMsgs.of("presets"), (c, p) -> SharedSuggestionProvider.suggest(Arrays.stream(type.values()).map(String::valueOf).sorted(), p));
}
