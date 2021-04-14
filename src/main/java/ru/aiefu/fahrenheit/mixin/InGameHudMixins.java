package ru.aiefu.fahrenheit.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.aiefu.fahrenheit.FahrenheitClient;

@Mixin(InGameHud.class)
public class InGameHudMixins extends DrawableHelper {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "net/minecraft/util/profiler/Profiler.pop()V"))
    private void renderFahrenheitTemp(MatrixStack matrices, CallbackInfo ci){
        this.client.getTextureManager().bindTexture(FahrenheitClient.HEAT_ICO);
        Window win = this.client.getWindow();
        DrawableHelper.drawTexture(matrices, win.getScaledWidth() / 2,win.getScaledHeight(), 0,0,16,16, 18,18);
    }
}
