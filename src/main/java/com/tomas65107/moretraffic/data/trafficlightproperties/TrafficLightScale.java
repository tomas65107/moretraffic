package com.tomas65107.moretraffic.data.trafficlightproperties;

import net.minecraft.util.StringRepresentable;

//  Possible sizes for 3-light traffic light:
//      1_0x
//
//  Possible sizes for 2-light traffic light:
//      1_0x, 1_5x
//
//  Possible sizes for 1-light traffic light:
//      1_0x, 1_5x, 2_0x, full_block(!!)
//

//
//  ! FULL BLOCK NOT SUPPORTED WITH POSITION
//
//  ! FULL BLOCK IS COMPATIBLE ONLY WITH VERTICAL_SUPPORT ORIENTATION
//

public enum TrafficLightScale implements StringRepresentable, TrafficLightProperty {
    S1_0X,
    S1_5X,
    S2_0X,
    FULLBLOCK; // FULLBLOCK IS COMPATIBLE ONLY WITH VERTICAL_SUPPORT ORIENTATION

    @Override
    public String getSerializedName() {
        return switch(this) {
            case S1_0X -> "1_0x";
            case S1_5X -> "1_5x";
            case S2_0X -> "2_0x";
            case FULLBLOCK -> "full_block";
        };
    }

    @Override
    public PropertyTypes getClassType() {
        return PropertyTypes.SCALE;
    }
}
