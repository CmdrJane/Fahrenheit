package ru.aiefu.fahrenheit.statuseffects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class DeadlyColdEffect extends StatusEffect {
    public DeadlyColdEffect() {
        super(StatusEffectType.HARMFUL, 1);
    }
}
