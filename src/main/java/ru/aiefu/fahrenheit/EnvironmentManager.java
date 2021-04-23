package ru.aiefu.fahrenheit;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

import java.util.Map;

public class EnvironmentManager {
    private static int precision = Fahrenheit.config_instance.precision > 0 ? Fahrenheit.config_instance.precision : 1;
    private int temp = 0;
    private float tempProgress = 0;
    private int tickTimer = 0;
    private int thirstTimer = 0;
    private int water = 20;
    private int hydration = 0;
    private float waterProgress = 0;

    public void tick(PlayerEntity player){
        ++tickTimer;

        if(this.tempProgress >= 8.0F){
            this.tempProgress = 0;
            this.temp = Math.min(this.temp + 1, 20);
        }
        else if (this.tempProgress <= -8.0F){
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
        if(this.thirstTimer >= 80 && Fahrenheit.config_instance.enableThirst){
            this.thirstTimer = 0;
            player.damage(Fahrenheit.DEHYDRATION, 1.0F);
        }

        if(tickTimer >= precision && Fahrenheit.config_instance.enableTemperature && !player.isCreative() && !player.isSpectator()) {
            long startTime = System.nanoTime();
            tickTimer = 0;

            World world = player.world;
            BlockPos playerPos = player.getBlockPos();
            Iterable<BlockPos> posIterable = BlockPos.iterateOutwards(playerPos, 3,3,3);
            float biomeTemp = world.getBiome(playerPos).getTemperature();
            Registry<DimensionType> dimTypeReg = world.getRegistryManager().getDimensionTypes();
            DimensionType playerDim = world.getDimension();
            boolean blChill = player.hasStatusEffect(Fahrenheit.CHILL_EFFECT);
            boolean blWarm = player.hasStatusEffect(Fahrenheit.WARM_EFFECT);
            boolean isWet = player.isWet();
            boolean isDay = world.isDay();

            if(!Fahrenheit.config_instance.disableDeadlyHeat && playerDim == dimTypeReg.get(DimensionType.THE_NETHER_REGISTRY_KEY) && !blChill){
                this.tempProgress += 8.0F;
                player.addStatusEffect(new StatusEffectInstance(Fahrenheit.DEADLY_HEAT_EFFECT, 15));
            }
            else if(!Fahrenheit.config_instance.disableDeadlyCold && playerDim == dimTypeReg.get(DimensionType.THE_END_REGISTRY_KEY) && !blWarm){
                this.tempProgress -= 8.0F;
                player.addStatusEffect(new StatusEffectInstance(Fahrenheit.DEADLY_COLD_EFFECT, 15));
            }
            else if(this.temp >= 20 && !blChill){
                player.addStatusEffect(new StatusEffectInstance(Fahrenheit.HEAT_STROKE, 15));
            }
            else if(this.temp <= -20 && !blWarm){
                player.addStatusEffect(new StatusEffectInstance(Fahrenheit.HYPOTHERMIA, 15));
            }

            float tmp = 0;
            switch (Fahrenheit.config_instance.operationMode){
                case 0:
                    if( biomeTemp <= 0.35F){
                        tmp = isDay ? Fahrenheit.defaultDataStorage.VERY_COLD_BIOMES.dayTemp * precision : Fahrenheit.defaultDataStorage.VERY_COLD_BIOMES.nightTemp * precision;
                    }
                    else if (biomeTemp > 0.35F && biomeTemp < 0.6F){
                        tmp = isDay ? Fahrenheit.defaultDataStorage.COLD_BIOMES.dayTemp * precision : Fahrenheit.defaultDataStorage.COLD_BIOMES.nightTemp * precision;
                    }
                    else if (biomeTemp < 0.8F && biomeTemp >= 0.6F){
                        tmp = isDay ? Fahrenheit.defaultDataStorage.MEDIUM_BIOMES.dayTemp * precision : Fahrenheit.defaultDataStorage.MEDIUM_BIOMES.nightTemp * precision;
                    }
                    else if (biomeTemp >= 0.8F && biomeTemp < 1.5F){
                        tmp = isDay ? Fahrenheit.defaultDataStorage.HOT_BIOMES.dayTemp * precision : Fahrenheit.defaultDataStorage.HOT_BIOMES.nightTemp * precision;
                    }
                    else if (biomeTemp >= 1.5F){
                        tmp = isDay ? Fahrenheit.defaultDataStorage.VERY_HOT_BIOMES.dayTemp * precision : Fahrenheit.defaultDataStorage.VERY_HOT_BIOMES.nightTemp * precision;
                        if(world.getBiome(playerPos).getCategory().equals(Biome.Category.DESERT)){
                            if(!world.isDay()) {
                                tmp = -Fahrenheit.defaultDataStorage.VERY_HOT_BIOMES.dayTemp * precision;
                            }
                        }
                    }
                    break;
                case 1:
                  MutableRegistry<Biome> biomeRegisrty = world.getRegistryManager().get(Registry.BIOME_KEY);
                  Biome playerBiome = world.getBiome(playerPos);
                    tmp = 0.005F * precision;
                    for(Identifier id : Fahrenheit.biomeDataMap.keySet()){
                        if(playerBiome == biomeRegisrty.get(id)){
                            tmp = world.isDay() ? Fahrenheit.biomeDataMap.get(id).dayTemp * precision : Fahrenheit.biomeDataMap.get(id).nightTemp * precision;
                            break;
                        }
                    }
                    break;
                //case 2:

            }
            if(playerPos.getY() < player.world.getSeaLevel() - 6 || (!world.isSkyVisible(playerPos.add(0, 0.7, 0)) && checkStoneNearby(player, BlockPos.iterateOutwards(playerPos, 1,2,1)))){
                tmp = -0.015F * precision;
            }

            if (isWet && !blWarm) {
                player.addStatusEffect(new StatusEffectInstance(Fahrenheit.WET_EFFECT, 120));
                System.out.println(Fahrenheit.config_instance.wetTemp * precision);
                tmp = Fahrenheit.config_instance.waterFlatChill ? Fahrenheit.config_instance.wetTemp * precision : tmp - Fahrenheit.config_instance.wetTemp * precision;
            }
            if(player.getEyeY() >= Fahrenheit.config_instance.thinAirThreshold && !Fahrenheit.config_instance.disableThinAir){
                player.addStatusEffect(new StatusEffectInstance(Fahrenheit.THIN_AIR, 15));
            }
            if(!Fahrenheit.blocks_cfg.isEmpty()) {
                Map<Identifier, Map<String, BlockDataStorage>> tempMap = Fahrenheit.blocks_cfg;
                for (BlockPos pos : posIterable) {
                    Identifier id = Registry.BLOCK.getId(world.getBlockState(pos).getBlock());
                    if(tempMap.containsKey(id)){
                        float tmp1 = call(tempMap.get(id), world.getBlockState(pos), player, pos) * precision;
                        if(tmp1 > 0){
                            tmp = tmp1 > tmp ? tmp1 + tmp : tmp;
                        }
                        else if(tmp1 < 0){
                            tmp = tmp1 < tmp ? tmp1 + tmp : tmp;
                        }
                    }
                }
            }
            if(blWarm && this.temp < 4){
                tmp = 0.04F * precision;
            }
            else if(blChill && this.temp > 6){
                tmp = -0.04F * precision;
            }
            this.tempProgress += tmp;
            if(Fahrenheit.config_instance.enableThirst) {
                if (this.temp >= 10) {
                    this.waterProgress += 0.025F * precision;
                } else {
                    this.waterProgress += 0.005F * precision;
                }
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
        CompoundTag fh_tag = new CompoundTag();
        fh_tag.putInt("temperature", this.temp);
        fh_tag.putInt("water", this.water);
        fh_tag.putInt("hydration", this.hydration);
        fh_tag.putInt("thirstTimer", this.thirstTimer);
        fh_tag.putFloat("tempProgress", this.tempProgress);
        fh_tag.putFloat("waterProgress", this.waterProgress);
        tag.put("Fahrenheit_Data", fh_tag);
    }
    public void readFromTag(CompoundTag tag){
        CompoundTag fh_tag = (CompoundTag) tag.get("Fahrenheit_Data");
        if(fh_tag != null) {
            this.temp = fh_tag.getInt("temperature");
            this.water = fh_tag.getInt("water");
            this.hydration = fh_tag.getInt("hydration");
            this.thirstTimer = fh_tag.getInt("thirstTimer");
            this.tempProgress = fh_tag.getFloat("tempProgress");
            this.waterProgress = fh_tag.getFloat("waterProgress");
        }
    }

    public float call(Map<String, BlockDataStorage> map, BlockState state, PlayerEntity player, BlockPos pos){
        float distance = (float) Math.sqrt(player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
        for(Map.Entry<String, BlockDataStorage> e : map.entrySet()){
            if (!e.getKey().equals("default") && state.get(BooleanProperty.of(e.getKey()))){
                return  distance <= e.getValue().closeRange ? e.getValue().closeRangeTemp : e.getValue().longRangeTemp;
            }
        }
        if(map.containsKey("default")){
            BlockDataStorage storage = map.get("default");
            return distance <= storage.closeRange ? storage.closeRangeTemp : storage.longRangeTemp;
        }
        return 0.0F;
    }

    private boolean checkStoneNearby(PlayerEntity player, Iterable<BlockPos> iterable){
        int i = 0;
        for (BlockPos pos : iterable){
           if(player.world.getBlockState(pos).isIn(BlockTags.BASE_STONE_OVERWORLD)){
               ++i;
           }
           if(i > 4){
               break;
           }
        }
        //System.out.println(i);
        //System.out.println(i>= 4);
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
        this.tempProgress = MathHelper.clamp(this.tempProgress + temp, -8.0F, 8.0F);
    }
    public void addTempLevel(int temp){
        this.temp = MathHelper.clamp(this.temp + temp, -20, 20);
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
