package com.tomas65107.moretraffic.gui.components.buttons;

import com.tomas65107.moretraffic.data.ColorsManager;
import com.tomas65107.moretraffic.data.helpers.TextHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;

import static com.tomas65107.moretraffic.data.helpers.ColorHelper.rgb;

public class ColorButton extends Button {

    private final int color; // ARGB
    private final boolean selected;
    public boolean shouldMinimizeBlack;

    public ColorButton(int x, int y, int w, int h, int color, OnPress onPress, boolean selected) {
        super(x, y, w, h, Component.empty(), onPress, DEFAULT_NARRATION);
        this.color = color;
        this.selected = selected;
    }

    public ColorButton(int x, int y, int w, int h, int color, OnPress onPress, boolean selected, boolean shouldMinimizeBlack) {
        this(x, y, w, h, color, onPress, selected);
        this.shouldMinimizeBlack = shouldMinimizeBlack;
    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int bg = this.isHovered ?
                //hovered
                selected ? rgb(ColorsManager.BUTTON_SELECTED_HOVER) : rgb(ColorsManager.TERTIARY)
                ://not hovered
                selected ? rgb(ColorsManager.BUTTON_SELECTED) : rgb(ColorsManager.SECONDARY);

        if (color != DyeColor.BLACK.getTextureDiffuseColor() || shouldMinimizeBlack) {
            g.fill(getX(), getY(), getX() + width, getY() + height, bg);
            g.fill(
                    getX() + 2,
                    getY() + 2,
                    getX() + width - 2,
                    getY() + height - 2,
                    color
            );
        } else {
            width = (16*3)+2;
            g.fill(getX(), getY(), getX() + width, getY() + height, bg);
            g.fill(
                    getX() + 2,
                    getY() + 2,
                    getX() + width - 2,
                    getY() + height - 2,
                    color
            );
            float scale = 0.5f;

            Component text = Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_color.light_off");

            int scaledWidth = (int)(Minecraft.getInstance().font.width(text) * scale);
            int scaledHeight = (int)(Minecraft.getInstance().font.lineHeight * scale);
            int textX = getX() + (width - scaledWidth) / 2;
            int textY = getY() + (height - scaledHeight) / 2;
            TextHelper.renderText(g, textX, textY, text, scale);
        }
    }
}