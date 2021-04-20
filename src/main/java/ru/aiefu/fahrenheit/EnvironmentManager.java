package ru.aiefu.fahrenheit;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentManager {
    private int temp = 0;
    private float tempProgress = 0;
    private int tickTimer = 0;
    private int thirstTimer = 0;
    private int water = 20;
    private int hydration = 0;
    private float waterProgress = 0;
    private Map<Identifier, Float> blocksTempMap = new HashMap<>();

    public void tick(PlayerEntity player){
        ++tickTimer;

        if(this.tempProgress >= 6.0F){
            this.tempProgress = 0;
            this.temp = Math.min(this.temp + 1, 20);
        }
        else if (this.tempProgress <= -6.0F){
            this.tempProgress = 0;
            this.temp = Math.max(this.temp - 1, -20);
        }
        if(this.water >= 20 && hydration > 0 && this.waterProgress >= 6.0F){
            this.waterProgress = 0;
            this.hydration = Math.max(this.hydration - 1, 0);
        }
        else if(this.waterProgress >= 12.0F){
            this.waterProgress = 0;
            this.water = Math.max(this.water - 1, 0);
        }
        if(this.water <= 0){
            ++this.thirstTimer;
        }
        if(this.thirstTimer >= 80){
            this.thirstTimer = 0;
            player.damage(Fahrenheit.DEHYDRATION, 1.0F);
        }

        if(tickTimer >= 10) {
            long startTime = System.nanoTime();
            tickTimer = 0;

            World world = player.world;
            BlockPos playerPos = player.getBlockPos();
            Iterable<BlockPos> posIterable = BlockPos.iterateOutwards(playerPos, 4,3,4);
            float biomeTemp = world.getBiome(playerPos).getTemperature();
            Registry<DimensionType> dimTypeReg = world.getRegistryManager().getDimensionTypes();
            DimensionType playerDim = world.getDimension();
            boolean blChill = player.hasStatusEffect(Fahrenheit.CHILL_EFFECT);
            boolean blWarm = player.hasStatusEffect(Fahrenheit.WARM_EFFECT);
            boolean isWet = player.isWet();


            if(playerDim == dimTypeReg.get(DimensionType.THE_NETHER_REGISTRY_KEY) && !blChill){
                this.tempProgress += 3.0F;
                if(this.temp >= 20) {
                    player.addStatusEffect(new StatusEffectInstance(Fahrenheit.DEADLY_HEAT_EFFECT, 15));
                }
            }
            else if(playerDim == dimTypeReg.get(DimensionType.THE_END_REGISTRY_KEY) && !blWarm){
                player.addStatusEffect(new StatusEffectInstance(Fahrenheit.DEADLY_COLD_EFFECT, 15));
            }
            else if(this.temp >= 20 && !blChill){
                player.addStatusEffect(new StatusEffectInstance(Fahrenheit.HEAT_STROKE, 15));
            }
            else if(this.temp <= -20 && !blWarm){
                player.addStatusEffect(new StatusEffectInstance(Fahrenheit.HYPOTHERMIA, 15));
            }

            float tmp = 0;
            if( biomeTemp <= 0.35F){
                tmp = -0.3F;
            }
            else if (biomeTemp > 0.35F && biomeTemp < 0.6F){
                tmp = -0.15F;
            }
            else if (biomeTemp < 0.8F && biomeTemp >= 0.6F){
                tmp = world.isDay() ? 0.07F : -0.10F;
            }
            else if (biomeTemp >= 0.8F && biomeTemp < 1.5F){
                tmp = 0.2F;
            }
            else if (biomeTemp >= 1.5F){
                tmp = 0.3F;
                if(world.getBiome(playerPos).getCategory().equals(Biome.Category.DESERT)){
                    if(!world.isDay()) {
                       tmp = -0.3F;
                    }
                }
            }
            if(playerPos.getY() < player.world.getSeaLevel() - 6 || (!world.isSkyVisible(playerPos) && checkStoneNearby(player, posIterable))){
                tmp = -0.15F;
                System.out.println(tmp);
            }

            if (isWet && !blWarm) {
                player.addStatusEffect(new StatusEffectInstance(Fahrenheit.WET_EFFECT, 60));
                this.tempProgress -= 0.2F;
            }
            if(player.getEyeY() >= 170){
                player.addStatusEffect(new StatusEffectInstance(Fahrenheit.THIN_AIR, 15));
            }
            if(!Fahrenheit.blocks_cfg.isEmpty()) {
                Map<Identifier, Map<String, float[]>> tempMap = Fahrenheit.blocks_cfg;
                for (BlockPos pos : posIterable) {
                    Identifier id = Registry.BLOCK.getId(world.getBlockState(pos).getBlock());
                    if(tempMap.containsKey(id)){
                        float tmp1 = call(tempMap.get(id), world.getBlockState(pos), player, pos);
                        tmp = tmp1 > tmp ? tmp + tmp1 : tmp;
                    }
                }
            }
            if(blWarm && this.temp < 4){
                tmp = 0.4F;
            }
            else if(blChill && this.temp > 6){
                tmp = -0.4F;
            }
            this.tempProgress += tmp;

            if(this.temp >= 10){
                this.waterProgress += 0.3F;
            }
            else {
                this.waterProgress += 0.10F;
            }

            ServerPlayerEntity serverPlayer = player.getServer().getPlayerManager().getPlayer(player.getUuid());
            CompoundTag data = new CompoundTag();
            data.putInt("temp", this.temp);
            data.putInt("water", this.water);
            if(serverPlayer != null) {
                ServerPlayNetworking.send(serverPlayer, Fahrenheit.craftID("sync_temp"), new PacketByteBuf(Unpooled.buffer()).writeCompoundTag(data));
            }
            long endTime = System.nanoTime();
            System.out.println("That took " + (endTime - startTime) + " nanos");
            System.out.println(this.temp +"/" + this.tempProgress);
            System.out.println(this.water);
        }
    }
    public void writeToTag(CompoundTag tag){

    }
    public void readFromTag(CompoundTag tag){

    }

    public float call(Map<String, float[]> map, BlockState state, PlayerEntity player, BlockPos pos){
        for(String p : map.keySet()){
            float [] values = map.get(p).length > 1 ? map.get(p) : new float[]{1.0F, 1.0F};
            if(p.equals("default")){
                return Math.sqrt(player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)) <= values[1] ? values[0] : values[0] / 2;
            } else if (state.get(BooleanProperty.of(p))){
                return Math.sqrt(player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)) <= values[1] ? values[0] : values[0] / 2;
            }
        }
        return 0.0F;
    };

    private boolean checkStoneNearby(PlayerEntity player, Iterable<BlockPos> iterable){
        int i = 0;
        for (BlockPos pos : iterable){
           if(player.world.getBlockState(pos).isIn(BlockTags.BASE_STONE_OVERWORLD) && Math.sqrt(player.squaredDistanceTo(pos.getX() + 0.5F, pos.getY() +0.5F, pos.getZ() +0.5F)) <= 2.5D){
               ++i;
           }
        }
        System.out.println(i);
        System.out.println(i>= 4);
        return i>= 4;
    }

    public void addWaterLevels(int water, int hydration){
        this.water = Math.min(this.water + water, 20);
        this.hydration = Math.min(this.hydration + hydration, 10);
    }

    public void addWaterLevels(int water){
        this.water = Math.min(this.water + water, 20);
    }
    public void addHydrationLevel(int level){
        this.hydration = Math.min(this.hydration + level, 10);
    }
    public void addWaterProgress(float waterProgress){
        this.waterProgress += Math.max(waterProgress, 0);
    }

    public void addTempProgress(float temp){
        this.tempProgress += temp;
    }
    public void addTempLevel(int temp){
        this.temp += temp;
    }
    public int getTemp(){
        return this.temp;
    }
    public void setTemp(int temp){
        this.temp = temp;
    }
    public void setTempProgress(float progress){
        this.tempProgress = progress;
    }
}
