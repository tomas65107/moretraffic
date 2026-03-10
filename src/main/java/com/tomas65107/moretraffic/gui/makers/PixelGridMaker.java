package com.tomas65107.moretraffic.gui.makers;

import com.tomas65107.moretraffic.data.TrafficDisplayPixels;
import com.tomas65107.moretraffic.gui.components.buttons.PixelButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class PixelGridMaker {

    public PixelGridMaker(
            int x, int y,
            TrafficDisplayPixels MaskData,
            Consumer<AbstractWidget> addToRender,
            Consumer<TrafficDisplayPixels> newMask,
            DyeColor painterColor
    ) {
        final TrafficDisplayPixels[] data = new TrafficDisplayPixels[1];

        data[0] = new TrafficDisplayPixels(new ArrayList<>(MaskData.rows));
        int index = 0;
        for (var pixel : data[0].rows) {
            int ix = index % 16;
            int iy = index / 16;

            boolean enabled = (data[0].get(ix, iy) != DyeColor.BLACK);

            int px = x + ix * 8;
            int py = y + iy * 8;

            PixelButton button = new PixelButton(
                    px, py,
                    8, 8,
                    enabled,
                    flipIt -> {
                        if (flipIt) {
                            data[0].set(ix, iy, painterColor);
                        } else {
                            data[0].set(ix, iy, DyeColor.BLACK);
                        }
                    },
                    pressAction -> {
                        if (pressAction == 0 && !Objects.equals(MaskData.serialize(), data[0].serialize())) {
                            newMask.accept(new TrafficDisplayPixels(new ArrayList<>(data[0].rows)));
                            data[0] = new TrafficDisplayPixels(new ArrayList<>(MaskData.rows));
                        }

                    },
                    data[0].get(ix, iy)
            );
            addToRender.accept(button);
            index++;
        }
    }
}
