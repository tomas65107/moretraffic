package com.tomas65107.moretraffic.data;

import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrafficDisplayPixels {

    public final List<DyeColor> rows;

    public TrafficDisplayPixels(List<DyeColor> rows) {
        this.rows = rows;
    }

    public TrafficDisplayPixels() {
        this.rows = new ArrayList<>(Collections.nCopies(256, DyeColor.BLACK));
    }

    public DyeColor get(int x, int y) {
        if (x < 0 || x >= 16 || y < 0 || y >= 16) throw new IndexOutOfBoundsException("Pixel getter input out of bounds");
        return rows.get(y * 16 + x);
    }

    public void set(int x, int y, DyeColor color) {
        if (x < 0 || x >= 16 || y < 0 || y >= 16) throw new IndexOutOfBoundsException("Pixel getter input out of bounds");
        rows.set(y * 16 + x, color);
    }

    public List<DyeColor> getRowsAsList() {
        return rows;
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder(256);
        for (DyeColor pixel : rows) {
            sb.append(Integer.toHexString(pixel.getId()));
        }
        return sb.toString();
    }

    public static TrafficDisplayPixels deserialize(String data) {
        if (data.length() != 256) throw new IllegalArgumentException("Invalid pixel data length: " + data.length());

        TrafficDisplayPixels pixels = new TrafficDisplayPixels();
        for (int i = 0; i < 256; i++) {
            int id = Character.digit(data.charAt(i), 16);
            pixels.rows.set(i, DyeColor.byId(id));
        }
        return pixels;
    }

}
