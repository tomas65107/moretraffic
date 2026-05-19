package com.tomas65107.moretraffic.mod.mixins;

import de.mrjulsen.trafficcraft.block.TrafficSignBlock;
import net.minecraft.world.level.block.RenderShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrafficSignBlock.class)
public class TrafficSignBlockMixin {

    @Inject(
            method = "getRenderShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void moretraffic$noModel(CallbackInfoReturnable<RenderShape> cir) {
        cir.setReturnValue(RenderShape.INVISIBLE);
    }


}