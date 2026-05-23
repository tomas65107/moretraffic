package com.tomas65107.moretraffic.gui.tooltip;

import com.tomas65107.moretraffic.data.ColorsManager;
import com.tomas65107.moretraffic.data.SpritesManager;
import com.tomas65107.moretraffic.helpers.TextHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static com.tomas65107.moretraffic.helpers.ColorHelper.rgb;
import static com.tomas65107.moretraffic.helpers.TextCutter.cutTextComponent;

public class NoticeBoxTooltip implements TooltipComponent {

    public enum TooltipType {
        BASE,
        INFORMATIVE,
        WARNING,
        ERROR,
        CTA_SPECIAL
    }

    List<Component> title;
    List<Component> message;
    List<Component> cta;
    TooltipType type;

    public NoticeBoxTooltip(Component title) {
        this(title, null, null, TooltipType.BASE);
    }

    public NoticeBoxTooltip(Component title, TooltipType type) {
        this(title, null, null, type);
    }

    public NoticeBoxTooltip(Component title, Component message) {
        this(title, message, null, TooltipType.BASE);
    }

    public NoticeBoxTooltip(Component title, Component message, TooltipType type) {
        this(title, message, null, type);
    }

    public NoticeBoxTooltip(Component title, Component message, Component cta, TooltipType type) {
        this.title = cutTextComponent(title, false);
        this.message = cutTextComponent(message, true);
        this.cta = cutTextComponent(cta, true);
        this.type = type == null ? TooltipType.BASE : type;
    }

    public static class Client implements ClientTooltipComponent {
        private final NoticeBoxTooltip tooltip;

        public Client(NoticeBoxTooltip tooltip) {
            this.tooltip = tooltip;
        }

        private int render(int x, int y, Object gfx, String returningStatement) {
            Color color = Color.black;
            switch (tooltip.type) {
                case BASE, CTA_SPECIAL -> color = ColorsManager.PRIMARY;
                case INFORMATIVE -> color = ColorsManager.HEADER;
                case WARNING -> color = ColorsManager.HEADER_WARNING;
                case ERROR -> color = ColorsManager.ERROR;
            }
            double yTotal = 0;
            int xTotal = 0;

            y = y - Minecraft.getInstance().font.lineHeight - 3;

            var additionalSpaceForPictogram = 0;
            if (tooltip.type == TooltipType.WARNING || tooltip.type == TooltipType.ERROR) {

                if (gfx instanceof GuiGraphics) {
                    SpritesManager.renderSprite((GuiGraphics) gfx, tooltip.type == TooltipType.WARNING ? SpritesManager.WARNING : SpritesManager.ERROR, x, y-1, 10);
                }
                additionalSpaceForPictogram = 13;
            }

            if(!Objects.equals(String.valueOf(tooltip.title), "[empty]")) {
                for (Component line : tooltip.title) {
                    if (gfx instanceof GuiGraphics) {
                        TextHelper.renderText((GuiGraphics) gfx, x + additionalSpaceForPictogram, (int) (y + yTotal), line, 1f, rgb(color), true);
                    }
                    yTotal += Minecraft.getInstance().font.lineHeight;
                    xTotal = Math.max(xTotal, Minecraft.getInstance().font.width(line) + additionalSpaceForPictogram);
                }
                if (Objects.equals(String.valueOf(tooltip.message), "[empty]") && Objects.equals(String.valueOf(tooltip.cta), "[empty]")) {
                } else {
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
                        TextHelper.renderText((GuiGraphics) gfx, x + 4, (int) (y + yTotal), line, scaleForTs, rgb(tooltip.type.equals(TooltipType.CTA_SPECIAL) ? Color.YELLOW : ColorsManager.TERTIARY), true);
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