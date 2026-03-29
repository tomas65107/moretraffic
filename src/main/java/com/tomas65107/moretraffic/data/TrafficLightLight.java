package com.tomas65107.moretraffic.data;

import net.minecraft.world.item.DyeColor;

import java.nio.ByteBuffer;
import java.util.Base64;

public class TrafficLightLight {

    public TrafficLightLight(DyeColor color, TrafficLightMask mask) {
        this.color = color;
        this.mask = mask;
    }

    public DyeColor color; // BLACK = off
    public TrafficLightMask mask;

    public static class TrafficLightMask {
        private final short[] rows;

        public TrafficLightMask(short[] rows) {
            this.rows = rows.clone(); // safe copy
        }

        public TrafficLightMask() {
            this.rows = new short[16];
        }

        public boolean get(int x, int y) {
            return ((rows[y] >>> x) & 1) != 0;
        }

        public void set(int x, int y, boolean covered) {
            if (covered)
                rows[y] |= (1 << x);
            else
                rows[y] &= ~(1 << x);
        }

        public String serialize() {
            if (getRows() == null) return "";
            ByteBuffer buffer = ByteBuffer.allocate(rows.length * 2);
            for (short row : rows) buffer.putShort(row);
            return Base64.getEncoder().encodeToString(buffer.array());
        }

        public static TrafficLightMask deserialize(String maskEncoded) {
            if (maskEncoded.isEmpty()) return new TrafficLightMask();
            byte[] bytes = Base64.getDecoder().decode(maskEncoded);
            if (bytes.length != 32) return null;

            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            short[] rows = new short[16];
            for (int i = 0; i < 16; i++)
                rows[i] = buffer.getShort();

            return new TrafficLightMask(rows);
        }

        // In TrafficLightMask
        public short[] getRows() {
            return rows;
        }

        public void setRow(int y, short row) {
            rows[y] = row;
        }

    }
}