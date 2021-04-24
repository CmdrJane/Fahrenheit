package ru.aiefu.fahrenheit.items.drinks;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import ru.aiefu.fahrenheit.EnvironmentManager;
import ru.aiefu.fahrenheit.Fahrenheit;
import ru.aiefu.fahrenheit.IPlayerMixins;

public class ItemDrinkable extends Item {
    protected int waterLevel;
    protected int hydration;
    protected int tempThreshold;
    protected int tempLevel;
    public ItemDrinkable(Settings settings, int waterLevel, int hydration, int tempThreshold, int tempLevel ) {
        super(settings);
        this.waterLevel = waterLevel;
        this.hydration = hydration;
        this.tempThreshold = tempThreshold;
        this.tempLevel = tempLevel;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(user instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity) user;
            EnvironmentManager manager = ((IPlayerMixins) user).getEnviroManager();
            manager.addWaterLevels(this.waterLevel, this.hydration);
            if(manager.getTemp() >= this.tempThreshold) {
                manager.addTempLevel(tempLevel);
            }
            stack.decrement(1);
            if(!player.inventory.insertStack(new ItemStack(Items.GLASS_BOTTLE))){
                player.dropItem(new ItemStack(Items.GLASS_BOTTLE), false);
            }
            if(!user.world.isClient) {
                Fahrenheit.sendSyncPacket((ServerPlayerEntity) user, manager.getTemp(), manager.getWater());
            }
            world.playSound(null, user.getBlockPos(), this.getDrinkSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
        }
        return stack;
    }
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 30;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public SoundEvent getDrinkSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }

    @Override
    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }
}
