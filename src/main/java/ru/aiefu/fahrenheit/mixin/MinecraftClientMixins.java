package ru.aiefu.fahrenheit.mixin;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.aiefu.fahrenheit.Fahrenheit;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixins {
    @Shadow
    public ClientPlayerInteractionManager interactionManager;
    @Shadow
    public ClientPlayerEntity player;

    @Shadow @Nullable public HitResult crosshairTarget;

    @Inject(method = "doItemUse", at = @At("TAIL"), cancellable = true)
    private void drinkingWater(CallbackInfo ci){
        if(!this.interactionManager.isBreakingBlock() && !this.player.isRiding() && this.player.isSneaking() && this.player.getMainHandStack() == ItemStack.EMPTY){
            BlockHitResult raycast = (BlockHitResult) this.player.raycast(2.D, 0.0F, true);
            if(raycast.getType() == HitResult.Type.BLOCK && this.player.world.getBlockState(raycast.getBlockPos()).getBlock() == Blocks.WATER){
                ClientPlayNetworking.send(Fahrenheit.craftID("drink_from_water_block"), new PacketByteBuf(Unpooled.buffer()));
                this.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }
}
