package ru.aiefu.fahrenheit.mixin;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.aiefu.fahrenheit.Fahrenheit;

@Mixin(PlayerManager.class)
public class PlayerManagerMixins {
    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void sync_fahrenheit_cfg(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci){
        ServerPlayNetworking.send(player, Fahrenheit.craftID("sync_cfg"), new PacketByteBuf(Unpooled.buffer().writeBoolean(Fahrenheit.config_instance.enableTemperature).writeBoolean(Fahrenheit.config_instance.enableThirst)));
    }
}
