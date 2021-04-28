package ru.aiefu.fahrenheit.statuseffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.server.network.ServerPlayerEntity;
import ru.aiefu.fahrenheit.Fahrenheit;
import ru.aiefu.fahrenheit.IPlayerMixins;

public class WetEffect extends StatusEffect {
    public WetEffect() {
        super(StatusEffectType.NEUTRAL, 0x98D98);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(entity instanceof ServerPlayerEntity && !entity.hasStatusEffect(Fahrenheit.WARM_EFFECT)){
            ((ServerPlayerEntity) entity).addExhaustion(0.02F);
        }
        else if(entity instanceof ServerPlayerEntity && entity.hasStatusEffect(Fahrenheit.CHILL_EFFECT)){
            ((IPlayerMixins)entity).getEnviroManager().addTempProgress(-0.08F);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
