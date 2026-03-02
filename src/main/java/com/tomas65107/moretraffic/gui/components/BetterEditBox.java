package com.tomas65107.moretraffic.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BetterEditBox extends EditBox {

    public Runnable onSaveCode;
    public TooltipComponent floatingTooltip;

    public BetterEditBox(int x, int y, int width, int height) {
        this(x ,y ,width, height, Component.empty());
    }

    public BetterEditBox(int x, int y, int width, int height, Component message) {
        super(Minecraft.getInstance().font, x, y, width, height, message);
    }

    public void onSave(Runnable runnable) { onSaveCode = runnable; }

    public void onChange(Consumer<String> responder) { this.setResponder(responder); }

    public void showFloatingTooltip(TooltipComponent tooltipComponent) {this.floatingTooltip = tooltipComponent; }
    public void hideFloatingTooltip() {this.floatingTooltip = null; }

    public void triggerSaveCode() {
        if (onSaveCode != null) onSaveCode.run();
    }



    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (floatingTooltip != null) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, List.of(Component.empty()), Optional.of(floatingTooltip), ItemStack.EMPTY, getX()-9, getY() + getHeight() + 18);
        }
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void setFocused(boolean focused) {
        boolean wasFocused = isFocused();
        super.setFocused(focused);

        if (wasFocused && !focused) {
            if (onSaveCode != null) onSaveCode.run();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean inside = this.isMouseOver(mouseX, mouseY);

        if (!inside && isFocused()) {
            setFocused(false);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

}
