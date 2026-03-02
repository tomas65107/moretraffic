package com.tomas65107.moretraffic.data;

import net.minecraft.world.item.DyeColor;

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

        // In TrafficLightMask
        public short[] getRows() {
            return rows;
        }

        public void setRow(int y, short row) {
            rows[y] = row;
        }

    }
}