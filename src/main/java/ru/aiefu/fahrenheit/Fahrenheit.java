package ru.aiefu.fahrenheit;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import ru.aiefu.fahrenheit.statuseffects.*;

public class Fahrenheit implements ModInitializer {
	public static final String MOD_ID = "fahrenheit";

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

	@Override
	public void onInitialize() {
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
	}
	public static Identifier craftID(String id){
		return new Identifier(MOD_ID, id);
	}
}
