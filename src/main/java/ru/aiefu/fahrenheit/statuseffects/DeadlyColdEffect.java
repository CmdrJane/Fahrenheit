package ru.aiefu.fahrenheit.statuseffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.server.network.ServerPlayerEntity;
import ru.aiefu.fahrenheit.Fahrenheit;

public class DeadlyColdEffect extends StatusEffect {
    public DeadlyColdEffect() {
        super(StatusEffectType.HARMFUL, 1);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(entity instanceof ServerPlayerEntity && !entity.hasStatusEffect(Fahrenheit.WARM_EFFECT)){
            entity.damage(DamageSource.GENERIC, 2.0F);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
