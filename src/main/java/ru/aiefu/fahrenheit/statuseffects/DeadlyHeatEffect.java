package ru.aiefu.fahrenheit.statuseffects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class DeadlyHeatEffect extends StatusEffect {
    public DeadlyHeatEffect() {
        super(StatusEffectType.HARMFUL, 1);
    }
}
