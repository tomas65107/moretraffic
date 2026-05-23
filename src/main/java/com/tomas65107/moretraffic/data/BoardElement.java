package com.tomas65107.moretraffic.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.tomas65107.moretraffic.helpers.ColorHelper.rgb;

public abstract class BoardElement {
    public int startX;
    public int startY;
    public abstract void render(GuiGraphics guiGraphics, int x, int y);
    public abstract String serialize();

    private BoardElement(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
    }

    public static class Text extends BoardElement {
        public Component text;

        Text(int startX, int startY,  Component text) {
            super(startX, startY);
            this.text = text;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int x, int y) {
            guiGraphics.drawString(Minecraft.getInstance().font, text, startX, startY, rgb(255, 255, 255));
        }

        @Override
        public String serialize() {return text.getString();}

        public Object deserialize(String serialized) {
            return null;
        }
    }

    public static class Sprite extends BoardElement {
        public List<DyeColor> rows;

        public Sprite(int startX, int startY) {
            super(startX, startY);
            this.rows = new ArrayList<>(Collections.nCopies(256, DyeColor.BLACK));
        }

        public Sprite(int startX, int startY, List<DyeColor> rows) {
            super(startX, startY);
            this.rows = rows;
        }

        public DyeColor get(int x, int y) {
            if (x < 0 || x >= 16 || y < 0 || y >= 16) throw new IndexOutOfBoundsException("Pixel getter input out of bounds");
            return rows.get(y * 16 + x);
        }

        public void set(int x, int y, DyeColor color) {
            if (x < 0 || x >= 16 || y < 0 || y >= 16) throw new IndexOutOfBoundsException("Pixel getter input out of bounds");
            rows.set(y * 16 + x, color);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int x, int y) {

        }

        @Override
        public String serialize() {
            StringBuilder sb = new StringBuilder(256);
            for (DyeColor pixel : rows) {
                sb.append(Integer.toHexString(pixel.getId()));
            }
            return sb.toString();
        }

        public static Sprite deserialize(String data, int startX, int startY) {
            if (data.length() != 256) throw new IllegalArgumentException("Invalid pixel data length: " + data.length());

            Sprite pixels = new Sprite(startX, startY);
            for (int i = 0; i < 256; i++) {
                int id = Character.digit(data.charAt(i), 16);
                pixels.rows.set(i, DyeColor.byId(id));
            }
            return pixels;
        }
    }
}
