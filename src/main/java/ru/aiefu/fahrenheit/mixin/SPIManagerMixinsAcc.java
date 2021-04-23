package ru.aiefu.fahrenheit.mixin;

import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerInteractionManager.class)
public interface SPIManagerMixinsAcc {
    @Accessor("mining")
    boolean isMining();
}
