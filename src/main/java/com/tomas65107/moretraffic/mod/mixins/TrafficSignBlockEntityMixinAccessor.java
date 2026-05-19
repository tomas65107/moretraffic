package com.tomas65107.moretraffic.mod.mixins;

import de.mrjulsen.trafficcraft.block.entity.TrafficSignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrafficSignBlockEntity.class)
public interface TrafficSignBlockEntityMixinAccessor {

    @Accessor("moretraffic$visualOffset")
    float moretraffic$getVisualOffset();
}