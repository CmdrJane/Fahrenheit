package ru.aiefu.fahrenheit.items;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import ru.aiefu.fahrenheit.Fahrenheit;

public class MagmaShard extends TrinketItem {
    public MagmaShard(Settings settings) {
        super(settings);
    }
    @Override
    public boolean canWearInSlot(String group, String slot) {
        return group.equals(SlotGroups.LEGS) && slot.equals(Slots.BELT);
    }

    /**
     * Called to render the trinket
     *
     * @param slot           The {@code group:slot} structured slot the trinket is being rendered in
     * @param matrixStack
     * @param vertexConsumer
     * @param light
     * @param model
     * @param player
     * @param headYaw
     * @param headPitch
     * @see {@link #translateToFace(MatrixStack, PlayerEntityModel, AbstractClientPlayerEntity, float, float)}
     * @see {@link #translateToChest(MatrixStack, PlayerEntityModel, AbstractClientPlayerEntity, float, float)}
     * @see {@link #translateToRightArm(MatrixStack, PlayerEntityModel, AbstractClientPlayerEntity, float, float)}
     * @see {@link #translateToLeftArm(MatrixStack, PlayerEntityModel, AbstractClientPlayerEntity, float, float)}
     * @see {@link #translateToRightLeg(MatrixStack, PlayerEntityModel, AbstractClientPlayerEntity, float, float)}
     * @see {@link #translateToLeftLeg(MatrixStack, PlayerEntityModel, AbstractClientPlayerEntity, float, float)}
     */
    @Override
    public void render(String slot, MatrixStack matrixStack, VertexConsumerProvider vertexConsumer, int light, PlayerEntityModel<AbstractClientPlayerEntity> model, AbstractClientPlayerEntity player, float headYaw, float headPitch) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack stack = new ItemStack(Fahrenheit.MAGMA_SHARD);
        matrixStack.scale(0.4F, 0.4F, 0.4F);
        matrixStack.translate(-0.3D, 1.55D, -0.35D);
        itemRenderer.renderItem(stack, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumer);
    }

    @Override
    public void tick(PlayerEntity player, ItemStack stack) {
        player.addStatusEffect(new StatusEffectInstance(Fahrenheit.WARM_EFFECT, 15));
    }
}
