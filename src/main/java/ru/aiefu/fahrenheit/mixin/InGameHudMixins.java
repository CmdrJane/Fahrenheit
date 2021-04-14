package ru.aiefu.fahrenheit.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.aiefu.fahrenheit.FahrenheitClient;
import ru.aiefu.fahrenheit.IPlayerMixins;

@Mixin(InGameHud.class)
public class InGameHudMixins extends DrawableHelper {
    @Shadow @Final private MinecraftClient client;

    /*
    @ModifyVariable(method = "renderStatusBars", at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerEntity.getArmor()I"), ordinal = 9)
    private int armorOffset(int s){
        return s - 10;
    }

     */

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "net/minecraft/util/profiler/Profiler.pop()V"))
    private void renderFahrenheitTemp(MatrixStack matrices, CallbackInfo ci){
        this.client.getTextureManager().bindTexture(FahrenheitClient.HEAT_ICO);
        MinecraftClient mc = MinecraftClient.getInstance();
        Window win = mc.getWindow();
        ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
        int m = win.getScaledWidth() / 2 - 91;
        int o = win.getScaledHeight() - 39;
        float f = (float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
        int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
        int q = MathHelper.ceil((f + (float)p) / 2.0F / 10.0F);
        int r = Math.max(10 - (q - 2), 3);
        int s = o - (q - 1) * r - 10;
        int rq = playerEntity.getArmor() > 0 ? s - 11 : s - 1;
        int tp = ((IPlayerMixins)playerEntity).getEnviroManager().getTemp();
        int aa;
        for(int z = 0; z < 10; ++z) {
            aa = m + z * 8;
            //Full
            if (-z * 2 - 1 > tp && tp > -16) {
                drawTexture(matrices, aa, rq, 56, 3, 9, 9, 256, 256);
            }
            //Half
            else if (-z * 2 - 1 == tp && tp > -16) {
                drawTexture(matrices, aa, rq, 47, 3, 8, 9, 256, 256);
            }
            //Full
            else if (z * 2 + 1 < tp && tp < 16) {
                drawTexture(matrices, aa, rq, 20, 3, 9, 9, 256, 256);
            }
            //Half
            else if (z * 2 + 1 == tp) {
                drawTexture(matrices, aa, rq, 11, 3, 8, 9, 256, 256);
            }
            //Full
            else if (z * 2 + 1 < tp && tp >= 16) {
                drawTexture(matrices, aa, rq - 1, 3, 3, 8, 10, 256, 256);
            }
            //Half
            /*
            else if (z * 2 + 1 == tp && tp >= 16) {
                drawTexture(matrices, aa, rq - 1, 3, 3, 8, 10, 256, 256);
            }
             */
        }
        this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
    }
}
