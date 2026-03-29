package com.tomas65107.moretraffic.block;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainStatus;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.tomas65107.moretraffic.data.lightinstructions.ICabinetPulsable;
import com.tomas65107.moretraffic.mod.MoreTrafficCompat;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class DerailerBlockEntity extends SmartBlockEntity implements TransformableBlockEntity, Clearable, ICabinetPulsable {
    public TrackTargetingBehaviour<Derailer> edgePoint;

    private UUID derailedTrain = null;

    int flagYRot = -1;
    boolean flagFlipped;
    public LerpedFloat flag;

    public DerailerBlockEntity(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        flag = LerpedFloat.linear()
                .startWithValue(0);
    }

    @Override
    public void handlePulseLight(Boolean newStatus) {
        BlockState state = getBlockState();
        level.setBlock(worldPosition, state.setValue(DerailerBlock.POWERED, newStatus), 3);

    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(edgePoint = new TrackTargetingBehaviour<>(this, MoreTrafficCompat.DERAILER));
    }

    @Override
    public void tick() {
        super.tick();
        assert level != null;
        if (level.isClientSide) {
            float currentTarget = flag.getChaseTarget();
            if (currentTarget == 0 || flag.settled()) {
                int target = getBlockState().getValue(DerailerBlock.POWERED) ? 1 : 0;
                if (target != currentTarget) {
                    flag.chase(target, 0.1f, LerpedFloat.Chaser.LINEAR);
                    if (target == 1)
                        AllSoundEvents.CONTRAPTION_DISASSEMBLE.playAt(level, worldPosition, 1, 2, true);
                }
            }
            boolean settled = flag.getValue() > .15f;
            flag.tickChaser();
            if (currentTarget == 0 && settled != flag.getValue() > .15f)
                AllSoundEvents.CONTRAPTION_ASSEMBLE.playAt(level, worldPosition, 0.75f, 1.5f, true);
            return;
        }

        for (Train train : Create.RAILWAYS.sided(level).trains.values()) {
            for (Carriage carriage : train.carriages) {
                CarriageContraptionEntity entity = carriage.anyAvailableEntity();
                if (entity == null) continue;

                if (isWithinRange(edgePoint.getGlobalPosition(), entity.position())) {
                    if (derailedTrain != null && derailedTrain.equals(train.id)) return; else derailedTrain = train.id;
                    if (this.getBlockState().getValue(DerailerBlock.POWERED)) {
                        train.navigation.cancelNavigation();
                        if (!train.derailed) {
                            train.derailed = true;
                            train.graph = null;
                            train.status.addMessage(new TrainStatus.StatusMessage(Component.literal(" - ").withStyle(ChatFormatting.GRAY).append(Component.translatable("interaction.moretraffic.derailment").withColor(16765876))));
                        }
                    }
                    return;
                }
            }
        }
        derailedTrain = null;
    }

    public boolean resolveFlagAngle() {
        if (flagYRot != -1)
            return true;

        BlockState target = edgePoint.getTrackBlockState();
        if (!(target.getBlock() instanceof ITrackBlock def))
            return false;

        Vec3 axis = null;
        BlockPos trackPos = edgePoint.getGlobalPosition();
        for (Vec3 vec3 : def.getTrackAxes(level, trackPos, target))
            axis = vec3.scale(edgePoint.getTargetDirection()
                    .getStep());
        if (axis == null)
            return false;

        Direction nearest = Direction.getNearest(axis.x, 0, axis.z);
        flagYRot = (int) (-nearest.toYRot() - 90);

        Vec3 diff = Vec3.atLowerCornerOf(trackPos.subtract(worldPosition))
                .multiply(1, 0, 1);
        if (diff.lengthSqr() == 0)
            return true;

        flagFlipped = diff.dot(Vec3.atLowerCornerOf(nearest.getClockWise()
                .getNormal())) > 0;

        return true;
    }

    private boolean isWithinRange(BlockPos blockPos, Vec3 pos) {
        var range = 2;
        return Math.abs(blockPos.getX() - pos.x) <= range &&
                Math.abs(blockPos.getY() - pos.y) <= range &&
                Math.abs(blockPos.getZ() - pos.z) <= range;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(Vec3.atLowerCornerOf(worldPosition), Vec3.atLowerCornerOf(edgePoint.getGlobalPosition())).inflate(2);
    }

    @Override
    public void transform(BlockEntity be, StructureTransform transform) {
        edgePoint.transform(be, transform);
    }

    @Override
    public void clearContent() {}
}
