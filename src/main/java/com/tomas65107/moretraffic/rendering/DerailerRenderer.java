package com.tomas65107.moretraffic.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour.RenderedTrackOverlayType;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;

import com.tomas65107.moretraffic.block.Derailer;
import com.tomas65107.moretraffic.block.DerailerBlockEntity;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;

import static com.tomas65107.moretraffic.registration.MTPartials.FLAG;

public class DerailerRenderer extends SmartBlockEntityRenderer<DerailerBlockEntity> {

    public DerailerRenderer(Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(DerailerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        TrackTargetingBehaviour<Derailer> target = be.edgePoint;
        if (target != null) {
            ms.pushPose();
            BlockPos pos = be.getBlockPos();
            BlockPos targetPos = target.getGlobalPosition();
            renderFlag(be, partialTicks, ms, buffer, light, overlay, getDirectionFromPositions(be.getBlockPos(), target.getGlobalPosition()));
            ms.popPose();

            // Track overlay
            assert be.getLevel() != null;
            BlockState trackState = be.getLevel().getBlockState(targetPos);
            Block block = trackState.getBlock();

            if (block instanceof ITrackBlock) {
                ms.pushPose();
                ms.translate(targetPos.getX() - pos.getX(), targetPos.getY() - pos.getY(), targetPos.getZ() - pos.getZ());
                TrackTargetingBehaviour.render(be.getLevel(), targetPos, target.getTargetDirection(), target.getTargetBezier(),
                        ms, buffer, light, overlay, RenderedTrackOverlayType.OBSERVER, 1);
                ms.popPose();
            }
        }
    }

    private void renderFlag(DerailerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, Direction trackDirection) {
        if (!be.resolveFlagAngle()) return;

        SuperByteBuffer flag = CachedBuffers.partial(FLAG, be.getBlockState());

        transformFlag(flag, be, partialTicks, trackDirection);
        flag.light(light).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
    }

    private void transformFlag(SuperByteBuffer flag, DerailerBlockEntity be, float partialTicks, Direction direction) {

        float value = be.flag.getValue(partialTicks);
        float progress = (float) Math.pow(Math.min(value * 5, 1), 2);

        if (be.flag.getChaseTarget() > 0 && !be.flag.settled() && progress == 1) {
            float wiggleProgress = (value - .2f) / .8f;
            progress += (float) ((Math.sin(wiggleProgress * (2 * Mth.PI) * 4) / 8f)
                    / Math.max(1, 8f * wiggleProgress));
        }

        float nudge = 1 / 16f;

        flag.center()
                .rotateYDegrees(direction.toYRot())
                .translate(nudge, 9.5f / 16f, 2f / 16f + nudge)
                .uncenter()
                .rotateXDegrees(-(progress * 90 + 270));
    }

    private Direction getDirectionFromPositions(BlockPos from, BlockPos to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int dz = to.getZ() - from.getZ();

        if (Math.abs(dx) > Math.abs(dz)) { // X is dominant
            return dx < 0 ? Direction.EAST : Direction.WEST;
        } else { // Z is dominant
            return dz < 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }
}