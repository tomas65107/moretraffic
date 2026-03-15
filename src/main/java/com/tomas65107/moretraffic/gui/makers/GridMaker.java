package com.tomas65107.moretraffic.gui.makers;

import com.tomas65107.moretraffic.gui.components.buttons.ColorButton;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.item.DyeColor;

import java.util.function.Consumer;

public final class GridMaker {

    public GridMaker(
            int x, int y,
            Consumer<AbstractWidget> addToRender,
            Consumer<DyeColor> onColorSelected,
            DyeColor currentlySelectedColor
    ) {
        this(x, y, addToRender, onColorSelected, currentlySelectedColor, false);
    }

    public GridMaker(
            int x, int y,
            Consumer<AbstractWidget> addToRender,
            Consumer<DyeColor> onColorSelected,
            DyeColor currentlySelectedColor,
            boolean showBlackMinimized
    ) {
        int startX = x;
        int gridX = startX;
        int gridY = y;

        for (DyeColor color : DyeColor.values()) {
            ColorButton button = new ColorButton(
                    gridX, gridY,
                    16, 16,
                    color.getTextureDiffuseColor(),
                    b -> onColorSelected.accept(color), currentlySelectedColor.equals(color),
                    showBlackMinimized
            );
            addToRender.accept(button);

            gridX += 17;
            if (gridX > startX + 8*17) {
                gridX = startX;
                gridY += 17;
            }
        }
    }
}