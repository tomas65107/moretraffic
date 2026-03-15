package com.tomas65107.moretraffic.data.lightinstructions;

import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlockEntity;
import com.tomas65107.moretraffic.block.LightControlCabinetBlockEntity;
import com.tomas65107.moretraffic.block.TrafficDisplayBlockEntity;
import com.tomas65107.moretraffic.data.TrafficDisplayPixels;
import com.tomas65107.moretraffic.data.TrafficLightGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record ModifyDisplay(String group, TrafficDisplayPixels trafficDisplayPixels) implements LightInstructionProperty {
    @Override
    public PropertyTypes getClassType() {
        return PropertyTypes.MODIFY_DISPLAY;
    }

    @Override
    public boolean executePayload(LightControlCabinetBlockEntity be, LightInstructionProperty instruction, Level level, BlockPos blockPos) {
        if (instruction instanceof ModifyDisplay(String igroup, TrafficDisplayPixels displayPixels)) {
            List<BlockPos> blockPosList = new ArrayList<>();
            for (TrafficLightGroup group : be.groups) if (group.name.equals(igroup)) {blockPosList = new ArrayList<>(group.lightsPositions); break;}
            if (!blockPosList.isEmpty()) {

                for (BlockPos lightPos : blockPosList) {
                    if (!level.isLoaded(lightPos)) continue;
                    if (level.getBlockEntity(lightPos) instanceof TrafficDisplayBlockEntity trafficDisplayBlockEntity) {
                        trafficDisplayBlockEntity.modifyDisplayPixels(displayPixels);
                    } else {
                        be.isRunning = false;
                        return false; //cannot find the traffic light at that position
                    }
                }
                return true; //all traffic lights found successfully and modified

            } else {
                be.isRunning = false;
                return false;
            }
        }
        be.isRunning = false;
        throw new RuntimeException("Corrupted Instruction passed for execution; Instruction of incorrect type");
    }
}
