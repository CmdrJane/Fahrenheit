package ru.aiefu.fahrenheit.items.drinks;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.aiefu.fahrenheit.EnvironmentManager;
import ru.aiefu.fahrenheit.Fahrenheit;
import ru.aiefu.fahrenheit.IPlayerMixins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDrinkable extends Item {
    protected int waterLevel;
    protected int hydration;
    protected int tempThreshold;
    protected int tempLevel;
    protected HashMap<StatusEffect, int[]> effects;
    protected boolean shouldInverse;
    public ItemDrinkable(Settings settings, int waterLevel, int hydration, int tempThreshold, int tempLevel, @Nullable HashMap<StatusEffect, int[]> effects, boolean shouldInverse) {
        super(settings);
        this.waterLevel = waterLevel;
        this.hydration = hydration;
        this.tempThreshold = tempThreshold;
        this.tempLevel = tempLevel;
        this.effects = effects;
        this.shouldInverse = shouldInverse;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(((IPlayerMixins)user).getEnviroManager().getWater() < 20)
        return super.use(world, user, hand);
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(user instanceof ServerPlayerEntity){
            ServerPlayerEntity player = (ServerPlayerEntity) user;
            EnvironmentManager manager = ((IPlayerMixins) user).getEnviroManager();
            manager.addWaterLevels(this.waterLevel, this.hydration);
            if(!shouldInverse && manager.getTemp() >= this.tempThreshold) {
                manager.addTempLevel(tempLevel);
            }
            else if(shouldInverse && manager.getTemp() <= this.tempThreshold){
                manager.addTempLevel(tempLevel);
            }
            stack.decrement(1);
            if(!player.inventory.insertStack(new ItemStack(Items.GLASS_BOTTLE))){
                player.dropItem(new ItemStack(Items.GLASS_BOTTLE), false);
            }
            if(!user.world.isClient) {
                if(effects != null && !effects.isEmpty()){
                    for(Map.Entry<StatusEffect, int[]> e : this.effects.entrySet()){
                        user.addStatusEffect(new StatusEffectInstance(e.getKey(), e.getValue()[0], e.getValue()[1]));
                    }
                }
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

    @Override
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
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(effects != null){
            for(StatusEffect e : effects.keySet()){
                tooltip.add(new LiteralText(e.getName().getString()).formatted(Formatting.BLUE));
            }
        }
    }
}
