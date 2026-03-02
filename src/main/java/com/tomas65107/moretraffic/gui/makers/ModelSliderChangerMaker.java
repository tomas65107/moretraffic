package com.tomas65107.moretraffic.gui.makers;

import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlockEntity;
import com.tomas65107.moretraffic.data.trafficlightproperties.TrafficLightProperty;
import com.tomas65107.moretraffic.gui.components.buttons.ModelButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.tomas65107.moretraffic.data.helpers.BlockStateHelper.getProperty;

public final class ModelSliderChangerMaker<T extends Enum<T> & TrafficLightProperty> {

    public ModelSliderChangerMaker(int x, int y,
                                   AdvancedTrafficLightBlockEntity be,
                                   Class<T> propertyClass,
                                   Consumer<AbstractWidget> addToRender,
                                   Consumer<Boolean> shouldQueueRefresh,
                                   Supplier<Boolean> shouldRenderTooltips) {
        int cursorX = x;

        for (TrafficLightProperty value : propertyClass.getEnumConstants()) {
            ModelButton button = new ModelButton(cursorX, y, 16, 16, be, value, selected(be.getBlockState(), value), shouldQueueRefresh, shouldRenderTooltips.get());
            cursorX += selected(be.getBlockState(), value) ? (16 + 3) : (16 + 2);

            addToRender.accept(button);
        }

    }

    private static boolean selected(BlockState currentBlockState, TrafficLightProperty value) {
        var property = getProperty(currentBlockState, value.getClassType().getNameOfProperty());
        if (property == null) return false;
        return currentBlockState.hasProperty(property) && Objects.equals(currentBlockState.getValue(property), value);
    }

}
