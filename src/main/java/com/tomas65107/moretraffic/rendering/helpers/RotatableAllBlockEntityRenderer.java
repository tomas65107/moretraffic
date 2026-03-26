package com.tomas65107.moretraffic.rendering.helpers;

import com.mojang.math.Axis;
import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.mcdragonlib.client.ber.SafeBlockEntityRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RotatableAllBlockEntityRenderer<T extends BlockEntity> extends SafeBlockEntityRenderer<T> {
    protected final Font font;

    public RotatableAllBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        this.font = context.getFont();
    }

    protected final void renderSafe(BERGraphics<T> graphics, float partialTick) {
        BlockState blockState = graphics.blockEntity().getBlockState();
        graphics.poseStack().pushPose();
        graphics.poseStack().translate((double)0.5F, (double)0.0F, (double)0.5F);
        Direction facing = blockState.getValue(DirectionalBlock.FACING);

        if (facing == Direction.UP) {
            graphics.poseStack().mulPose(Axis.XP.rotationDegrees(270));
            graphics.poseStack().translate(0.0D, -0.5D, 0.5D);
        } else if (facing == Direction.DOWN) {
            graphics.poseStack().mulPose(Axis.XP.rotationDegrees(90));
            graphics.poseStack().translate(0.0D, -0.5D, -0.5D);
        } else {
            graphics.poseStack().mulPose(Axis.YP.rotationDegrees(
                facing != Direction.EAST && facing != Direction.WEST
                    ? facing.toYRot()
                    : facing.getOpposite().toYRot()
            ));
        }
        graphics.poseStack().translate(-0.5F, 1.0F, -0.5F);
        graphics.poseStack().scale(0.0625F, -0.0625F, 0.0625F);
        this.renderBlock(graphics, partialTick);
        graphics.poseStack().popPose();
    }

    protected abstract void renderBlock(BERGraphics<T> var1, float var2);
}