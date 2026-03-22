package com.tomas65107.moretraffic.registration;

import com.tomas65107.moretraffic.mod.MoreTraffic;
import com.tomas65107.moretraffic.registration.basedescription.SimpleBlockItem;
import com.tomas65107.moretraffic.registration.basedescription.SimpleItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


public class MTItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MoreTraffic.MODID);

    public static final DeferredItem<BlockItem> ADV_3_TRAFFIC_LIGHT_ITEM = ITEMS.registerSimpleBlockItem(MTBlocks.ADV_3_TRAFFIC_LIGHT);
    public static final DeferredItem<BlockItem> ADV_2_TRAFFIC_LIGHT_ITEM = ITEMS.registerSimpleBlockItem(MTBlocks.ADV_2_TRAFFIC_LIGHT);
    public static final DeferredItem<BlockItem> ADV_1_TRAFFIC_LIGHT_ITEM = ITEMS.registerSimpleBlockItem(MTBlocks.ADV_1_TRAFFIC_LIGHT);
    public static final DeferredItem<BlockItem> TRAFFIC_DISPLAY_ITEM = ITEMS.register("traffic_display", () -> new SimpleBlockItem(MTBlocks.TRAFFIC_DISPLAY.get(), new Item.Properties(), false, false, true));

    public static final DeferredItem<SimpleBlockItem> BLINKER = ITEMS.register("blinker", () -> new SimpleBlockItem(MTBlocks.BLINKER.get(), new Item.Properties(), false, true, true));

    public static final DeferredItem<SimpleBlockItem> LIGHT_CONTROL_CABINET = ITEMS.register("control_cabinet", () -> new SimpleBlockItem(MTBlocks.LIGHT_CONTROL_CABINET.get(), new Item.Properties(), true, false, true));
    public static final DeferredItem<BlockItem> TRAFFIC_TRUSS = ITEMS.registerSimpleBlockItem(MTBlocks.TRAFFIC_TRUSS);
    public static final DeferredItem<BlockItem> TRAFFIC_TRUSS_WALKWAY = ITEMS.registerSimpleBlockItem(MTBlocks.TRAFFIC_TRUSS_WALKWAY);
    public static final DeferredItem<BlockItem> TRAFFIC_PILLAR = ITEMS.registerSimpleBlockItem(MTBlocks.TRAFFIC_PILLAR);

    public static final DeferredItem<BlockItem> BALLAST_GRAY = ITEMS.register("ballast_gray", () -> new SimpleBlockItem(MTBlocks.BALLAST_GRAY.get(), new Item.Properties(), true, false, false));

    public static final DeferredItem<BlockItem> LEDSTRIP = ITEMS.register("led_light", () -> new SimpleBlockItem(MTBlocks.LEDSTRIP.get(), new Item.Properties(), true, false, true));

    public static final DeferredItem<SimpleItem> LIGHT_DIODE = ITEMS.registerItem("light", (p) -> new SimpleItem(p, true, true, true));

}