package com.tomas65107.moretraffic.gui.components;

import com.mojang.blaze3d.platform.Window;
import com.tomas65107.moretraffic.gui.AbstractTomiContainerScreen;
import com.tomas65107.moretraffic.gui.components.overriderenders.PriorityWidget;
import com.tomas65107.moretraffic.helpers.ClientScheduler;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.tomas65107.moretraffic.data.ColorsManager.PRIMARY;
import static com.tomas65107.moretraffic.helpers.ColorHelper.rgb;

public class BetterEditBox extends EditBox {
    public Runnable onSaveCode;
    public TooltipComponent floatingTooltip;

    private boolean focusLock = false;

    public BetterEditBox(int x, int y, int width, int height) {
        this(x ,y ,width, height, Component.empty());
    }

    public BetterEditBox(int x, int y, int width, int height, Component message) {
        super(Minecraft.getInstance().font, x, y, width, height, message);
        this.setTextColor(rgb(PRIMARY));
    }

    public void onSave(Runnable runnable) { onSaveCode = runnable; }

    public void onChange(Consumer<String> responder) { this.setResponder(responder); }

    public void showFloatingTooltip(TooltipComponent tooltipComponent) {this.floatingTooltip = tooltipComponent; }
    public void hideFloatingTooltip() {this.floatingTooltip = null; }

    public void triggerSaveCode() {
        if (onSaveCode != null) onSaveCode.run();
    }



    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (floatingTooltip != null) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, List.of(Component.empty()), Optional.of(floatingTooltip), ItemStack.EMPTY, getX()-9, getY() + getHeight() + 18);
        }
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

//        var original = Minecraft.getInstance().screen.getFocused();
//        if (original instanceof PriorityWidget<?> p) original = p.getOriginal();
//        if (original instanceof BetterEditBox focusingBox) {
//            System.out.println(focusingBox.getValue());
//        }
    }

    @Override
    public void setFocused(boolean focused) {
        if (!focused) {
            super.setFocused(false); //sets the focus

            Minecraft mc = Minecraft.getInstance();
            double guiMouseX = mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth();
            double guiMouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

            assert Minecraft.getInstance().screen != null;
            var child = Minecraft.getInstance().screen.getChildAt(guiMouseX, guiMouseY);
            if (!child.equals(Optional.empty())) {
                final GuiEventListener[] original = {child.get()};
                if (original[0] instanceof PriorityWidget<?> p) original[0] = p.getOriginal();

                // If the newly clicked element is a bettereditbox
                if (original[0] instanceof BetterEditBox focusingBox) {
                    if (focusingBox.focusLock || focusLock) return;

                    focusingBox.focusLock = true;
                    if (onSaveCode != null) onSaveCode.run();
                    ClientScheduler.runLater(3, () -> {
                        if (Minecraft.getInstance().screen.getChildAt(guiMouseX, guiMouseY).isPresent()) {
                            Minecraft.getInstance().screen.setFocused(Minecraft.getInstance().screen.getChildAt(guiMouseX, guiMouseY).orElseThrow());
                            focusingBox.focusLock = false;
                        }
                    });
                } else {
                    if (onSaveCode != null) onSaveCode.run();
                }
            } else {
                if (onSaveCode != null) onSaveCode.run();
            }
        } else {
            super.setFocused(true);
        }
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        if (isActive()) {
            if (!(isMouseOver(mouseX, mouseY))) {
                Minecraft.getInstance().screen.setFocused(null);
                return false;
            }
        }
        return super.clicked(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            Minecraft.getInstance().screen.setFocused(null);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_E) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

//    @Override
//    public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        if (!(this.isMouseOver(mouseX, mouseY)) && isFocused()) {
//            setFocused(false);
//        }
//        return super.mouseClicked(mouseX, mouseY, button);
//    }

}