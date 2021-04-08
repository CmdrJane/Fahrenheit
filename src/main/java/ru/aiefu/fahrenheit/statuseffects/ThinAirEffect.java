package ru.aiefu.fahrenheit.statuseffects;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import ru.aiefu.fahrenheit.Fahrenheit;

public class ThinAirEffect extends StatusEffect {
    private int timer = 0;

    public ThinAirEffect() {
        super(StatusEffectType.NEUTRAL, 5);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        ++this.timer;
        System.out.println(entity.getAir());
        if(this.timer >= 20 && entity instanceof ServerPlayerEntity && !entity.isSubmergedInWater() && entity.getEyeY() >= 170.0D){
            int r = EnchantmentHelper.getRespiration(entity);
            int f = (int) ((entity.getEyeY() - 170.0D) * 0.2D);
            int j = r > 0 && entity.getRandom().nextInt(r + 1) > 0 ? 0 : 2 + f;
            entity.setAir(entity.getAir() -j);
            this.timer = 0;
            if(entity.getAir() < -20) {
                entity.setAir(-20);
                entity.damage(Fahrenheit.OUT_OF_AIR, 2.0F);
            }
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration > 0;
    }
}
