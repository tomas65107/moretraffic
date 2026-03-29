package com.tomas65107.moretraffic.rendering;

import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlock;
import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlockEntity;
import com.tomas65107.moretraffic.data.TrafficLightLight;
import com.tomas65107.moretraffic.registration.MTRegistrate;
import com.tomas65107.moretraffic.rendering.helpers.LegacyCube;
import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.mcdragonlib.client.ber.RotatableBlockEntityRenderer;
import de.mrjulsen.mcdragonlib.util.Pair;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec2;

import java.awt.*;

import static com.tomas65107.moretraffic.data.helpers.ColorHelper.rgb;
import static com.tomas65107.moretraffic.data.trafficlightproperties.TrafficLightPosition.BOTTOM;
import static com.tomas65107.moretraffic.rendering.MaterialValues.*;

public class TrafficLightBlockEntityRenderer extends RotatableBlockEntityRenderer<AdvancedTrafficLightBlockEntity> {

    public static final ResourceLocation LIGHT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/block/light_on.png");


    private static final float BIG_LIGHT = 0.25f; // BL
    private static final float PIXEL = BIG_LIGHT / 16f; // P = BL/16

    private static final LegacyCube LIGHT = createLight();


    public TrafficLightBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void renderBlock(BERGraphics<AdvancedTrafficLightBlockEntity> graphics, float partialTick) {
        graphics.poseStack().pushPose();
        graphics.poseStack().scale(16.0F, 16.0F, 16.0F);

        AdvancedTrafficLightBlockEntity be = graphics.blockEntity();

        //exact coords
        float baseX = 0.375F;
        float baseZ = 0.71875F;
        float[] lightBottomY = {
                0.09375F, 0.40625F, 0.71875F
        };

        int lightOffset = 0;
        if (be.getBlockState().getValue(AdvancedTrafficLightBlock.POSITION).equals(BOTTOM)) {
            if (be.getBlockState().getBlock().equals(MTRegistrate.ADV_2_TRAFFIC_LIGHT.get())) {lightOffset = 1;}
            if (be.getBlockState().getBlock().equals(MTRegistrate.ADV_1_TRAFFIC_LIGHT.get())) {lightOffset = 2;}
        }

//        for (float y : lightBottomY) {
        for (int index = 0; index < be.lights.size(); index++) {

            float y = lightBottomY[index + lightOffset];

            graphics.poseStack().pushPose();
            graphics.poseStack().translate(baseX, y, baseZ + 0.0625F);
            if (be.lights.get(index).color == DyeColor.BLACK) {
                LIGHT.setLight(NOT_EMISSIVE); //black
            } else {
                LIGHT.setLight(EMISSIVE); //default
            }
            LIGHT.setTint(getBoostedTint(be.lights.get(index).color));
            LIGHT.render(graphics);
            if (be.lights.get(index).mask != null) {
                renderMask(graphics, be.lights.get(index).mask);
            }
            graphics.poseStack().popPose();
        }

        graphics.poseStack().popPose();
    }

    static int getBoostedTint(DyeColor dyeColor) {
        if (dyeColor == DyeColor.BLACK) {return DyeColor.BLACK.getTextureDiffuseColor();}
        int color = dyeColor.getTextureDiffuseColor();

        // extract RGB in 0..1
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        // convert RGB -> HSL
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float l = (max + min) / 2f;

        float s = 0f;
        if (max != min) {s = l < 0.5f ? (max - min) / (max + min) : (max - min) / (2f - max - min);}

        float h = 0f; // hue
        if (max == r) h = (g - b) / (max - min);
        else if (max == g) h = 2f + (b - r) / (max - min);
        else h = 4f + (r - g) / (max - min);
        h *= 60f;
        if (h < 0) h += 360f;

        s = Math.min(1f, s * 67f);

        // convert HSL back to RGB
        float c = (1 - Math.abs(2*l - 1)) * s;
        float x = c * (1 - Math.abs((h / 60f) % 2 - 1));
        float m = l - c/2f;

        float r1=0,g1=0,b1=0;
        if (h < 60)      { r1=c; g1=x; b1=0; }
        else if (h < 120){ r1=x; g1=c; b1=0; }
        else if (h < 180){ r1=0; g1=c; b1=x; }
        else if (h < 240){ r1=0; g1=x; b1=c; }
        else if (h < 300){ r1=x; g1=0; b1=c; }
        else             { r1=c; g1=0; b1=x; }

        int R = Math.min(255, (int)((r1 + m)*255));
        int G = Math.min(255, (int)((g1 + m)*255));
        int B = Math.min(255, (int)((b1 + m)*255));

        return 0xFF000000 | (R << 16) | (G << 8) | B;
    }

    private void renderMask(BERGraphics<AdvancedTrafficLightBlockEntity> graphics,
                            TrafficLightLight.TrafficLightMask mask) {

        for (int y = 0; y < 16; y++) {
            short row = mask.getRows()[y];

            for (int x = 0; x < 16; x++) {
                if (((row >>> x) & 1) == 0) continue;

                // Compute UVs for this pixel (16x16 texture grid)
                float u0 = x / 16f;
                float v0 = y / 16f;
                float u1 = (x + 1) / 16f;
                float v1 = (y + 1) / 16f;

                LegacyCube pixelCube = LegacyCube.cube(
                        LIGHT_TEXTURE,
                        PIXEL + 0.002f,
                        PIXEL + 0.002f,
                        0.0625F,
                        dir -> dir != Direction.NORTH && dir != Direction.UP,
                        dir -> Pair.of(new Vec2(u0, v0), new Vec2(u1, v1))
                );

                pixelCube.setLight(MASKED_EMISSIVE);
                pixelCube.setTint(rgb(new Color(30, 30, 30)));

                graphics.poseStack().pushPose();
                graphics.poseStack().translate(
                        x * PIXEL - 0.0001f,
                        (y * PIXEL - 0.0001f) -0.234f,
                        0.001f
                );

                pixelCube.render(graphics);
                graphics.poseStack().popPose();
            }
        }
    }

    private static LegacyCube createLight() {
        return LegacyCube.cube(LIGHT_TEXTURE, 0.25F, 0.25F, 0.0625F,
                dir -> dir != Direction.NORTH && dir != Direction.UP,
                dir -> {
                    switch (dir) {
                        case WEST, EAST -> { return Pair.of(new Vec2(0f, 0f), new Vec2(0.0625f, 1f)); }
                        case DOWN, UP -> { return Pair.of(new Vec2(0f, 0f), new Vec2(1f, 0.0625f)); }
                        default -> { return Pair.of(new Vec2(0f, 0f), new Vec2(1f, 1f)); }
                    }
                }
        );
    }
}
