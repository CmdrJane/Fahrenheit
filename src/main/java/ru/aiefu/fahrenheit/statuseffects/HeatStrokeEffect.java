package ru.aiefu.fahrenheit.statuseffects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class HeatStrokeEffect extends StatusEffect {
    public HeatStrokeEffect() {
        super(StatusEffectType.HARMFUL, 5);
    }
}
