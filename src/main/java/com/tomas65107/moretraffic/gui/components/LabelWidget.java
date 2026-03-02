package com.tomas65107.moretraffic.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class LabelWidget extends AbstractWidget {

    private final int color;
    private final boolean shadow;
    private final float size;

    public LabelWidget(int x, int y, Component text, int color, boolean shadow) {
        this(x, y, text, color, shadow, 1f);
    }

    public LabelWidget(int x, int y, Component text, int color, boolean shadow, float size) {
        super(x, y, Minecraft.getInstance().font.width(text), Minecraft.getInstance().font.lineHeight, text);
        this.color = color;
        this.shadow = shadow;
        this.size = size;
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.pose().pushPose();
        g.pose().translate(this.getX(), this.getY(), 0); // move pivot to text position
        g.pose().scale(size, size, 1f); // scale relative to pivot
        g.drawString(Minecraft.getInstance().font, this.getMessage(), 0, 0, color, shadow);
        g.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}