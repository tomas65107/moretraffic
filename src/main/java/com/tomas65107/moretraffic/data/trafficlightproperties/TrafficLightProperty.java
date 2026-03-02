package com.tomas65107.moretraffic.data.trafficlightproperties;

public sealed interface TrafficLightProperty permits TrafficLightOrientation, TrafficLightPosition, TrafficLightScale {

    enum PropertyTypes {
        ORIENTATION,
        POSITION,
        SCALE;

        public String getNameOfProperty() {
            return switch(this) {
                case ORIENTATION -> "orientation";
                case POSITION -> "position";
                case SCALE -> "scale";
            };
        }

        public Class getClassOfProperty() {
            return switch(this) {
                case ORIENTATION -> TrafficLightOrientation.class;
                case POSITION -> TrafficLightPosition.class;
                case SCALE -> TrafficLightScale.class;
            };
        }

    }

    PropertyTypes getClassType();
    String getSerializedName();
}