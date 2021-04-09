package ru.aiefu.fahrenheit.statuseffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
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

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(entity instanceof ServerPlayerEntity && !entity.hasStatusEffect(Fahrenheit.CHILL_EFFECT)){
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 15));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 15));
            Difficulty diff = entity.world.getDifficulty();
            if(diff == Difficulty.NORMAL && entity.getHealth() > 1.0F) {
                entity.damage(DamageSource.GENERIC, 0.5F);
            } else if(diff == Difficulty.HARD){
                entity.damage(DamageSource.GENERIC, 1.0F);
            }
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
