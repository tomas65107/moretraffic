package com.tomas65107.moretraffic.gui.tooltip;

import com.tomas65107.moretraffic.data.ColorsManager;
import com.tomas65107.moretraffic.data.helpers.TextHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static com.tomas65107.moretraffic.data.helpers.ColorHelper.rgb;
import static com.tomas65107.moretraffic.data.helpers.TextCutter.cutTextComponent;
import static com.tomas65107.moretraffic.data.helpers.TextCutter.joinTogetherComponents;

public class NoticeBoxTooltip implements TooltipComponent {
    Component title;
    List<Component> message;
    List<Component> cta;
    Color color;
    boolean makeCtaYellow;

    public NoticeBoxTooltip(Component title) {
        this(title, null, null, null, false);
    }

    public NoticeBoxTooltip(Component title, Color color) {
        this(title, null, null, color, false);
    }

    public NoticeBoxTooltip(Component title, Component message) {
        this(title, message, null, null, false);
    }

    public NoticeBoxTooltip(Component title, Component message, Component cta) {
        this(title, message, cta, null, false);
    }

    public NoticeBoxTooltip(Component title, Component message, Component cta, Color color, boolean makeCtaYellow) {
        this.title = joinTogetherComponents(cutTextComponent(title, false), false);
        this.message = cutTextComponent(message, true);
        this.cta = cutTextComponent(cta, true);
        this.color = color;
        this.makeCtaYellow = makeCtaYellow;
    }

    public static class Client implements ClientTooltipComponent {
        private final NoticeBoxTooltip tooltip;

        public Client(NoticeBoxTooltip tooltip) {
            this.tooltip = tooltip;
        }

        private int render(int x, int y, Object gfx, String returningStatement) {
            if (tooltip.color == null) {tooltip.color = ColorsManager.PRIMARY;}

            double yTotal = 0;
            int xTotal = 0;

            y = y - Minecraft.getInstance().font.lineHeight - 3;

            if (!tooltip.title.getString().isEmpty()) {
                if (gfx instanceof GuiGraphics) {
                    TextHelper.renderText((GuiGraphics) gfx, x, y, tooltip.title, 1f, rgb(tooltip.color), true);
                }
                yTotal += Minecraft.getInstance().font.lineHeight;
                xTotal = Math.max(xTotal, Minecraft.getInstance().font.width(tooltip.title));

                if (Objects.equals(String.valueOf(tooltip.message), "[empty]") && Objects.equals(String.valueOf(tooltip.cta), "[empty]")) {} else {
                    yTotal += 4f;
                }
            }

            float scaleForTs;
            if(!Objects.equals(String.valueOf(tooltip.message), "[empty]")) {
                scaleForTs = 1f;
                for (Component line : tooltip.message) {
                    if (gfx instanceof GuiGraphics) {
                        TextHelper.renderText((GuiGraphics) gfx, x, (int) (y + yTotal), line, scaleForTs, rgb(ColorsManager.SECONDARY), true);
                    }
                    yTotal += Minecraft.getInstance().font.lineHeight * scaleForTs;
                    xTotal = (int) Math.max(xTotal, Minecraft.getInstance().font.width(line) * scaleForTs);
                }
            }

            if (!Objects.equals(String.valueOf(tooltip.cta), "[empty]")) {
                scaleForTs = 0.87f;
                yTotal += 4f;
                for (Component line : tooltip.cta) {
                    if (gfx instanceof GuiGraphics) {
                        TextHelper.renderText((GuiGraphics) gfx, x + 4, (int) (y + yTotal), line, scaleForTs, rgb(tooltip.makeCtaYellow ? Color.YELLOW : ColorsManager.TERTIARY), true);
                    }
                    yTotal += Minecraft.getInstance().font.lineHeight * scaleForTs;
                    xTotal = (int) Math.max(xTotal, Minecraft.getInstance().font.width(line) * scaleForTs);
                }
                yTotal += 1f;
            }
            yTotal += 1f;


            return (int) switch (returningStatement) {
                case "x" -> xTotal;
                case "y" -> yTotal - Minecraft.getInstance().font.lineHeight - 3;
                case "render" -> 1;
                default -> throw new IllegalArgumentException("just pass in 'y', 'x' or 'render'");
            };

        }

        @Override
        public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
            render(x, y, guiGraphics, "render");
        }

        @Override
        public int getHeight() {
            return render(0, 0, null, "y");
        }

        @Override
        public int getWidth(Font font) {
            return render(0, 0, null, "x");
        }
    }
}