package ru.aiefu.fahrenheit.statuseffects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class ChillEffect extends StatusEffect {
    public ChillEffect() {
        super(StatusEffectType.NEUTRAL, 1);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
