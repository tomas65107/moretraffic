package com.tomas65107.moretraffic.data.helpers;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MaskConverter {

    public static String openResourcePopUp() {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            PointerBuffer filterPatterns = stack.mallocPointer(5);
            filterPatterns.put(stack.UTF8("*.png"));
            filterPatterns.put(stack.UTF8("*.jpg"));
            filterPatterns.put(stack.UTF8("*.jpeg"));
            filterPatterns.put(stack.UTF8("*.gif"));
            filterPatterns.put(stack.UTF8("*.bmp"));
            filterPatterns.flip();

            return TinyFileDialogs.tinyfd_openFileDialog(
                    "Select 16x16 image to convert to a mask",
                    null,
                    filterPatterns,
                    "16x16 Image Files",
                    false
            );
        }
    }

    public static short[] convertImage(File file) throws Exception {

        BufferedImage img = ImageIO.read(file);

        // resize to 16x16
        BufferedImage resized = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(img, 0, 0, 16, 16, null);
        g.dispose();

        short[] rows = new short[16];

        for (int y = 0; y < 16; y++) {

            short row = 0;

            for (int x = 0; x < 16; x++) {

                int rgb = resized.getRGB(x, y);

                int alpha = (rgb >> 24) & 255;

                // transparent = 0, non‑transparent = 1
                if (alpha > 0) {
                    row |= (1 << x);
                }
            }

            rows[y] = row;
        }

        return rows;
    }

    public static String toExportFormat(short[] rows) {

        StringBuilder sb = new StringBuilder();

        for (short r : rows) {
            sb.append(r).append(",");
        }

        return sb.toString();
    }
}
