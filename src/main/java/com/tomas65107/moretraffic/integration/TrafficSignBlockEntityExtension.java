package com.tomas65107.moretraffic.integration;

/** State added to TrafficCraft traffic-sign block entities by the MoreTraffic mixin. */
public interface TrafficSignBlockEntityExtension {
    float moretraffic$getVisualOffset();

    void moretraffic$setVisualOffset(float value);
}