package com.tomas65107.moretraffic.gui.components;

import com.tomas65107.moretraffic.gui.tooltip.NoticeBoxTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

import static com.tomas65107.moretraffic.data.SpritesManager.renderSprite;

public class HelpElementWidget extends AbstractWidget {

    private final ResourceLocation sprite;
    private final NoticeBoxTooltip tooltip;

    public HelpElementWidget(int x, int y, ResourceLocation sprite, NoticeBoxTooltip tooltip) {
        super(x, y, 16, 16, Component.empty());
        this.sprite = sprite;
        this.tooltip = tooltip;
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderSprite(g, sprite, getX(), getY());

        if (isHovered() && tooltip != null) {
            g.renderTooltip(Minecraft.getInstance().font, List.of(Component.empty()), Optional.of(tooltip), ItemStack.EMPTY, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
