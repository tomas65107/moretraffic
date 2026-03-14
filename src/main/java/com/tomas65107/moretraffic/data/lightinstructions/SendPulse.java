package com.tomas65107.moretraffic.data.lightinstructions;

import com.tomas65107.moretraffic.block.FlashingBlinkerBlockEntity;
import com.tomas65107.moretraffic.block.LightControlCabinetBlockEntity;
import com.tomas65107.moretraffic.block.TrafficDisplayBlockEntity;
import com.tomas65107.moretraffic.data.TrafficLightGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record SendPulse(String group, boolean enable) implements LightInstructionProperty {
    @Override
    public PropertyTypes getClassType() {
        return PropertyTypes.SEND_PULSE;
    }

    @Override
    public boolean executePayload(LightControlCabinetBlockEntity be, LightInstructionProperty instruction, Level level, BlockPos blockPos) {
        if (instruction instanceof SendPulse(String igroup, boolean enable)) {

            List<BlockPos> blockPosList = new ArrayList<>();
            for (TrafficLightGroup group : be.groups) if (group.name.equals(igroup)) { blockPosList = new ArrayList<>(group.lightsPositions); break; }

            if (!blockPosList.isEmpty()) {
                for (BlockPos lightPos : blockPosList) {
                    if (level.getBlockEntity(lightPos) instanceof FlashingBlinkerBlockEntity flashingBlinkerBlockEntity) {
                        flashingBlinkerBlockEntity.lightStatus = enable;
                        flashingBlinkerBlockEntity.setChanged();
                        level.sendBlockUpdated(flashingBlinkerBlockEntity.getBlockPos(), flashingBlinkerBlockEntity.getBlockState(), flashingBlinkerBlockEntity.getBlockState(), 3);
                    } else {
                        be.isRunning = false;
                        return false; //cannot find supported blocks there
                    }
                }
                return true;
            } else {
                be.isRunning = false;
                return false;
            }
        }
        be.isRunning = false;
        throw new RuntimeException("Corrupted Instruction passed for execution; Instruction of incorrect type");
    }
}
