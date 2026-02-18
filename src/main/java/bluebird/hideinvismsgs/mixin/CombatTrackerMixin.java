package bluebird.hideinvismsgs.mixin;

import bluebird.hideinvismsgs.HideInvisMsgs;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CombatTracker.class)
public class CombatTrackerMixin {
    @Redirect(method = "getDeathMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDisplayName()Lnet/minecraft/network/chat/Component;"))
    public Component hideInvisMsgs$getDisplayName(LivingEntity livingEntity) {
        return HideInvisMsgs.hideinvismsgs$ObfuscateOrNormalDeaths(livingEntity);
    }

    @Redirect(method = "getFallMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDisplayName()Lnet/minecraft/network/chat/Component;"))
    public Component hideInvisMsgs$getFallMessage(LivingEntity livingEntity) {
        return HideInvisMsgs.hideinvismsgs$ObfuscateOrNormalDeaths(livingEntity);
    }

    @Redirect(method = "getFallMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatTracker;getDisplayName(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/network/chat/Component;"))
    public Component hideInvisMsgs$getFallMessage(Entity entity) {
        return HideInvisMsgs.hideinvismsgs$ObfuscateOrNormalDeaths(entity);
    }

    @Redirect(method = "getMessageForAssistedFall", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDisplayName()Lnet/minecraft/network/chat/Component;"))
    public Component hideInvisMsgs$getMessageForAssistedFall(LivingEntity livingEntity) {
        return HideInvisMsgs.hideinvismsgs$ObfuscateOrNormalDeaths(livingEntity);
    }
}
