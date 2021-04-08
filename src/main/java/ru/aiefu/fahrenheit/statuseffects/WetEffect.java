package ru.aiefu.fahrenheit.statuseffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;
import ru.aiefu.fahrenheit.Fahrenheit;
import ru.aiefu.fahrenheit.IPlayerMixins;
import ru.aiefu.fahrenheit.Utils;

public class WetEffect extends StatusEffect {
    public WetEffect() {
        super(StatusEffectType.NEUTRAL, 0x98D98);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(entity instanceof PlayerEntity && !Utils.containsEffectType(entity.getStatusEffects(), Fahrenheit.WARM_EFFECT)){
            ((PlayerEntity) entity).addExhaustion(0.05F);
        }
        else if(entity instanceof PlayerEntity && Utils.containsEffectType(entity.getStatusEffects(), Fahrenheit.CHILL_EFFECT)){
            ((IPlayerMixins)entity).getEnviroManager().addTempProgress(-0.3F);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
