package com.tomas65107.moretraffic.data.trafficlightproperties;

import net.minecraft.util.StringRepresentable;

public enum TrafficLightOrientation implements StringRepresentable, TrafficLightProperty {
    VERTICAL, //! FULL BLOCK NOT SUPPORTED
    VERTICAL_SUPPORT;

    @Override
    public String getSerializedName() {
        return switch(this) {
            case VERTICAL -> "vertical";
            case VERTICAL_SUPPORT -> "vertical_support";
        };
    }

    @Override
    public PropertyTypes getClassType() {
        return PropertyTypes.ORIENTATION;
    }
}
