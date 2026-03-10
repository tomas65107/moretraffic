package com.tomas65107.moretraffic.gui.components.buttons;

import com.tomas65107.moretraffic.data.ColorsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.function.Consumer;
import static com.tomas65107.moretraffic.data.helpers.ColorHelper.rgb;

public class PixelButton extends Button {

    private final boolean enabled;
    private Consumer<Boolean> flipPixelState;

    /// 0 = released; 1 = left; 2 = right
    private final Consumer<Integer> pressAction;

    private final long openedAt;

    public boolean aleradyChangedTsButtonTsHold;

    public PixelButton(int x, int y, int w, int h, boolean enabled, Consumer<Boolean> maskChangedFromLastState, Consumer<Integer> pressAction) {
        super(x, y, w, h, Component.empty(), nie->{}, DEFAULT_NARRATION);
        this.enabled = enabled;
        this.flipPixelState = maskChangedFromLastState;
        this.pressAction = pressAction;
        this.openedAt = System.currentTimeMillis();
    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int mouseAction = getMouseStatus();

        if (!aleradyChangedTsButtonTsHold && isHovered) {
            if (System.currentTimeMillis() - openedAt < 200) return;

            if (mouseAction == 1) { //only change deselected -> selected
                if (!enabled) {
                    aleradyChangedTsButtonTsHold = true;
                    flipPixelState.accept(true);
                }
            }
            if (mouseAction == 2) { //only change selected -> deselected
                if (enabled) {
                    aleradyChangedTsButtonTsHold = true;
                    flipPixelState.accept(true);
                }
            }
        }

        int bg = isHovered ?
                (enabled ? rgb(ColorsManager.SECONDARY) : rgb(ColorsManager.TERTIARY))
                : (enabled ? rgb(ColorsManager.PRIMARY) : rgb(ColorsManager.BACKGROUND));
        g.fill(getX(), getY(), getX() + width, getY() + height, bg);

        // Draw overlay if changed
        if (aleradyChangedTsButtonTsHold) {
            g.fill(getX(), getY(), getX() + width, getY() + height, rgb(mouseAction == 1 ? new Color(192, 255, 190) : new Color(255, 193, 193)));
        }

        if (mouseAction == 0) {
            aleradyChangedTsButtonTsHold = false;
        }
    }

    private int getMouseStatus() {
        long window = Minecraft.getInstance().getWindow().getWindow();
        if (GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
            pressAction.accept(1);
            return 1;
        } else if (GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS) {
            pressAction.accept(2);
            return 2;
        } else {
            pressAction.accept(0);
            return 0;
        }
    }

}