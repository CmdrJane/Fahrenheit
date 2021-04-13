package ru.aiefu.fahrenheit.statuseffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.Difficulty;
import ru.aiefu.fahrenheit.Fahrenheit;

public class HeatStrokeEffect extends StatusEffect {
    public HeatStrokeEffect() {
        super(StatusEffectType.HARMFUL, 5);
    }
    private int timer = 0;
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        ++timer;
        if(timer >= 30 && entity instanceof ServerPlayerEntity && !entity.hasStatusEffect(Fahrenheit.CHILL_EFFECT) && !entity.hasStatusEffect(Fahrenheit.DEADLY_HEAT_EFFECT)){
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 15));
            Difficulty diff = entity.world.getDifficulty();
            if(diff == Difficulty.NORMAL && entity.getHealth() > 1.0F) {
                entity.damage(Fahrenheit.HEAT_STROKE_DMG, 0.5F);
            } else if(diff == Difficulty.HARD){
                entity.damage(Fahrenheit.HEAT_STROKE_DMG, 1.5F);
            }
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
