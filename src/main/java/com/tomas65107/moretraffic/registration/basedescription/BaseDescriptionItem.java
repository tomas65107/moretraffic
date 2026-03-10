package com.tomas65107.moretraffic.registration.basedescription;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class BaseDescriptionItem {

    protected final boolean displayItemDescription;
    protected final boolean wrenchable;
    protected final boolean colorable;

    protected BaseDescriptionItem(boolean wrenchable, boolean colorable, boolean displayItemDescription) {
        this.wrenchable = wrenchable;
        this.colorable = colorable;
        this.displayItemDescription = displayItemDescription;
    }

    protected void addDescription(ItemStack stack, List<Component> tooltip) {
        if (Minecraft.getInstance().level == null) return;
        if (displayItemDescription) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            String type = stack.getItem() instanceof BlockItem ? "block" : "item";

            String key = type + "." + id.getNamespace() + "." + id.getPath() + ".description";
            tooltip.add(Component.translatable(key));
        }

        var flags = Component.empty();
        if (wrenchable) flags.append(Component.translatable("core.description.wrenchable").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
        if (wrenchable && colorable) flags.append("  ");
        if (colorable) flags.append(Component.translatable("core.description.colorable").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
        tooltip.add(flags);

    }
}