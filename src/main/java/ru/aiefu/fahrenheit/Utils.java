package ru.aiefu.fahrenheit;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.Collection;

public class Utils {
    public static boolean containsEffectType(Collection<StatusEffectInstance> effects, StatusEffect compareTo){
        for(StatusEffectInstance e : effects){
            if(e.getEffectType() == compareTo){
                return true;
            }
        }
        return false;
    }
}
