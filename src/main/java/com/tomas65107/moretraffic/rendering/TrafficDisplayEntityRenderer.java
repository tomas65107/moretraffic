package com.tomas65107.moretraffic.rendering;

import com.tomas65107.moretraffic.block.TrafficDisplayBlockEntity;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import de.mrjulsen.mcdragonlib.client.ber.BERCube;
import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.mcdragonlib.client.ber.RotatableBlockEntityRenderer;
import de.mrjulsen.mcdragonlib.data.Pair;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec2;

import java.awt.*;

import static com.tomas65107.moretraffic.rendering.MaterialValues.EMISSIVE;
import static com.tomas65107.moretraffic.rendering.MaterialValues.MASKED_EMISSIVE;
import static com.tomas65107.moretraffic.rendering.TrafficLightBlockEntityRenderer.LIGHT_TEXTURE;
import static com.tomas65107.moretraffic.rendering.TrafficLightBlockEntityRenderer.getBoostedTint;

public class TrafficDisplayEntityRenderer extends RotatableBlockEntityRenderer<TrafficDisplayBlockEntity> {

    public TrafficDisplayEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderBlock(BERGraphics<TrafficDisplayBlockEntity> berGraphics, float v) {
        float fullPixel = 1.0f; // each block pixel in block units
        float cubeSize = fullPixel * 0.8f; // scale cube to 50% of the pixel
        float cubeOffset = (fullPixel - cubeSize) / 2f; // center cube in pixel

        int index = 0;
        for (var pixelColor : berGraphics.blockEntity().pixelMask.rows) {
            boolean isTurnedOn = pixelColor != DyeColor.BLACK;
            int x = index % 16;
            int y = index / 16;

            BERCube pixelCube = BERCube.cube(
                    LIGHT_TEXTURE,
                    cubeSize,
                    cubeSize,
                    0.0625F,
                    dir -> dir == Direction.SOUTH,
                    dir -> Pair.of(new Vec2(0f,0f), new Vec2(1f,1f))
            );

            pixelCube.setLight(isTurnedOn ? EMISSIVE : MASKED_EMISSIVE);
            pixelCube.setTint(getBoostedTint(pixelColor));

            berGraphics.poseStack().pushPose();
            berGraphics.poseStack().translate(
                    x * fullPixel + cubeOffset,
                    y * fullPixel + cubeOffset,
                    7.2f
            );

            pixelCube.render(berGraphics);
            berGraphics.poseStack().popPose();

            index++;
        }

    }
}
