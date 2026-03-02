package com.tomas65107.moretraffic.data.helpers;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class BlockStateHelper {


    public static BlockState setValueFromString(BlockState state, String propertyName, String valueName
    ) {
        Property<?> property = getProperty(state, propertyName);
        if (property == null) return state;

        Optional<?> parsed = property.getValue(valueName);
        if (parsed.isEmpty()) return state;

        return setUnchecked(state, property, parsed.get());
    }

    @Nullable
    public static Property<?> getProperty(BlockState state, String name) {
        return state.getBlock().getStateDefinition().getProperty(name);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> BlockState setUnchecked(BlockState state, Property<T> property, Object value) {
        return state.setValue(property, (T) value);
    }

}
