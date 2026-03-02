package com.tomas65107.moretraffic.gui.components.overriderenders;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class PriorityWidget<T extends AbstractWidget> extends AbstractWidget {
    private final T original;

    public PriorityWidget(T original) {
        super(original.getX(), original.getY(), original.getWidth(), original.getHeight(), original.getMessage());
        this.original = original;
    }

    @Override
    protected void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        gfx.pose().pushPose();
        gfx.pose().translate(0, 0, 167);
        original.render(gfx, mouseX, mouseY, partialTick);
        gfx.pose().popPose();
    }

    public T getOriginal() {
        return original;
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return original.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return original.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return original.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return original.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        return original.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    @Override
    public void setFocused(boolean focused) {
        original.setFocused(focused);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return original.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isFocused() {
        return original.isFocused();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        original.updateNarration(narrationElementOutput);
    }
}