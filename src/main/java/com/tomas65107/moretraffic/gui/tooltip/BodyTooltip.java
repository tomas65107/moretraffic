package com.tomas65107.moretraffic.gui.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.function.Consumer;

public class BodyTooltip implements TooltipComponent {
    Consumer<TooltipContext> renderItems;
    public BodyTooltip(Consumer<TooltipContext> renderItems) {
        this.renderItems = renderItems;
    }

    public static class TooltipContext {
        public final Object guiGraphics;
        public final int x;
        public final int y;
        public int yTotal;
        public int xTotal;

        public TooltipContext(Object guiGraphics, int x, int y, int xTotal, int yTotal) {
            this.guiGraphics = guiGraphics;
            this.x = x;
            this.y = y;
            this.xTotal = xTotal;
            this.yTotal = yTotal;
        }
    }

    public static class Client implements ClientTooltipComponent {
        private final BodyTooltip tooltip;

        public Client(BodyTooltip tooltip) {
            this.tooltip = tooltip;
        }

        public int render(int x, int y, Object guiGraphics, String returningStatement) {
            TooltipContext context = new TooltipContext(guiGraphics, x, y, 0, 0);
            tooltip.renderItems.accept(context);

            return switch (returningStatement) {
                case "x" -> context.xTotal;
                case "y" -> context.yTotal;
                case "render" -> 1;
                default -> throw new IllegalArgumentException("just pass in 'y', 'x' or 'render'");
            };
        }

        @Override
        public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
            render(x, y, guiGraphics, "render");
        }

        @Override
        public int getHeight() { return render(0, 0, null, "y"); }

        @Override
        public int getWidth(Font font) { return render(0, 0, null, "x"); }
    }
}