package ru.aiefu.fahrenheit;

import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EnvironmentManager {
    private int temp = 0;
    private float tempProgress = 0;
    private int tickTimer = 0;

    //StatusEffectsInstances
    public static final StatusEffectInstance WET_EFFECT = new StatusEffectInstance(Fahrenheit.WET_EFFECT, 5);
    public static final StatusEffectInstance HEAT_STROKE = new StatusEffectInstance(Fahrenheit.HEAT_STROKE, 1);
    public static final StatusEffectInstance HYPOTHERMIA = new StatusEffectInstance(Fahrenheit.HYPOTHERMIA, 1);
    public static final StatusEffectInstance COLD_EFFECT = new StatusEffectInstance(Fahrenheit.COLD_EFFECT, 1);
    public static final StatusEffectInstance HEAT_EFFECT = new StatusEffectInstance(Fahrenheit.HEAT_EFFECT, 1);
    public static final StatusEffectInstance DEADLY_COLD = new StatusEffectInstance(Fahrenheit.DEADLY_COLD_EFFECT, 1);
    public static final StatusEffectInstance DEADLY_HEAT = new StatusEffectInstance(Fahrenheit.DEADLY_HEAT_EFFECT, 1);


    public void tick(PlayerEntity player){
        ++tickTimer;

        if(this.tempProgress >= 6.0F){
            this.tempProgress = 0;
            this.temp = Math.min(this.temp += 1, 15);
        }
        if (this.tempProgress <= -6.0F){
            this.tempProgress = 0;
            this.temp = Math.max(this.temp -= 1, -15);
        }

        if(tickTimer >= 10) {
            tickTimer = 0;

            BlockPos playerPos = player.getBlockPos();
            float biomeTemp = player.world.getBiome(playerPos).getTemperature();
            Registry<DimensionType> dimTypeReg = player.world.getRegistryManager().getDimensionTypes();
            DimensionType playerDim = player.world.getDimension();

            if(playerDim == dimTypeReg.get(DimensionType.THE_NETHER_REGISTRY_KEY) && !player.hasStatusEffect(Fahrenheit.CHILL_EFFECT)){
                player.addStatusEffect(DEADLY_HEAT);
            }
            else if(playerDim == dimTypeReg.get(DimensionType.THE_END_REGISTRY_KEY) && !player.hasStatusEffect(Fahrenheit.WARM_EFFECT)){
                player.addStatusEffect(DEADLY_COLD);
            }
            else if(this.temp > 8 && this.temp < 15){
                player.addStatusEffect(HEAT_EFFECT);
            }
            else if(this.temp >= 15){
                player.addStatusEffect(HEAT_STROKE);
            }
            else if(this.temp < -8 && this.temp > -15){
                player.addStatusEffect(COLD_EFFECT);
            }
            else if(this.temp <= -15){
                player.addStatusEffect(HYPOTHERMIA);
            }

            if(biomeTemp <= 0.20 && biomeTemp < 0.4F){
                this.tempProgress -=0.3;
            }
            else if (biomeTemp > 0.4F && biomeTemp < 0.6F){
                this.tempProgress -= 0.15;
            }
            else if (biomeTemp < 0.8F && biomeTemp >= 0.6F){
                if(player.world.isDay()){
                    this.tempProgress += 0.05;
                }
                else {
                    this.tempProgress -= 0.08;
                }
            }
            else if (biomeTemp >= 0.8F && biomeTemp < 1.5F){
                this.tempProgress += 0.2;
            }
            else if (biomeTemp >= 1.5F){
                this.tempProgress += 0.3;
                if(player.world.getBiome(playerPos).getCategory().equals(Biome.Category.DESERT)){
                    if(player.world.isDay()) {
                        this.tempProgress += 0.1;
                    }
                    else {
                        this.tempProgress -= 0.6;
                    }
                }
            }

            if (player.isWet() && !player.hasStatusEffect(Fahrenheit.WARM_EFFECT)) {
                player.addStatusEffect(WET_EFFECT);
                this.tempProgress -= 0.05F;
            }

            Map<Identifier, Runnable> blocks = new HashMap<>();
            blocks.put(Registry.BLOCK.getId(Blocks.LAVA), () -> {System.out.println("It's Worked");
            });
            for(BlockPos pos : BlockPos.iterateOutwards(playerPos, 4,3,4)){
                Identifier id = Registry.BLOCK.getId(player.world.getBlockState(pos).getBlock());
                if(blocks.containsKey(id)){
                    blocks.get(id).run();
                    return;
                }
            }
        }
    }
    public void addTempProgress(float temp){
        this.tempProgress += temp;
    }
    public void addTempLevel(int temp){
        this.temp += temp;
    }
}
