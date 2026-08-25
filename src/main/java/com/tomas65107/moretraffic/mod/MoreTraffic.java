package com.tomas65107.moretraffic.mod;

import com.mojang.logging.LogUtils;
import com.tomas65107.moretraffic.mod.registration.MTNetworking;
import com.tomas65107.moretraffic.mod.registration.MTRegistrate;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import static com.tomas65107.moretraffic.mod.registration.CreativeTab.CREATIVE_MODE_TABS;
import static com.tomas65107.moretraffic.mod.registration.MTMenus.MENUS;
import static com.tomas65107.moretraffic.mod.registration.MTRegistrate.REGISTRATE;

@Mod(MoreTraffic.MODID)
public class MoreTraffic {
    public static final String MODID = "moretraffic";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MoreTraffic() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        REGISTRATE.registerEventListeners(modEventBus);
        MTRegistrate.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        MENUS.register(modEventBus);
        MTNetworking.register();
        MoreTrafficCompat.init();

        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
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
