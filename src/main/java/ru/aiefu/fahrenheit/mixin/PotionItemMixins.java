package ru.aiefu.fahrenheit.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.aiefu.fahrenheit.EnvironmentManager;
import ru.aiefu.fahrenheit.IPlayerMixins;

@Mixin(PotionItem.class)
public class PotionItemMixins {
    @Inject(method = "finishUsing", at = @At(value = "INVOKE", target = "java/util/List.iterator()Ljava/util/Iterator;"))
    private void addWaterPoints(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir){
        if(user instanceof IPlayerMixins){
            EnvironmentManager enviroManager = ((IPlayerMixins)user).getEnviroManager();
            enviroManager.addWaterLevels(2, 1);
            if(enviroManager.getTemp() > 10){
                enviroManager.addTempLevel(-1);
            }
        }
    }
}
