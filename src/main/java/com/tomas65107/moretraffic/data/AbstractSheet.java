package com.tomas65107.moretraffic.data;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import java.util.function.Consumer;

public abstract class AbstractSheet {
    public final int x;
    public final int y;
    public final String title;
    public final boolean showDoneButton;
    public final int width;
    public final int height;

    public abstract void init(Consumer<AbstractWidget> adder);
    public final void renderer(GuiGraphics gfx, int mouseX, int mouseY) {}

    public AbstractSheet(int x, int y, String title, boolean showDoneButton, int width, int height) {
        this.x = x;
        this.y = y;
        this.title = title;
        this.showDoneButton = showDoneButton;
        this.width = width;
        this.height = height;
    }
}
