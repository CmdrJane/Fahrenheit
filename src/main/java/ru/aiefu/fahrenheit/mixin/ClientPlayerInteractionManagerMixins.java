package ru.aiefu.fahrenheit.mixin;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.screenhandler.client.ClientNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.aiefu.fahrenheit.Fahrenheit;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixins {
    @Inject(method = "interactBlock", at = @At(value = "INVOKE", target = "net/minecraft/client/network/ClientPlayerEntity.shouldCancelInteraction()Z"), cancellable = true)
    private void drinkingWater(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir){
        HitResult raycast = player.raycast(20.0D,0.0F, true);
        if(raycast.getType() == HitResult.Type.BLOCK && world.getBlockState(new BlockPos(raycast.getPos())).getBlock() == Blocks.WATER && player.isSneaking()){
            ClientPlayNetworking.send(Fahrenheit.craftID("drink_from_water_block"), new PacketByteBuf(Unpooled.buffer()));
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
