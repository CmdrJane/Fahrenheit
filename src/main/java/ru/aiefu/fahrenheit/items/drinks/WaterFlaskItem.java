package ru.aiefu.fahrenheit.items.drinks;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import ru.aiefu.fahrenheit.IPlayerMixins;

public class WaterFlaskItem extends Item {
    public WaterFlaskItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();
        if(!tag.contains("water")){
            tag.putInt("water", 0);
        }
        int water = tag.getInt("water");
        System.out.println(water);
        BlockHitResult result = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
        if(result.getType() == HitResult.Type.BLOCK && world.getBlockState(result.getBlockPos()).getBlock() instanceof FluidDrainable && water < 400){
            tag.putInt("water", Math.min(water + 100, 400));
            return TypedActionResult.success(stack);
        }
        else if(water >= 100){
            return super.use(world, user, hand);
        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 60;
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

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        CompoundTag tag = stack.getOrCreateTag();
        int water = tag.getInt("water");
        if(water >= 100 && user instanceof IPlayerMixins){
            tag.putInt("water", Math.max(water - 100, 0));
            ((IPlayerMixins)user).getEnviroManager().addWaterLevels(3);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), this.getDrinkSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
        }
        return stack;
    }
}
