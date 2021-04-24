package ru.aiefu.fahrenheit;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.aiefu.fahrenheit.commands.FahrenheitReloadCfg;
import ru.aiefu.fahrenheit.commands.GetDistanceTo;
import ru.aiefu.fahrenheit.items.drinks.WaterFlaskItem;
import ru.aiefu.fahrenheit.mixin.SPIManagerMixinsAcc;
import ru.aiefu.fahrenheit.statuseffects.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Fahrenheit implements ModInitializer {
	public static final String MOD_ID = "fahrenheit";
	public static ConfigInstance config_instance;
	public static Map<Identifier, Map<String, BlockDataStorage>> blocks_cfg = new HashMap<>();
	public static HashMap<Identifier, BiomeDataStorage> biomeDataMap = new HashMap<>();
	public static DefaultDataStorage defaultDataStorage;

	//Items
	public static final Item WATER_FLASK = new WaterFlaskItem(new FabricItemSettings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().alwaysEdible().hunger(0).build()).maxCount(1), 400);
	public static final Item METAL_WATER_FLASK = new WaterFlaskItem(new FabricItemSettings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().alwaysEdible().hunger(0).build()).maxCount(1), 800);

	//StatusEffects
	public static final StatusEffect WARM_EFFECT = new WarmEffect();
	public static final StatusEffect CHILL_EFFECT = new ChillEffect();
	public static final StatusEffect WET_EFFECT = new WetEffect();
	public static final StatusEffect HEAT_STROKE = new HeatStrokeEffect();
	public static final StatusEffect HYPOTHERMIA = new Hypothermia();
	public static final StatusEffect SATURATION_EFFECT = new SaturationEffect();
	public static final StatusEffect REFRESHIN_EFFECT = new RefreshingEffect();
	public static final StatusEffect DEADLY_COLD_EFFECT = new DeadlyColdEffect();
	public static final StatusEffect DEADLY_HEAT_EFFECT = new DeadlyHeatEffect();
	public static final StatusEffect THIN_AIR = new ThinAirEffect();
	//DamageSources
	public static final DamageSource OUT_OF_AIR = new DamageSourcesCustom("out_or_air_source").setBypassesArmor();
	public static final DamageSource HYPOTHERMIA_DMG = new DamageSourcesCustom("hypothermia_source").setBypassesArmor();
	public static final DamageSource HEAT_STROKE_DMG = new DamageSourcesCustom("heat_stroke_source").setBypassesArmor();
	public static final DamageSource DEADLY_HEAT_DMG = new DamageSourcesCustom("deadly_heat_source").setBypassesArmor();
	public static final DamageSource DEADLY_COLD_DMG = new DamageSourcesCustom("deadly_cold_source").setBypassesArmor();
	public static final DamageSource DEHYDRATION = new DamageSourcesCustom("dehydration_source").setBypassesArmor();

	@Override
	public void onInitialize() {
		craftPaths();
		Registry.register(Registry.STATUS_EFFECT, craftID("warm"), WARM_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("chill"), CHILL_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("wet"), WET_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("heat_stroke"), HEAT_STROKE);
		Registry.register(Registry.STATUS_EFFECT, craftID("hypothermia"), HYPOTHERMIA);
		Registry.register(Registry.STATUS_EFFECT, craftID("saturation"), SATURATION_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("refreshing"), REFRESHIN_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("deadly_cold"), DEADLY_COLD_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("deadly_heat"), DEADLY_HEAT_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("thin_air"), THIN_AIR);
		Registry.register(Registry.ITEM, craftID("water_flask"), WATER_FLASK);
		Registry.register(Registry.ITEM, craftID("metal_water_flask"), METAL_WATER_FLASK);
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			GetDistanceTo.register(dispatcher);
			FahrenheitReloadCfg.register(dispatcher);
		});
		serverSidePackets();
	}
	public static Identifier craftID(String id){
		return new Identifier(MOD_ID, id);
	}
	public void craftPaths(){
		try{
			if(!Files.isDirectory(Paths.get("./config"))){
				Files.createDirectory(Paths.get("./config"));
			}
			if(!Files.isDirectory(Paths.get("./config/fahrenheit"))){
				Files.createDirectory(Paths.get("./config/fahrenheit"));
			}
			IOManager ioManager = new IOManager();
			if(!Files.exists(Paths.get("./config/fahrenheit/config.json"))){
				ioManager.genDefaultCfg();
			}
			if(!Files.exists(Paths.get("./config/fahrenheit/biomes-by-temp-category-config.json"))){
				ioManager.genDefaultDataStorage();
			}
			if(!Files.exists(Paths.get("./config/fahrenheit/blocks-data.json"))){
				ioManager.genBlocksCfg();
			}
			if(!Files.exists(Paths.get("./config/fahrenheit/biome-config.json"))){
				ioManager.genBiomeCfg();
			}
			ioManager.readConfig();
			ioManager.readDefaultDataStorage();
			ioManager.readBlocksCfg();
			ioManager.readBiomeCfg();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	public void serverSidePackets(){
		ServerPlayNetworking.registerGlobalReceiver(craftID("drink_from_water_block"), (server, player, handler, buf, responseSender) -> {
			BlockHitResult raycast = (BlockHitResult) player.raycast(2.D, 0.0F, true);
			if(!((SPIManagerMixinsAcc)player.interactionManager).isMining() && raycast.getType() == HitResult.Type.BLOCK && player.world.getBlockState(raycast.getBlockPos()).getBlock() == Blocks.WATER){
				EnvironmentManager enviroManager = ((IPlayerMixins)player).getEnviroManager();
				enviroManager.addWaterLevels(2, 1);
				if(enviroManager.getTemp() > 10) {
					enviroManager.addTempLevel(-1);
				}
				sendSyncPacket(player, enviroManager.getTemp(), enviroManager.getWater());
				player.world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
			}
		});
	}
	public static void sendSyncPacket(@Nullable ServerPlayerEntity player, int temp, int water){
		if(player != null) {
			ServerPlayNetworking.send(player, Fahrenheit.craftID("sync_temp"), new PacketByteBuf(Unpooled.buffer().writeInt(temp).writeInt(water)));
		}
	}
}
