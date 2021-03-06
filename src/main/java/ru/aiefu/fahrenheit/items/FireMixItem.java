package ru.aiefu.fahrenheit.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.aiefu.fahrenheit.Fahrenheit;

import java.util.List;

public class FireMixItem extends Item {
    public FireMixItem(Settings settings) {
        super(settings);
    }
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(!world.isClient && user instanceof ServerPlayerEntity){
            ServerPlayerEntity player = (ServerPlayerEntity) user;
            if(player.hasStatusEffect(Fahrenheit.CHILL_EFFECT)){
                player.removeStatusEffect(Fahrenheit.CHILL_EFFECT);
            }
            player.addStatusEffect(new StatusEffectInstance(Fahrenheit.WARM_EFFECT, 4800));
            stack.decrement(1);
            if(!player.inventory.insertStack(new ItemStack(Items.GLASS_BOTTLE))){
                player.dropItem(new ItemStack(Items.GLASS_BOTTLE), false);
            }
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
        tooltip.add(new TranslatableText("description.fahrenheit.fire_mix"));
    }
}
