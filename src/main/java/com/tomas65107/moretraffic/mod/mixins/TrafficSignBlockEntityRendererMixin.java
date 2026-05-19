package com.tomas65107.moretraffic.mod.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tomas65107.moretraffic.rendering.MaterialValues;
import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.trafficcraft.block.TrafficSignBlock;
import de.mrjulsen.trafficcraft.block.data.TrafficSignShape;
import de.mrjulsen.trafficcraft.block.entity.TrafficSignBlockEntity;
import de.mrjulsen.trafficcraft.client.ber.TrafficSignBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrafficSignBlockEntityRenderer.class)
public class TrafficSignBlockEntityRendererMixin {

    @Inject(
            method = "renderBlock",
            at = @At("HEAD")
    )
    private void moretraffic$renderExtraModel(BERGraphics<TrafficSignBlockEntity> graphics, float partialTick, CallbackInfo ci) {
        TrafficSignBlockEntity entity = graphics.blockEntity();
        if (entity == null || entity.getLevel() == null) return;
        BlockState state = entity.getBlockState();

        float offset = ((TrafficSignBlockEntityMixinAccessor)(Object) entity).moretraffic$getVisualOffset();

        graphics.poseStack().translate(0, 0, offset);

        graphics.poseStack().pushPose();

        graphics.poseStack().scale(16.0F, 16.0F, -16.0F);
        graphics.poseStack().translate(0, 0, -1);

        Direction facing = state.getValue(TrafficSignBlock.FACING);
        switch (facing) {
            case SOUTH -> {
                graphics.poseStack().translate(1, 0, 1);
                graphics.poseStack().mulPose(Axis.YP.rotationDegrees(180.0F));
            }
            case WEST -> {
                graphics.poseStack().translate(1, 0, 0);
                graphics.poseStack().mulPose(Axis.YP.rotationDegrees(-90.0F));
            }
            case EAST -> {
                graphics.poseStack().translate(0, 0, 1);
                graphics.poseStack().mulPose(Axis.YP.rotationDegrees(90.0F));
            }
        }

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                graphics.poseStack().last(),
                graphics.multiBufferSource().getBuffer(RenderType.cutout()),
                state,
                Minecraft.getInstance().getBlockRenderer().getBlockModel(state),
                1.0f,
                1.0f,
                1.0f,
                LevelRenderer.getLightColor(entity.getLevel(), entity.getBlockPos()),
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                RenderType.solid()
        );

        graphics.poseStack().popPose();
    }

    private static int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos) - 7;
        int sLight = level.getBrightness(LightLayer.SKY, pos) - 7;
        return LightTexture.pack(bLight, sLight);
    }
}