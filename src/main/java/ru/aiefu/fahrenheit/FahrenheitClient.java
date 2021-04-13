package ru.aiefu.fahrenheit;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class FahrenheitClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient.getInstance().inGameHud;
        });
    }
}
