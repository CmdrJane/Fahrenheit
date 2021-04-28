package ru.aiefu.fahrenheit;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class FahrenheitClient implements ClientModInitializer {
    public static final Identifier HEAT_ICO = new Identifier(Fahrenheit.MOD_ID,"textures/hud/fahrenheit_icons.png");
    public static int fahrenheit_temp_status = 0;
    public static int fahrenheit_water_status = 20;
    public static boolean should_render_temp = true;
    public static boolean should_render_thirst = true;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(Fahrenheit.craftID("sync_temp"), (client, handler, buf, responseSender) -> {
            fahrenheit_temp_status = buf.readInt();
            fahrenheit_water_status = buf.readInt();
        });
        ClientPlayNetworking.registerGlobalReceiver(Fahrenheit.craftID("sync_cfg"), (client, handler, buf, responseSender) -> {
            boolean temp_enabled = buf.readBoolean();
            boolean water_enabled = buf.readBoolean();
            if(temp_enabled){
                should_render_temp = Fahrenheit.config_instance.enableTemperatureHud;
            }
            else {
                should_render_temp = false;
            }
            if(water_enabled){
                should_render_thirst = Fahrenheit.config_instance.enableThirstHud;
            }
            else {
                should_render_thirst = false;
            }
        });
    }
}
