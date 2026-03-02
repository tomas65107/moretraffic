package com.tomas65107.moretraffic.data.lightinstructions;

import com.tomas65107.moretraffic.block.LightControlCabinetBlockEntity;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;


public record AwaitRedstone(int internalTimestampWhenRedstoneDetected) implements LightInstructionProperty {

    @Override
    public PropertyTypes getClassType() {
        return PropertyTypes.AWAIT_REDSTONE;
    }

    @Override
    public boolean executePayload(LightControlCabinetBlockEntity be, LightInstructionProperty instruction, Level level, BlockPos blockPos) {
        if (instruction instanceof AwaitRedstone awaitRedstone) {
            return level.hasNeighborSignal(blockPos);
        }
        be.isRunning = false;
        throw new RuntimeException("Corrupted Instruction passed for execution; Instruction of incorrect type");
    }
}
