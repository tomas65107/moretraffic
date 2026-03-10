package com.tomas65107.moretraffic.registration.basedescription;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class SimpleBlockItem extends BlockItem {

    private final BaseDescriptionItem helper;

    public SimpleBlockItem(Block block, Properties properties, boolean wrenchable, boolean colorable, boolean displayItemDescription) {

        super(block, properties);
        this.helper = new BaseDescriptionItem(wrenchable, colorable, displayItemDescription) {};
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        helper.addDescription(stack, tooltip);
    }
}
