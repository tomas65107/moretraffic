package com.tomas65107.moretraffic.registration;

import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MTItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MoreTraffic.MODID);

    public static final DeferredItem<BlockItem> ADV_3_TRAFFIC_LIGHT_ITEM = ITEMS.registerSimpleBlockItem(MTBlocks.ADV_3_TRAFFIC_LIGHT);
    public static final DeferredItem<BlockItem> ADV_2_TRAFFIC_LIGHT_ITEM = ITEMS.registerSimpleBlockItem(MTBlocks.ADV_2_TRAFFIC_LIGHT);
    public static final DeferredItem<BlockItem> ADV_1_TRAFFIC_LIGHT_ITEM = ITEMS.registerSimpleBlockItem(MTBlocks.ADV_1_TRAFFIC_LIGHT);

    public static final DeferredItem<BlockItem> LIGHT_CONTROL_CABINET = ITEMS.registerSimpleBlockItem(MTBlocks.LIGHT_CONTROL_CABINET);

    public static final DeferredItem<Item> LIGHT_DIODE = ITEMS.registerItem("light_diode",
            Item::new
    );

}