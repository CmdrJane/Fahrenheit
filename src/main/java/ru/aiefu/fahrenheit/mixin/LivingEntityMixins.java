package ru.aiefu.fahrenheit.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.aiefu.fahrenheit.Fahrenheit;
import ru.aiefu.fahrenheit.IPlayerMixins;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixins extends Entity {

    public LivingEntityMixins(EntityType<?> type, World world) {
        super(type, world);
    }
    @ModifyVariable(method = "getNextAirOnLand", at = @At("HEAD"), ordinal = 0)
    private int highAltitudeAirPatch(int air){
        if(this instanceof IPlayerMixins && ((LivingEntity)(Object)this).hasStatusEffect(Fahrenheit.THIN_AIR)){
            return air - 4;
        }
        return air;
    }
}
