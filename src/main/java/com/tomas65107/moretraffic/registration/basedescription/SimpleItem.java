package com.tomas65107.moretraffic.registration.basedescription;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SimpleItem extends Item {

    private final BaseDescriptionItem helper;

    public SimpleItem(Properties properties, boolean wrenchable, boolean colorable, boolean displayItemDescription) {
        super(properties);
        this.helper = new BaseDescriptionItem(wrenchable, colorable, displayItemDescription) {};
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        helper.addDescription(stack, tooltip);
    }
}