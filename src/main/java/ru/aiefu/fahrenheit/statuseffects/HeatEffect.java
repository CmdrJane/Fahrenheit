package ru.aiefu.fahrenheit.statuseffects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class HeatEffect extends StatusEffect {
    public HeatEffect() {
        super(StatusEffectType.NEUTRAL, 5);
    }
}
