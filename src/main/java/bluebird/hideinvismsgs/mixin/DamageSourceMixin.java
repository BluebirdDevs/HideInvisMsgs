package bluebird.hideinvismsgs.mixin;

import bluebird.hideinvismsgs.HideInvisMsgs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DamageSource.class)
public class DamageSourceMixin {

    @Redirect(method = "getLocalizedDeathMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDisplayName()Lnet/minecraft/network/chat/Component;"))
    public Component hideInvisMsgs$hideInvisDeaths(LivingEntity livingEntity) {
        return HideInvisMsgs.hideinvismsgs$ObfuscateOrNormalDeaths(livingEntity);
    }

    @Redirect(
            method = "getLocalizedDeathMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getDisplayName()Lnet/minecraft/network/chat/Component;"
            )
    )
    private Component hideInvisMsgs$hideInvisKills(Entity killer) {
        boolean enabled = false;
        boolean showTeam = false;
        if (killer.level() instanceof ServerLevel serverLevel) {
            enabled = serverLevel
                    .getGameRules()
                    .get(HideInvisMsgs.OBFUSCATED_INVIS_KILLS);
            showTeam = serverLevel
                    .getGameRules()
                    .get(HideInvisMsgs.SHOW_INVIS_TEAM_NAME);
        }

        if (enabled && killer instanceof Player && killer.isInvisible()) {
            if (showTeam) {
                return PlayerTeam.formatNameForTeam(killer.getTeam(), Component.literal("Obfuscated").withStyle(ChatFormatting.OBFUSCATED));
            }
            return Component.literal("Obfuscated").withStyle(ChatFormatting.OBFUSCATED);
        }
        return killer.getDisplayName();
    }
}