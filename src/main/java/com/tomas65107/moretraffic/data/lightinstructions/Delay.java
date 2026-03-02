package com.tomas65107.moretraffic.data.lightinstructions;

import com.tomas65107.moretraffic.block.LightControlCabinetBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public record Delay(int delayInTicks) implements LightInstructionProperty {
    @Override
    public PropertyTypes getClassType() {
        return PropertyTypes.DELAY;
    }

    @Override
    public boolean executePayload(LightControlCabinetBlockEntity be, LightInstructionProperty instruction, Level level, BlockPos blockPos) {
        if (instruction instanceof Delay delay) {
            return (be.lastInstructionFinishedTick + delay.delayInTicks() == be.ticksSinceStart);
        }
        be.isRunning = false;
        throw new RuntimeException("Corrupted Instruction passed for execution; Instruction of incorrect type");
    }

}
