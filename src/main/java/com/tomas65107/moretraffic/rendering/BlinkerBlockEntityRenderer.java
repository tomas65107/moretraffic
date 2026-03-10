package com.tomas65107.moretraffic.rendering;

import com.tomas65107.moretraffic.block.FlashingBlinkerBlockEntity;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import de.mrjulsen.mcdragonlib.client.ber.BERCube;
import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.mcdragonlib.client.ber.RotatableBlockEntityRenderer;
import de.mrjulsen.mcdragonlib.data.Pair;
import de.mrjulsen.trafficcraft.data.PaintColor;
import jdk.nio.mapmode.ExtendedMapMode;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
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

        BERCube glass = BERCube.cube(
                LIGHT_TEXTURE,
                glassWidth, glassHeight, glassDepth,
                dir -> true,
                dir -> Pair.of(new Vec2(0f,0f), new Vec2(1f,1f))
        );
        graphics.poseStack().pushPose();
        graphics.poseStack().translate(
                0.31f,
                2f / 16f + glassHeight - 0.008 ,
                0.373f
        );
        glass.setLight(EMISSIVE);

        int colorWithAlpha = (255 << 24) | (getBoostedTint(toDyeColor(be.getColor())) & 0x00FFFFFF);
        glass.setTint(colorWithAlpha);
        if (be.lightStatus) glass.render(graphics);
        graphics.poseStack().popPose();

        // Bulb cube
        float bulbSize = (2f/16f)+0.001f;
        BERCube bulb = BERCube.cube(
                ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/block/bulb.png"),
                bulbSize, bulbSize, bulbSize,
                dir -> true,
                dir -> Pair.of(new Vec2(0f,0f), new Vec2(1f,1f))
        );
        graphics.poseStack().pushPose();
        graphics.poseStack().translate(
                0.5f-(bulbSize/2),
                0.65,
                0.437f
        );
        bulb.setLight(be.lightStatus ? EMISSIVE : MASKED_EMISSIVE);
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