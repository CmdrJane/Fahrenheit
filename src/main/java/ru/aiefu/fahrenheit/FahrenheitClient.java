package ru.aiefu.fahrenheit;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class FahrenheitClient implements ClientModInitializer {
    public static final Identifier HEAT_ICO = new Identifier(Fahrenheit.MOD_ID,"textures/hud/fahrenheit_icons.png");

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(Fahrenheit.craftID("sync_temp"), (client, handler, buf, responseSender) -> ((IPlayerMixins)client.player).getEnviroManager().setTemp(buf.readInt()));
    }
}
