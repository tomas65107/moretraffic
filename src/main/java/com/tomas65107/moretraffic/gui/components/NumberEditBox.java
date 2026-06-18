package com.tomas65107.moretraffic.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class NumberEditBox extends BetterEditBox {

    public NumberEditBox(int x, int y) {
        super(x+2, y+2, 47, 14);
        this.setBordered(false);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        guiGraphics.blit(
                ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/num_textbox_bg.png"),
                getX()-3, getY()-3, // screen position
                0, 0,           // texture u/v
                width, height,  // render size
                width, height   // texture size
        );

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }
}
