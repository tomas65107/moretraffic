package com.tomas65107.moretraffic.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public final class CustomRenderAsWidget extends AbstractWidget {

    private final Consumer<GuiGraphics> gfx;

    public int xAdder = 0;
    public int yAdder = 0;

    public CustomRenderAsWidget(Consumer<GuiGraphics> guiGraphics) {
        super(0, 0, 0, 0, Component.empty());
        gfx = guiGraphics;
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.pose().pushPose();
        g.pose().translate(xAdder, yAdder, 0);
        gfx.accept(g);
        g.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}