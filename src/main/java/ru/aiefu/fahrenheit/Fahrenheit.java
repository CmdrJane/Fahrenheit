package ru.aiefu.fahrenheit;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
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

	}
	public Identifier craftID(String id){
		return new Identifier(MOD_ID, id);
	}
}
