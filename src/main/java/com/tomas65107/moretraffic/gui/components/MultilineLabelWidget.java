package com.tomas65107.moretraffic.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import static com.tomas65107.moretraffic.data.ColorsManager.PRIMARY;
import static com.tomas65107.moretraffic.data.ColorsManager.SECONDARY;
import static com.tomas65107.moretraffic.helpers.ColorHelper.rgb;
import static com.tomas65107.moretraffic.helpers.TextCutter.cutTextComponent;

public class MultilineLabelWidget extends AbstractWidget {

    public MultilineLabelWidget(int x, int y, Component text, boolean shadow, float size) {
        super(x, y, Minecraft.getInstance().font.width(text), Minecraft.getInstance().font.lineHeight, text);

        int offset = 0;
//        for (Component component : cutTextComponent(text, 0, 180, true)) {
//            adder.accept(new LabelWidget(10, offset, component.copy().withStyle(style -> style.withColor(rgb(SECONDARY))), rgb(PRIMARY), true));
//            offset += 10;
//        }
    }

    public MultilineLabelWidget(int x, int y, Component text) {
        this(x, y, text, true, 1);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
