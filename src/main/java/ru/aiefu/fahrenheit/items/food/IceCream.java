package ru.aiefu.fahrenheit.items.food;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import ru.aiefu.fahrenheit.Fahrenheit;
import ru.aiefu.fahrenheit.IPlayerMixins;

public class IceCream extends Item {
    protected int temp;
    public IceCream(Settings settings, int temp) {
        super(settings);
        this.temp = temp;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(user instanceof ServerPlayerEntity && !user.world.isClient) {
            ((IPlayerMixins) user).getEnviroManager().addTempLevel(temp);
            user.addStatusEffect(new StatusEffectInstance(Fahrenheit.CHILL_EFFECT, 800));
        }
        return super.finishUsing(stack, world, user);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }
}
