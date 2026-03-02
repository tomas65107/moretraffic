package com.tomas65107.moretraffic.data.helpers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import static com.tomas65107.moretraffic.data.helpers.ColorHelper.rgb;
import static com.tomas65107.moretraffic.data.ColorsManager.TERTIARY;

public final class TextHelper {

    public static Component buttonEnabled(boolean buttonEnabled) {
        if (buttonEnabled) {
            return Component.translatable("gui.createinterlinked.option_set").withColor(TERTIARY.getRGB()).withStyle(ChatFormatting.ITALIC);
        } else {
            return Component.translatable("gui.createinterlinked.option_not_set").withColor(TERTIARY.getRGB()).withStyle(ChatFormatting.ITALIC);
        }
    }

    public enum Alignment {
        LEADING,
        CENTER,
        TRAILING,
    }

    public static int align(Alignment alignTo, Component component, int x) {
        int width = net.minecraft.client.Minecraft.getInstance().font.width(component);
        return switch (alignTo) {
            case LEADING -> x;
            case CENTER -> x - (width / 2);
            case TRAILING -> x - width;
        };
    }


    public static void renderText(GuiGraphics guiGraphics, int x, int y, Component component) {
        renderText(guiGraphics, x, y, component, 1f, rgb(255, 255, 255), true);
    }
    public static void renderText(GuiGraphics guiGraphics, int x, int y, Component component, int color) {
        renderText(guiGraphics, x, y, component, 1f, color, true);
    }
    public static void renderText(GuiGraphics guiGraphics, int x, int y, Component component, float scale) {
        renderText(guiGraphics, x, y, component, scale, rgb(255, 255, 255), true);
    }
    public static void renderText(GuiGraphics guiGraphics, int x, int y, Component component, float scale, int color, boolean shadow) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1f);
        guiGraphics.drawString(
                net.minecraft.client.Minecraft.getInstance().font,
                component,
                (int) (x / scale),
                (int) (y / scale),
                color,
                shadow
        );
        guiGraphics.pose().popPose();
    }


}