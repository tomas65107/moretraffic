package com.tomas65107.moretraffic.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/** Forge 1.20.1 checkbox with the value-change callback used by the original GUI. */
public class CallbackCheckbox extends Checkbox {
    private final Consumer<Boolean> onValueChange;

    public CallbackCheckbox(int x, int y, Component message, boolean selected, Consumer<Boolean> onValueChange) {
        super(x, y, Minecraft.getInstance().font.width(message) + 24, 20, message, selected);
        this.onValueChange = onValueChange;
    }

    @Override
    public void onPress() {
        super.onPress();
        onValueChange.accept(selected());
    }
}