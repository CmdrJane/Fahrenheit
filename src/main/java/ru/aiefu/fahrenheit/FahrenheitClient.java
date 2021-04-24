package ru.aiefu.fahrenheit;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class FahrenheitClient implements ClientModInitializer {
    public static final Identifier HEAT_ICO = new Identifier(Fahrenheit.MOD_ID,"textures/hud/fahrenheit_icons.png");
    public static int fahrenheit_temp_status = 0;
    public static int fahrenheit_water_status = 20;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(Fahrenheit.craftID("sync_temp"), (client, handler, buf, responseSender) -> {
            fahrenheit_temp_status = buf.readInt();
            fahrenheit_water_status = buf.readInt();
        });
    }
}
