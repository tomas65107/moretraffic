package com.tomas65107.moretraffic.data.lightinstructions;

import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlockEntity;
import com.tomas65107.moretraffic.block.LightControlCabinetBlockEntity;
import com.tomas65107.moretraffic.data.TrafficLightGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record ModifyLight(String group, DyeColor light0, DyeColor light1, DyeColor light2) implements LightInstructionProperty {
    @Override
    public PropertyTypes getClassType() {
        return PropertyTypes.MODIFY_LIGHT;
    }

    @Override
    public boolean executePayload(LightControlCabinetBlockEntity be, LightInstructionProperty instruction, Level level, BlockPos blockPos) {
        if (instruction instanceof ModifyLight modifyLight) {
            List<BlockPos> blockPosList = new ArrayList<>();
            for (TrafficLightGroup group : be.groups) if (group.name.equals(modifyLight.group())) {blockPosList = new ArrayList<>(group.lightsPositions); break;}
            if (!blockPosList.isEmpty()) {

                for (BlockPos TrafficLightblockPos : blockPosList) {
                    if (level.getBlockEntity(TrafficLightblockPos) instanceof AdvancedTrafficLightBlockEntity trafficLightBlock) {
                        trafficLightBlock.modifyLightColor(0, light0());
                        trafficLightBlock.modifyLightColor(1, light1());
                        trafficLightBlock.modifyLightColor(2, light2());
                    } else {
                        be.isRunning = false;
                        return false; //cannot find the traffic light at that position
                    }
                }
                return true; //all traffic lights found successfully and modified

            } else {
                be.isRunning = false;
                throw new RuntimeException("Corrupted Instruction passed for execution; Group node cannot be indexed or is empty");
            }
        }
        be.isRunning = false;
        throw new RuntimeException("Corrupted Instruction passed for execution; Instruction of incorrect type");
    }
}