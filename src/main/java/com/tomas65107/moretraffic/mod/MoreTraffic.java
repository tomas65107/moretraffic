package com.tomas65107.moretraffic.mod;

import com.tomas65107.moretraffic.registration.MTRegistrate;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import static com.tomas65107.moretraffic.registration.CreativeTab.CREATIVE_MODE_TABS;
import static com.tomas65107.moretraffic.registration.MTMenus.MENUS;
import static com.tomas65107.moretraffic.registration.MTRegistrate.REGISTRATE;

@Mod(MoreTraffic.MODID)
public class MoreTraffic {
    public static final String MODID = "moretraffic";
    public static final Logger LOGGER = LogUtils.getLogger();


    public MoreTraffic(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        REGISTRATE.registerEventListeners(modEventBus);
        MTRegistrate.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        MENUS.register(modEventBus);
        MoreTrafficCompat.init();

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("MoreTraffic commonSetup");
        LOGGER.warn("\nMORETRAFFIC IS IN ALPHA\nSome things are not finished and things may crash or break.\nPlease PLEASE make sure you backup any important worlds, you are responsible for corruption.\nMod is in early stage of development");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
