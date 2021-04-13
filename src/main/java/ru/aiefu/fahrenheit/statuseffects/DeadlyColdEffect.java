package ru.aiefu.fahrenheit.statuseffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.server.network.ServerPlayerEntity;
import ru.aiefu.fahrenheit.Fahrenheit;

public class DeadlyColdEffect extends StatusEffect {
    private int timer = 0;
    public DeadlyColdEffect() {
        super(StatusEffectType.HARMFUL, 1);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        ++timer;
        if(timer >= 30 && entity instanceof ServerPlayerEntity && !entity.hasStatusEffect(Fahrenheit.WARM_EFFECT)){
            timer = 0;
            entity.damage(Fahrenheit.DEADLY_COLD_DMG, 1.5F);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
