package ru.aiefu.fahrenheit;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
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
	public static final StatusEffect COLD_EFFECT = new ColdEffect();
	public static final StatusEffect HEAT_EFFECT = new HeatEffect();
	public static final StatusEffect DEADLY_COLD_EFFECT = new DeadlyColdEffect();
	public static final StatusEffect DEADLY_HEAT_EFFECT = new DeadlyHeatEffect();

	@Override
	public void onInitialize() {
		Registry.register(Registry.STATUS_EFFECT, craftID("warm"), WARM_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("chill"), CHILL_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("wet"), WET_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("heat_stroke"), HEAT_STROKE);
		Registry.register(Registry.STATUS_EFFECT, craftID("hypothermia"), HYPOTHERMIA);
		Registry.register(Registry.STATUS_EFFECT, craftID("cold"), COLD_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("heat"), HEAT_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("deadly_cold"), DEADLY_COLD_EFFECT);
		Registry.register(Registry.STATUS_EFFECT, craftID("deadly_heat"), DEADLY_HEAT_EFFECT);
	}
	public Identifier craftID(String id){
		return new Identifier(MOD_ID, id);
	}
}
