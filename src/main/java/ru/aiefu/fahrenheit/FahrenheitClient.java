package ru.aiefu.fahrenheit;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;

public class FahrenheitClient implements ClientModInitializer {
    public static final Identifier HEAT_ICO = new Identifier(Fahrenheit.MOD_ID,"textures/hud/heat.png");

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            int centerX = mc.getWindow().getScaledWidth() / 2;
            int centerY = mc.getWindow().getScaledHeight() / 4;
            Window win = mc.getWindow();
            //RenderSystem.disableAlphaTest();
            mc.getTextureManager().bindTexture(HEAT_ICO);
            DrawableHelper.drawTexture(matrixStack, centerX, win.getScaledHeight() - 55, 0,0,16,16,18,18);
            //RenderSystem.enableAlphaTest();
        });
    }
}
