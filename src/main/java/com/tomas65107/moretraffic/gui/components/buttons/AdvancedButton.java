package com.tomas65107.moretraffic.gui.components.buttons;

import com.tomas65107.moretraffic.data.SpritesManager;
import com.tomas65107.moretraffic.data.helpers.TextHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class AdvancedButton extends Button {
    private final Component component;
    private final ResourceLocation pictogram;
    public TooltipComponent tooltipComponent;
    private final boolean hideBackgroundButton;

    public static final int NORMAL_HEIGHT = 20;

    public AdvancedButton(int x, int y, int width, int height, Component component, ResourceLocation pictogram, OnPress onPress) {
        this(x, y, width, height, component, pictogram, null, false, onPress);
    }

    public AdvancedButton(int x, int y, int width, int height, Component component, ResourceLocation pictogram, TooltipComponent tooltipComponent, OnPress onPress) {
        this(x, y, width, height, component, pictogram, tooltipComponent, false, onPress);
    }

    public AdvancedButton(int x, int y, int width, int height, Component component, TooltipComponent tooltipComponent, OnPress onPress) {
        this(x, y, width, height, component, null, tooltipComponent, false, onPress);
    }

    public AdvancedButton(int x, int y, int width, int height, ResourceLocation pictogram, TooltipComponent tooltipComponent, boolean hideButtonBackground, OnPress onPress) {
        this(x, y, width, height, Component.empty(), pictogram, tooltipComponent, hideButtonBackground, onPress);
    }

    public AdvancedButton(int x, int y, int width, int height, Component component, ResourceLocation pictogram, TooltipComponent tooltipComponent, boolean hideButtonBackground, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.component = component;
        this.pictogram = pictogram;
        this.tooltipComponent = tooltipComponent;
        this.hideBackgroundButton = hideButtonBackground;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!hideBackgroundButton) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }
        if (pictogram != null && hideBackgroundButton) {
            int centerX = getX() + getWidth() / 2 - 8;
            int centerY = getY() + getHeight() / 2 - 8;
            float spriteScale = 0.8f; // half-size
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(centerX + 8, centerY + 8, 0);
            guiGraphics.pose().scale(spriteScale, spriteScale, 1f);
            guiGraphics.pose().translate(-(centerX + 8), -(centerY + 8), 0);
            SpritesManager.renderSprite(guiGraphics, pictogram, centerX, centerY);
            guiGraphics.pose().popPose();
        } else if (pictogram != null) {
            SpritesManager.renderSprite(guiGraphics, pictogram, getX() + 2, getY() + 2);
        }
        if (component != null) {
            TextHelper.renderText(guiGraphics, getX() + (pictogram != null ? (2 + 16 + 2 + 2) : 6), getY() + 6, component);
        }

        if (tooltipComponent != null && isHovered()) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, List.of(Component.empty()), Optional.of(tooltipComponent), ItemStack.EMPTY, mouseX, mouseY);
        }

    }
}
