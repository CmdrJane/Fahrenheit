package ru.aiefu.fahrenheit.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.aiefu.fahrenheit.EnvironmentManager;
import ru.aiefu.fahrenheit.IPlayerMixins;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixins implements IPlayerMixins {
	private EnvironmentManager enviroManager = new EnvironmentManager();

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "net/minecraft/entity/player/HungerManager.update(Lnet/minecraft/entity/player/PlayerEntity;)V"))
	private void tickEnvironmentManager(CallbackInfo info) {
		this.enviroManager.tick((PlayerEntity) (Object) this);
	}
	public EnvironmentManager getEnviroManager(){
		return this.enviroManager;
	}
}
