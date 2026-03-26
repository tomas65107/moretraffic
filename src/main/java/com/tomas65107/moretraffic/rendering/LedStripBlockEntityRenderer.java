package com.tomas65107.moretraffic.rendering;

import com.tomas65107.moretraffic.block.LEDStripBlockEntity;
import com.tomas65107.moretraffic.rendering.helpers.LegacyCube;
import com.tomas65107.moretraffic.rendering.helpers.RotatableAllBlockEntityRenderer;
import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.mcdragonlib.util.Pair;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec2;

import static com.tomas65107.moretraffic.rendering.MaterialValues.EMISSIVE;
import static com.tomas65107.moretraffic.rendering.MaterialValues.NOT_EMISSIVE;
import static com.tomas65107.moretraffic.rendering.TrafficLightBlockEntityRenderer.getBoostedTint;

public class LedStripBlockEntityRenderer extends RotatableAllBlockEntityRenderer<LEDStripBlockEntity> {

    public LedStripBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderBlock(BERGraphics<LEDStripBlockEntity> graphics, float partialTicks) {
        LEDStripBlockEntity be = graphics.blockEntity();

        // BACKPLATE
        LegacyCube backplate = LegacyCube.cube(
                ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/block/bulb.png"),
                be.sizeX, be.sizeY, 1f,
                dir -> true,
                dir -> Pair.of(new Vec2(0f,0f), new Vec2(1f,1f))
        );

        // LIGHT
        LegacyCube light = LegacyCube.cube(
                ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/block/light_on_solid.png"),
                be.sizeX, be.sizeY, 1.5f,
                dir -> true,
                dir -> Pair.of(new Vec2(0f,0f), new Vec2(1f,1f))
        );

        // Render BACKPLATE
        graphics.poseStack().pushPose();
        graphics.poseStack().translate(be.startPosX, 16-be.startPosY - 0.2, 0 + 0.01);
        backplate.setLight(NOT_EMISSIVE);
        backplate.setTint(DyeColor.BLACK.getTextureDiffuseColor());
        backplate.render(graphics);
        graphics.poseStack().popPose();

        // Render LIGHT
        graphics.poseStack().pushPose();
        graphics.poseStack().translate(be.startPosX, 16-be.startPosY - 0.2, 0 + 1f + 0.01);
        light.setLight(be.color == DyeColor.BLACK ? NOT_EMISSIVE : EMISSIVE);
        light.setTint(getBoostedTint(be.color));
        light.render(graphics);
        graphics.poseStack().popPose();

    }
}