package ru.aiefu.fahrenheit.statuseffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import ru.aiefu.fahrenheit.Fahrenheit;
import ru.aiefu.fahrenheit.Utils;

public class Hypothermia extends StatusEffect {
    public Hypothermia() {
        super(StatusEffectType.HARMFUL,5);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(entity instanceof PlayerEntity && !Utils.containsEffectType(entity.getStatusEffects(), Fahrenheit.WARM_EFFECT)) {
            entity.applyStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3));
            entity.damage(DamageSource.GENERIC,1.0F);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
