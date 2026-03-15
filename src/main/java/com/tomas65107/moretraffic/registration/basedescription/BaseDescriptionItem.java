package com.tomas65107.moretraffic.registration.basedescription;

import com.tomas65107.moretraffic.data.helpers.TextCutter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.List;

import static com.tomas65107.moretraffic.data.helpers.ColorHelper.rgb;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GRAY;

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
            for (var c : TextCutter.cutTextComponent(Component.translatable(key), false)) tooltip.add(c.copy().withStyle(DARK_GRAY));
        }

        var flags = Component.empty();
        if (wrenchable) flags.append(Component.translatable("core.description.wrenchable").withStyle(ChatFormatting.ITALIC).withColor(rgb(new Color(116, 155, 182))));
        if (wrenchable && colorable) flags.append("  ");
        if (colorable) flags.append(Component.translatable("core.description.colorable").withStyle(ChatFormatting.ITALIC).withColor(rgb(new Color(165, 188, 96))));

        if (!flags.equals(Component.empty())) tooltip.add(flags);

    }
}