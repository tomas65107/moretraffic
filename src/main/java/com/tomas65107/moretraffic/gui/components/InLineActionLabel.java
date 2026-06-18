package com.tomas65107.moretraffic.gui.components;

import com.tomas65107.moretraffic.data.ColorsManager;
import com.tomas65107.moretraffic.gui.components.buttons.AdvancedButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.tomas65107.moretraffic.helpers.ColorHelper.rgb;
import static com.tomas65107.moretraffic.helpers.ColorHelper.withAlpha;

public class InLineActionLabel extends LabelWidget {

    private final List<AdvancedButton> buttons;
    private final int hoverSize;
    private final Runnable onClick;
    private final boolean isOptionFocused;

    public InLineActionLabel(int x, int y, Component text, int color, boolean shadow, List<AdvancedButton> buttons, int hoverSize, Runnable onClick,  boolean isFocused) {
        super(x, y, text, color, shadow);
        this.buttons = buttons;
        this.hoverSize = hoverSize;
        this.onClick = onClick;
        this.isOptionFocused = isFocused;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(g, mouseX, mouseY, partialTick);

        int padding = 2;
        int FontHeight = Minecraft.getInstance().font.lineHeight;

        if (isOptionFocused) {
            g.fill(
                    getX() - padding,
                    getY() - padding,
                    getX() + hoverSize + padding,
                    getY() + FontHeight + padding,
                    withAlpha(rgb(ColorsManager.BUTTON_SELECTED), 60)
            );
        }

        if (hoverSize != 0 && isMouseOverCustom(mouseX, mouseY)) {
            g.fill(
                    getX() - padding,
                    getY() - padding,
                    getX() + hoverSize + padding,
                    getY() + FontHeight + padding,
                    withAlpha(rgb(ColorsManager.BUTTON_SELECTED_HOVER), 40)
            );
        }

        if (buttons != null && isMouseOverCustom(mouseX, mouseY)) {
            int rectRight = getX() + hoverSize + padding;

            int buttonSpacing = 1;

            int currentX = rectRight;

            int size = 11;
            for (AdvancedButton button : buttons) {
                if (button == null) continue;

                currentX -= size + buttonSpacing;

                button.setX(currentX);
                button.setY(getY()-1);
                button.setWidth(size);
                button.setHeight(size);

                button.render(g, mouseX, mouseY, partialTick);

            }
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (buttons != null) {
            for (AdvancedButton b : buttons) {
                if (b.isMouseOver(mouseX, mouseY)) {
                    return b.mouseClicked(mouseX, mouseY, button);
                }
            }
        }

        if (isMouseOverCustom((int) mouseX, (int) mouseY)) {
            onClick.run();
            return true;
        }

        return false;
    }

    private boolean isMouseOverCustom(int mouseX, int mouseY) {
        int padding = 1;
        int height = Minecraft.getInstance().font.lineHeight;

        int x1 = getX() - padding;
        int y1 = getY() - padding;
        int x2 = getX() + hoverSize + padding;
        int y2 = getY() + height + padding;

        return mouseX >= x1 && mouseX <= x2
                && mouseY >= y1 && mouseY <= y2;
    }
}
