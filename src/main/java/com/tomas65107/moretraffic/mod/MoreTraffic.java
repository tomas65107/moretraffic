package com.tomas65107.moretraffic.mod;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import static com.tomas65107.moretraffic.registration.CreativeTab.CREATIVE_MODE_TABS;
import static com.tomas65107.moretraffic.registration.MTBE.BLOCK_ENTITIES;
import static com.tomas65107.moretraffic.registration.MTBlocks.BLOCKS;
import static com.tomas65107.moretraffic.registration.MTItems.ITEMS;
import static com.tomas65107.moretraffic.registration.MTMenus.MENUS;

@Mod(MoreTraffic.MODID)
public class MoreTraffic {
    public static final String MODID = "moretraffic";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MoreTraffic(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        MENUS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("MoreTraffic commonSetup");
        LOGGER.warn("MORETRAFFIC IS IN ALPHA\nSome things are not finished and things may crash or break.\nPlease PLEASE make sure you backup any important worlds, you are responsible for corruption.\nMod is in early stage of development");
        TrafficCraftCompat.init();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
