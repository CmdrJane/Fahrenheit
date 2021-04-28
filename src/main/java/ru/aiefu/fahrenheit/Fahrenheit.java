package ru.aiefu.fahrenheit;

import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketSlots;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.loot.LootTable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.potion.Potion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.jetbrains.annotations.Nullable;
import ru.aiefu.fahrenheit.blocks.FermenterBlock;
import ru.aiefu.fahrenheit.blocks.FermenterEntity;
import ru.aiefu.fahrenheit.commands.DebugCommand;
import ru.aiefu.fahrenheit.commands.FahrenheitReloadCfg;
import ru.aiefu.fahrenheit.commands.GetDistanceTo;
import ru.aiefu.fahrenheit.items.FireMixItem;
import ru.aiefu.fahrenheit.items.IceMixItem;
import ru.aiefu.fahrenheit.items.MagmaShard;
import ru.aiefu.fahrenheit.items.IceCube;
import ru.aiefu.fahrenheit.items.drinks.ItemDrinkable;
import ru.aiefu.fahrenheit.items.drinks.WaterFlaskItem;
import ru.aiefu.fahrenheit.items.food.IceCream;
import ru.aiefu.fahrenheit.statuseffects.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Fahrenheit implements ModInitializer {
	public static final String MOD_ID = "fahrenheit";
	public static ConfigInstance config_instance;
	public static Map<Identifier, Map<String, BlockDataStorage>> blocks_cfg = new HashMap<>();
	public static HashMap<Identifier, BiomeDataStorage> biomeDataMap = new HashMap<>();
	public static DefaultDataStorage defaultDataStorage;

	//StatusEffects
	public static final StatusEffect WARM_EFFECT = new WarmEffect();
	public static final StatusEffect CHILL_EFFECT = new ChillEffect();
	public static final StatusEffect WET_EFFECT = new WetEffect();
	public static final StatusEffect HEAT_STROKE = new HeatStrokeEffect();
	public static final StatusEffect HYPOTHERMIA = new Hypothermia();
	public static final StatusEffect SATURATION_EFFECT = new SaturationEffect();
	public static final StatusEffect REFRESHING_EFFECT = new RefreshingEffect();
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
	//Items
	public static final Item WATER_FLASK = new WaterFlaskItem(new FabricItemSettings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().alwaysEdible().hunger(0).build()).maxCount(1), 400);
	public static final Item METAL_WATER_FLASK = new WaterFlaskItem(new FabricItemSettings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().alwaysEdible().hunger(0).build()).maxCount(1), 800);
	public static final Item APPLE_JUICE_BOTTLE = new ItemDrinkable(new FabricItemSettings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().alwaysEdible().hunger(0).build()).maxCount(16),3,4,9,-2, new HashMapOf<>(REFRESHING_EFFECT, new int[]{1200, 0}), false);
	public static final Item MELON_JUICE_BOTTLE = new ItemDrinkable(new FabricItemSettings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().alwaysEdible().hunger(0).build()).maxCount(16),5,6,9,-3, new HashMapOf<>(REFRESHING_EFFECT, new int[]{2600, 0}), false);
	public static final Item BERRIES_JUICE_BOTTLE = new ItemDrinkable(new FabricItemSettings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().alwaysEdible().hunger(1).build()).maxCount(16),4,5,6,-4, new HashMapOf<>(REFRESHING_EFFECT, new int[]{1800, 0}, StatusEffects.JUMP_BOOST, new int[]{400, 2}), false);
	public static final Item CARROT_JUICE_BOTTLE = new ItemDrinkable(new FabricItemSettings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().alwaysEdible().hunger(1).build()).maxCount(16),3,4,8,-3, new HashMapOf<>(REFRESHING_EFFECT, new int[]{1500, 0}, StatusEffects.NIGHT_VISION, new int[]{800, 0}),false);
	public static final Item APPLE_CIDER_BOTTLE = new ItemDrinkable(new FabricItemSettings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().hunger(0).alwaysEdible().build()).maxCount(16), 2, 2, -4, 4, null, true);
	public static final Item WAFFLE = new Item(new FabricItemSettings().group(ItemGroup.MISC).food(new FoodComponent.Builder().hunger(5).saturationModifier(0.9F).build()).maxCount(16));
	public static final Item ICE_MIX = new IceMixItem(new FabricItemSettings().group(ItemGroup.BREWING).food(new FoodComponent.Builder().hunger(0).alwaysEdible().build()).maxCount(8));
	public static final Item FIRE_MIX = new FireMixItem(new FabricItemSettings().group(ItemGroup.BREWING).food(new FoodComponent.Builder().hunger(0).alwaysEdible().build()).maxCount(8));
	public static final Item JUICER = new Item(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));
	public static final Item MIXER = new Item(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));
	public static final Item ICE_CREAM = new IceCream(new FabricItemSettings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().hunger(2).alwaysEdible().build()).maxCount(16),-2);
	public static final Item ICE_CUBE = new IceCube(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
	public static final Item MAGMA_SHARD = new MagmaShard(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
	//Blocks
	//public static final FermenterBlock FERMENTER_BLOCK = new FermenterBlock(FabricBlockSettings.of(Material.WOOD).strength(4.0f));
	//BlockItems
	//public static final BlockItem FERMENTER_ITEM = new BlockItem(FERMENTER_BLOCK, new FabricItemSettings().group(ItemGroup.MISC));
	//BlockEntities
	//public static final BlockEntityType<FermenterEntity> FERMENTER_BLOCK_ENTITY = BlockEntityType.Builder.create(FermenterEntity::new, FERMENTER_BLOCK).build(null);

	@Override
	public void onInitialize() {
		craftPaths();
		//Registry.register(Registry.BLOCK_ENTITY_TYPE, craftID("fermenter"), FERMENTER_BLOCK_ENTITY);
		Registry.register(Registry.STATUS_EFFECT, craftID("warm"), WARM_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("chill"), CHILL_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("wet"), WET_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("heat_stroke"), HEAT_STROKE);
		Registry.register(Registry.STATUS_EFFECT, craftID("hypothermia"), HYPOTHERMIA);
		Registry.register(Registry.STATUS_EFFECT, craftID("saturation"), SATURATION_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("refreshing"), REFRESHING_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("deadly_cold"), DEADLY_COLD_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("deadly_heat"), DEADLY_HEAT_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("thin_air"), THIN_AIR);
		Registry.register(Registry.ITEM, craftID("water_flask"), WATER_FLASK);
		Registry.register(Registry.ITEM, craftID("metal_water_flask"), METAL_WATER_FLASK);
		Registry.register(Registry.ITEM, craftID("apple_juice_bottle"), APPLE_JUICE_BOTTLE);
		Registry.register(Registry.ITEM, craftID("melon_juice_bottle"), MELON_JUICE_BOTTLE);
		Registry.register(Registry.ITEM, craftID("berries_juice_bottle"), BERRIES_JUICE_BOTTLE);
		Registry.register(Registry.ITEM, craftID("carrot_juice_bottle"), CARROT_JUICE_BOTTLE);
		Registry.register(Registry.ITEM, craftID("apple_cider_bottle"), APPLE_CIDER_BOTTLE);
		Registry.register(Registry.ITEM, craftID("waffle"), WAFFLE);
		Registry.register(Registry.ITEM, craftID("ice_cream"), ICE_CREAM);
		Registry.register(Registry.ITEM, craftID("ice_cube"), ICE_CUBE);
		Registry.register(Registry.ITEM, craftID("magma_shard"), MAGMA_SHARD);
		Registry.register(Registry.ITEM, craftID("juicer"), JUICER);
		Registry.register(Registry.ITEM, craftID("mixer"), MIXER);
		Registry.register(Registry.ITEM, craftID("ice_mix"), ICE_MIX);
		Registry.register(Registry.ITEM, craftID("fire_mix"), FIRE_MIX);
		//Registry.register(Registry.ITEM, craftID("fermenter"), FERMENTER_ITEM);
		TrinketSlots.addSlot(SlotGroups.LEGS, Slots.BELT, new Identifier("trinkets", "textures/item/empty_trinket_slot_belt.png"));
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			GetDistanceTo.register(dispatcher);
			FahrenheitReloadCfg.register(dispatcher);
			DebugCommand.register(dispatcher);
		});
		addTradeOffers();
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
			EnvironmentManager enviroManager = ((IPlayerMixins)player).getEnviroManager();
			if(enviroManager.getWater() < 20 && raycast.getType() == HitResult.Type.BLOCK && player.world.getBlockState(raycast.getBlockPos()).getBlock() == Blocks.WATER){
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
	public void addTradeOffers(){
		TradeOffers.Factory[] original = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.ARMORER).get(5);
		TradeOffers.Factory[] copy = Arrays.copyOf(original, original.length + 2);
		copy[original.length] = (entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 32), new ItemStack(Items.PACKED_ICE, 64), new ItemStack(ICE_CUBE),1,1, 1.0F);
		copy[original.length + 1] = (entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 32), new ItemStack(Items.MAGMA_BLOCK, 64), new ItemStack(MAGMA_SHARD),1,1, 1.0F);
		TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.ARMORER).put(5, copy);
		TradeOffers.Factory[] originalCleric = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.CLERIC).get(1);
		TradeOffers.Factory[] copy1 = Arrays.copyOf(originalCleric, originalCleric.length + 1);
		copy1[originalCleric.length] = (entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(APPLE_CIDER_BOTTLE, 4), 32,1, 1.0F);
		TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.CLERIC).put(1, copy1);
	}
	public void addTradeOffer(VillagerProfession profession, int tier, ItemStack input, ItemStack output, int maxUses, int rewardExp, float priceMultiplier){
		TradeOffers.Factory[] original = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(profession).get(tier);
		TradeOffers.Factory[] copy = Arrays.copyOf(original, original.length + 1);
		copy[original.length] = (entity, random) -> new TradeOffer(input, output, maxUses, rewardExp, priceMultiplier);
		TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(profession).put(tier, copy);
	}
	public void addTradeOffer(VillagerProfession profession, int tier, ItemStack input1, ItemStack input2, ItemStack output, int maxUses, int rewardExp, float priceMultiplier){
		TradeOffers.Factory[] original = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(profession).get(tier);
		TradeOffers.Factory[] copy = Arrays.copyOf(original, original.length + 1);
		copy[original.length] = (entity, random) -> new TradeOffer(input1, input2, output, maxUses, rewardExp, priceMultiplier);
		TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(profession).put(tier, copy);
	}
}
