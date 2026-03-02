package com.tomas65107.moretraffic.data.trafficlightproperties;

import net.minecraft.util.StringRepresentable;

public enum TrafficLightPosition implements StringRepresentable, TrafficLightProperty {
    TOP,
    BOTTOM; //! ONLY FOR WITH ONE LIGHT EXEPT FULL_BLOCK

    @Override
    public String getSerializedName() {
        return switch(this) {
            case TOP -> "top";
            case BOTTOM -> "bottom";
        };
    }

    @Override
    public PropertyTypes getClassType() {
        return PropertyTypes.POSITION;
    }
}
