package com.tomas65107.moretraffic.rendering;

import com.tomas65107.moretraffic.block.SignboardBlockEntity;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import com.tomas65107.moretraffic.rendering.helpers.RotatableAllBlockEntityRenderer;
import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class SignboardStripBlockEntityRenderer extends RotatableAllBlockEntityRenderer<SignboardBlockEntity> {

    public SignboardStripBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderBlock(BERGraphics<SignboardBlockEntity> graphics, float partialTicks) {
        SignboardBlockEntity be = graphics.blockEntity();

        graphics.poseStack().pushPose();
        graphics.poseStack().translate(0, 15.8, 0.15);

        if (be.elements != null) {
            for (int i = be.elements.size() - 1; i >= 0; i--) {
                graphics.poseStack().pushPose();
                graphics.poseStack().translate(0, 0, -i*0.05);
                be.elements.get(i).render(graphics);
                graphics.poseStack().popPose();
            }
        }
        graphics.poseStack().popPose();

    }
}