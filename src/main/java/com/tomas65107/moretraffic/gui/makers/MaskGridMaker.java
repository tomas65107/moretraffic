package com.tomas65107.moretraffic.gui.makers;

import com.tomas65107.moretraffic.data.TrafficLightLight;
import com.tomas65107.moretraffic.gui.components.buttons.PixelButton;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.Arrays;
import java.util.function.Consumer;

public final class MaskGridMaker {

    private int pressAction = 0;

    short[] newRows;

    public MaskGridMaker(
            int x, int y,
            TrafficLightLight.TrafficLightMask MaskData,
            Consumer<AbstractWidget> addToRender,
            Consumer<TrafficLightLight.TrafficLightMask> newMask
    ) {
        newRows = MaskData.getRows().clone();
        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {

                boolean enabled = MaskData.get(col, row);

                int px = x + col * 8;
                int py = y + row * 8;

                int finalRow = row;
                int finalCol = col;
                PixelButton button = new PixelButton(
                        px, py,
                        8, 8,
                        enabled,
                        flipIt -> {
                            if (!flipIt) return;

                            newRows[finalRow] ^= (short) (1 << finalCol);
                        },
                        v -> {
                            pressAction = v;
                            if (pressAction ==0 && !Arrays.equals(newRows, MaskData.getRows().clone())) {
                                newMask.accept(new TrafficLightLight.TrafficLightMask(newRows));
                                newRows = MaskData.getRows().clone();
                            }
                        }
                );
                addToRender.accept(button);
            }
        }
    }
}
