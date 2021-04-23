package ru.aiefu.fahrenheit.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.aiefu.fahrenheit.EnvironmentManager;
import ru.aiefu.fahrenheit.IPlayerMixins;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixins extends Entity implements IPlayerMixins {
	private EnvironmentManager enviroManager = new EnvironmentManager();

	public PlayerEntityMixins(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "net/minecraft/entity/player/HungerManager.update(Lnet/minecraft/entity/player/PlayerEntity;)V", shift = At.Shift.AFTER))
	private void tickEnvironmentManager(CallbackInfo info) {
		this.enviroManager.tick((PlayerEntity) (Object) this);
		if(this.isSprinting()){
			this.enviroManager.addWaterProgress(0.005F);
		}
	}
	public EnvironmentManager getEnviroManager(){
		return this.enviroManager;
	}
	@Inject(method = "writeCustomDataToTag", at = @At("TAIL"))
	private void writeEnviroManager(CompoundTag tag, CallbackInfo ci){
		enviroManager.writeToTag(tag);
	}
	@Inject(method = "readCustomDataFromTag", at = @At("TAIL"))
	private void readEnviroManager(CompoundTag tag, CallbackInfo ci){
		enviroManager.readFromTag(tag);
	}
}
