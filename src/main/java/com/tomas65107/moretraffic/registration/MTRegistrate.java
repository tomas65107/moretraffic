package com.tomas65107.moretraffic.registration;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tomas65107.moretraffic.block.*;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import com.tomas65107.moretraffic.mod.MoreTrafficCompat;
import com.tomas65107.moretraffic.registration.basedescription.SimpleItem;
import com.tomas65107.moretraffic.rendering.DerailerRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;

import static com.simibubi.create.AllBlocks.CUCKOO_CLOCK;

public class MTRegistrate {
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MoreTraffic.MODID);

    public static final BlockEntry<AdvancedTrafficLightBlock> ADV_3_TRAFFIC_LIGHT = REGISTRATE.block("advanced_3_traffic_light", AdvancedTrafficLightBlock::new).register();
    public static final BlockEntry<AdvancedTrafficLightBlock> ADV_2_TRAFFIC_LIGHT = REGISTRATE.block("advanced_2_traffic_light", AdvancedTrafficLightBlock::new).register();
    public static final BlockEntry<AdvancedTrafficLightBlock> ADV_1_TRAFFIC_LIGHT = REGISTRATE.block("advanced_1_traffic_light", AdvancedTrafficLightBlock::new).register();
    public static final BlockEntityEntry<AdvancedTrafficLightBlockEntity> ADV_TRAFFIC_LIGHT_BE = REGISTRATE
            .blockEntity("advanced_traffic_light", AdvancedTrafficLightBlockEntity::new)
            .validBlocks(ADV_3_TRAFFIC_LIGHT, ADV_2_TRAFFIC_LIGHT, ADV_1_TRAFFIC_LIGHT)
            .register();


    public static final BlockEntry<TrafficDisplayBlock> TRAFFIC_DISPLAY = REGISTRATE
            .block("traffic_display", TrafficDisplayBlock::new)
            .item()
            .onRegisterAfter(Registries.ITEM, item ->
                    TooltipModifier.REGISTRY.register(item, new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)))
            .removeTab(CreativeModeTabs.SEARCH)
            .build()
            .register();
    public static final BlockEntityEntry<TrafficDisplayBlockEntity> TRAFFIC_DISPLAY_BE = REGISTRATE
            .blockEntity("traffic_display", TrafficDisplayBlockEntity::new)
            .validBlock(TRAFFIC_DISPLAY)
            .register();

    public static final BlockEntry<FlashingBlinkerBlock> BLINKER = REGISTRATE
            .block("blinker", FlashingBlinkerBlock::new)
            .item()
            .onRegisterAfter(Registries.ITEM, item ->
                    TooltipModifier.REGISTRY.register(item, new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)))
            .removeTab(CreativeModeTabs.SEARCH)
            .build()
            .register();
    public static final BlockEntityEntry<FlashingBlinkerBlockEntity> BLINKER_BE = REGISTRATE
            .blockEntity("blinker", FlashingBlinkerBlockEntity::new)
            .validBlock(BLINKER)
            .register();

    public static final BlockEntry<LightControlCabinetBlock> LIGHT_CONTROL_CABINET = REGISTRATE
            .block("control_cabinet", LightControlCabinetBlock::new)
            .item()
            .removeTab(CreativeModeTabs.SEARCH)
            .build()
            .register();
    public static final BlockEntityEntry<LightControlCabinetBlockEntity> LIGHT_CONTROL_CABINET_BE = REGISTRATE
            .blockEntity("control_cabinet", LightControlCabinetBlockEntity::new)
            .validBlock(LIGHT_CONTROL_CABINET)
            .register();

    public static final BlockEntry<DerailerBlock> DERAILER = REGISTRATE
            .block("derailer", DerailerBlock::new)
            .item(TrackTargetingBlockItem.ofType(EdgePointType.OBSERVER))
            .removeTab(CreativeModeTabs.SEARCH)
            .onRegisterAfter(Registries.ITEM, item ->
                    TooltipModifier.REGISTRY.register(item, new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)))
            .build()
            .register();
    public static final BlockEntityEntry<DerailerBlockEntity> DERAILER_BE = REGISTRATE
            .blockEntity("derailer", DerailerBlockEntity::new)
//            .visual(() -> DerailerVisual::new)
            .validBlock(DERAILER)
            .register();

    public static final BlockEntry<TrafficTrussBlock> TRAFFIC_TRUSS = REGISTRATE
            .block("traffic_truss", TrafficTrussBlock::new)
            .item()
            .removeTab(CreativeModeTabs.SEARCH)
            .build()
            .register();

    public static final BlockEntry<TrafficTrussBlock> TRAFFIC_TRUSS_WALKWAY = REGISTRATE
            .block("traffic_truss_walkway", TrafficTrussBlock::new)
            .item()
            .removeTab(CreativeModeTabs.SEARCH)
            .build()
            .register();

    public static final BlockEntry<TrafficPillarBlock> TRAFFIC_PILLAR = REGISTRATE
            .block("traffic_pillar", TrafficPillarBlock::new)
            .item()
            .removeTab(CreativeModeTabs.SEARCH)
            .build()
            .register();

    public static final BlockEntry<BallastBlock> BALLAST_GRAY = REGISTRATE
            .block("ballast_gray", BallastBlock::new)
            .item()
            .removeTab(CreativeModeTabs.SEARCH)
            .build()
            .register();

    public static final BlockEntry<BallastBlock> BALLAST_BROWN = REGISTRATE
            .block("ballast_brown", BallastBlock::new)
            .item()
            .removeTab(CreativeModeTabs.SEARCH)
            .build()
            .register();

    public static final BlockEntry<LEDStripBlock> LEDSTRIP = REGISTRATE
            .block("led_light", LEDStripBlock::new)
            .item()
            .onRegisterAfter(Registries.ITEM, item ->
                    TooltipModifier.REGISTRY.register(item, new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)))
            .removeTab(CreativeModeTabs.SEARCH)
            .build()
            .register();
    public static final BlockEntityEntry<LEDStripBlockEntity> LED_STRIP_BE = REGISTRATE
            .blockEntity("led_light", LEDStripBlockEntity::new)
            .validBlock(LEDSTRIP)
            .register();

    public static final ItemEntry<Item> LIGHT_DIODE = REGISTRATE.
            item("light", Item::new)
            .onRegisterAfter(Registries.ITEM, item ->
                    TooltipModifier.REGISTRY.register(item, new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)))
            .removeTab(CreativeModeTabs.SEARCH)
            .register();

    public static void register(IEventBus modEventBus) {
    }

}