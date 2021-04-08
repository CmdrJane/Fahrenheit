package ru.aiefu.fahrenheit.statuseffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;
import ru.aiefu.fahrenheit.Fahrenheit;

public class ThinAirEffect extends StatusEffect {
    private int timer = 0;

    public ThinAirEffect() {
        super(StatusEffectType.NEUTRAL, 5);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        ++this.timer;
        if(this.timer >= 20 && entity instanceof PlayerEntity && !entity.isSubmergedInWater() && entity.getEyeY() >= 170.0D){
            int f = (int) ((entity.getEyeY() - 170.0D) * 0.1D);
            entity.setAir(entity.getAir() -(2 + f));
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
