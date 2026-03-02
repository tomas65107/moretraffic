package com.tomas65107.moretraffic.data;

import net.minecraft.core.BlockPos;

import java.util.List;

public class TrafficLightGroup {
    public String name;
    public List<BlockPos> lightsPositions;

    public TrafficLightGroup(String name, List<BlockPos> lightsPositions) {
        this.name = name;
        this.lightsPositions = lightsPositions;
    }

    public TrafficLightGroup(String name) {
        this.name = name;
    }

    public TrafficLightGroup() {

    }
}
