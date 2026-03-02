package com.tomas65107.moretraffic.gui.components.overriderenders;

import com.tomas65107.moretraffic.gui.components.buttons.AdvancedButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class NonInteractableWidget<T extends AbstractWidget> extends AbstractWidget {
    private final T original;

    public NonInteractableWidget(T original) {
        super(original.getX(), original.getY(), original.getWidth(), original.getHeight(), original.getMessage());
        this.original = original;

        if (original instanceof AdvancedButton) {
            ((AdvancedButton) original).tooltipComponent = null;
        }
    }

    @Override
    protected void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        if (original.isFocused()) {
            original.setFocused(false);
        }
        original.render(gfx, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean isHovered() {
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {}

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}