package com.tomas65107.moretraffic.rendering;

import com.tomas65107.moretraffic.block.FlashingBlinkerBlockEntity;
import com.tomas65107.moretraffic.rendering.helpers.LegacyCube;
import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.mcdragonlib.client.ber.RotatableBlockEntityRenderer;
import de.mrjulsen.mcdragonlib.util.Pair;
import de.mrjulsen.trafficcraft.data.PaintColor;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec2;

import java.awt.*;

import static com.tomas65107.moretraffic.data.helpers.ColorHelper.rgb;
import static com.tomas65107.moretraffic.rendering.MaterialValues.*;
import static com.tomas65107.moretraffic.rendering.TrafficLightBlockEntityRenderer.LIGHT_TEXTURE;
import static com.tomas65107.moretraffic.rendering.TrafficLightBlockEntityRenderer.getBoostedTint;

public class BlinkerBlockEntityRenderer extends RotatableBlockEntityRenderer<FlashingBlinkerBlockEntity> {

    public BlinkerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderBlock(BERGraphics<FlashingBlinkerBlockEntity> graphics, float partialTicks) {
        graphics.poseStack().pushPose();

        graphics.poseStack().scale(16.0F, 16.0F, 16.0F);

        FlashingBlinkerBlockEntity be = graphics.blockEntity();

        // Glass cube
        float glassWidth = (6f / 16f)+0.005f;
        float glassHeight = (6f / 16f)+0.005f;
        float glassDepth = (4f / 16f)+0.005f;

        LegacyCube glass = LegacyCube.cube(
                ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/block/light_on_solid.png"),
                glassWidth, glassHeight, glassDepth,
                dir -> true,
                dir -> Pair.of(new Vec2(0f,0f), new Vec2(1f,1f))
        );
        graphics.poseStack().pushPose();
        graphics.poseStack().translate(
                0.31f,
                4f / 16f + glassHeight - 0.008 ,
                0.373f
        );
        glass.setLight(EMISSIVE);

        int colorWithAlpha = (255 << 24) | (getBoostedTint(toDyeColor(be.getColor())) & 0x00FFFFFF);
        glass.setTint(colorWithAlpha);
        if (be.lightStatus) glass.render(graphics);
        graphics.poseStack().popPose();

        // Bulb cube
        float bulbSize = (2f/16f)+0.001f;
        LegacyCube bulb = LegacyCube.cube(
                ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/block/bulb.png"),
                bulbSize, bulbSize, bulbSize,
                dir -> true,
                dir -> Pair.of(new Vec2(0f,0f), new Vec2(1f,1f))
        );
        graphics.poseStack().pushPose();
        graphics.poseStack().translate(
                0.5f-(bulbSize/2),
                0.55,
                0.437f
        );
        bulb.setLight(be.lightStatus ? EMISSIVE : NOT_EMISSIVE);
        int offColor = (130 << 24) | (getBoostedTint(toDyeColor(be.getColor())) & 0x00FFFFFF);
        bulb.setTint(be.lightStatus ? getBoostedTint(toDyeColor(be.getColor())) : offColor);
        bulb.render(graphics);
        graphics.poseStack().popPose();

        graphics.poseStack().popPose();
    }

    public DyeColor toDyeColor(PaintColor paintColor) {
        if (paintColor.getIndex() < 0) return DyeColor.BLACK;
        return DyeColor.byId(paintColor.getIndex());
    }
}