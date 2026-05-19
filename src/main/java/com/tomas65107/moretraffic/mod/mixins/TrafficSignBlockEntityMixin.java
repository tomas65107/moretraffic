package com.tomas65107.moretraffic.mod.mixins;

import de.mrjulsen.trafficcraft.block.entity.TrafficSignBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrafficSignBlockEntity.class)
public class TrafficSignBlockEntityMixin {

    @Unique
    private float moretraffic$visualOffset = 0.0f;

    public float moretraffic$getVisualOffset() {
        return moretraffic$visualOffset;
    }

    public void moretraffic$setVisualOffset(float value) {
        this.moretraffic$visualOffset = value;
    }

    @Inject(
            method = "loadAdditional",
            at = @At("TAIL")
    )
    private void moretraffic$loadExtra(
            CompoundTag tag,
            HolderLookup.Provider registries,
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
            HolderLookup.Provider registries,
            CallbackInfo ci
    ) {
        tag.putFloat("MoreTrafficVisualOffset", this.moretraffic$visualOffset);
    }


}