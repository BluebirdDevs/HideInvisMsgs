package bluebird.hideinvismsgs.mixin;

import bluebird.hideinvismsgs.HideInvisMsgs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @ModifyArg(method = "removePlayerFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private Component broadcastSystemMessage(Component component) {
        ServerPlayer player = this.player;
        boolean gamerule = false;
        if (player.level() instanceof ServerLevel serverLevel) gamerule = serverLevel.getGameRules().get(HideInvisMsgs.OBFUSCATED_LEAVE_MSG);
        boolean shouldObfuscate = gamerule && player.isInvisible() && (player.deathTime > 0 || player.getStats().getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH)) < 20);

        return Component.translatable("multiplayer.player.left", shouldObfuscate ? Component.literal("Obfuscated").withStyle(ChatFormatting.OBFUSCATED) : player.getDisplayName()).withStyle(ChatFormatting.YELLOW);
    }
}