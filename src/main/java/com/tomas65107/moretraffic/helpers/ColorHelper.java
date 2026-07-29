package com.tomas65107.moretraffic.helpers;

import net.minecraft.world.item.DyeColor;

import java.awt.*;

public final class ColorHelper {

    /**
     * Converts Color (ARGB) to Minecraft ARGB integer.
     *
     * @param color   color component
     * @return ARGB integer usable in Minecraft
     */
    public static int rgb(Color color) {
        return ((color.getAlpha() & 0xFF) << 24) |
                ((color.getRed() & 0xFF) << 16) |
                ((color.getGreen() & 0xFF) << 8) |
                (color.getBlue() & 0xFF);
    }
    /**
     * Converts ARGB values to Minecraft ARGB integer.
     *
     * @param red   Red component (0-255)
     * @param green Green component (0-255)
     * @param blue  Blue component (0-255)
     * @param alpha Alpha component (0-255), optional (default 255 = fully opaque)
     * @return ARGB integer usable in Minecraft
     */
    public static int rgb(int red, int green, int blue, int alpha) {
        return ((alpha & 0xFF) << 24) |    // Alpha
                ((red & 0xFF) << 16) |     // Red
                ((green & 0xFF) << 8) |    // Green
                (blue & 0xFF);             // Blue
    }

    /**
     * Converts RGB values to Minecraft ARGB integer.
     *
     * @param red   Red component (0-255)
     * @param green Green component (0-255)
     * @param blue  Blue component (0-255)
     * <p>Alpha component not specified, optional
     * @return ARGB integer usable in Minecraft
     */
    public static int rgb(int red, int green, int blue) {
        return rgb(red, green, blue, 255);
    }

    public static int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    public static int textureDiffuseColor(DyeColor color) {
        float[] components = color.getTextureDiffuseColors();
        return rgb(
                Math.round(components[0] * 255.0F),
                Math.round(components[1] * 255.0F),
                Math.round(components[2] * 255.0F)
        );
    }
}
