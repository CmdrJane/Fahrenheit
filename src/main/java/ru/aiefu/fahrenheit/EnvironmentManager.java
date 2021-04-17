package ru.aiefu.fahrenheit;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentManager {
    private int temp = 0;
    private float tempProgress = 0;
    private int tickTimer = 0;
    private int water = 0;
    private int waterProgress = 0;

    public void tick(PlayerEntity player){
        ++tickTimer;

        if(this.tempProgress >= 6.0F){
            this.tempProgress = 0;
            this.temp = Math.min(this.temp += 1, 20);
        }
        if (this.tempProgress <= -6.0F){
            this.tempProgress = 0;
            this.temp = Math.max(this.temp -= 1, -20);
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
                this.tempProgress += 6.0F;
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
                if(world.isDay()){
                    tmp = 0.05F;
                }
                else {
                    tmp = -0.08F;
                }
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
            if(blWarm && this.temp < 4){
                tmp = 0.4F;
            }
            else if(blChill && this.temp > 6){
                tmp = -0.4F;
            }
            this.tempProgress += tmp;

            if (isWet && !blWarm) {
                player.addStatusEffect(new StatusEffectInstance(Fahrenheit.WET_EFFECT, 60));
                this.tempProgress -= 0.2F;
            }
            if(player.getEyeY() >= 170){
                player.addStatusEffect(new StatusEffectInstance(Fahrenheit.THIN_AIR, 15));
            }

            Map<Identifier, Runnable> blocks = new HashMap<>();
            blocks.put(new Identifier("minecraft:lava"), () -> {
                System.out.println("It's Worked");
                this.addTempProgress(0.7F);
            });
            for(BlockPos pos : posIterable){
                Identifier id = Registry.BLOCK.getId(player.world.getBlockState(pos).getBlock());
                if(world.getBlockState(pos).getBlock() == Blocks.FURNACE && world.getBlockState(pos).get(AbstractFurnaceBlock.LIT)){
                    this.tempProgress += 0.5F;
                }
                else if(blocks.containsKey(id)){
                    blocks.get(id).run();
                    break;
                }
            }

            ServerPlayerEntity serverPlayer = player.getServer().getPlayerManager().getPlayer(player.getUuid());
            if(serverPlayer != null) {
                ServerPlayNetworking.send(serverPlayer, Fahrenheit.craftID("sync_temp"), new PacketByteBuf(Unpooled.buffer().writeInt(this.temp)));
            }
            long endTime = System.nanoTime();
            System.out.println("That took " + (endTime - startTime) + " nanos");
            System.out.println(this.temp +"/" + this.tempProgress);
        }
    }
    public void writeToTag(CompoundTag tag){

    }
    public void readFromTag(CompoundTag tag){

    }

    private boolean checkStoneNearby(PlayerEntity player, Iterable<BlockPos> iterable){
        int i = 0;
        for (BlockPos pos : iterable){
           if(player.world.getBlockState(pos).isIn(BlockTags.BASE_STONE_OVERWORLD) && player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < 1.8D){
               ++i;
           }
        }
        System.out.println(i);
        System.out.println(i>= 4);
        return i >= 4;
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
