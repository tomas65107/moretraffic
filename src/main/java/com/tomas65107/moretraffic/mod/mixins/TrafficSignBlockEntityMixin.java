package com.tomas65107.moretraffic.mod.mixins;

import com.tomas65107.moretraffic.integration.TrafficSignBlockEntityExtension;

import de.mrjulsen.trafficcraft.block.entity.TrafficSignBlockEntity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrafficSignBlockEntity.class)
public class TrafficSignBlockEntityMixin implements TrafficSignBlockEntityExtension {

    @Unique
    private float moretraffic$visualOffset = 0.0f;

    @Override
    public float moretraffic$getVisualOffset() {
        return moretraffic$visualOffset;
    }

    @Override
    public void moretraffic$setVisualOffset(float value) {
        this.moretraffic$visualOffset = value;
    }

    @Inject(
            method = "load",
            at = @At("TAIL")
    )
    private void moretraffic$loadExtra(
            CompoundTag tag,
            CallbackInfo ci
    ) {
        if (tag.contains("MoreTrafficVisualOffset")) {
            this.moretraffic$visualOffset = tag.getFloat("MoreTrafficVisualOffset");
        }
    }

    @Inject(
            method = "saveAdditional",
            at = @At("TAIL")
    )
    private void moretraffic$saveExtra(
            CompoundTag tag,
            CallbackInfo ci
    ) {
        tag.putFloat("MoreTrafficVisualOffset", this.moretraffic$visualOffset);
    }


}