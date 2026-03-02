package com.tomas65107.moretraffic.mod;

import com.tomas65107.moretraffic.gui.containers.AdvancedTrafficLightScreen;
import com.tomas65107.moretraffic.gui.containers.LightControlCabinetScreen;
import com.tomas65107.moretraffic.gui.tooltip.BodyTooltip;
import com.tomas65107.moretraffic.gui.tooltip.NoticeBoxTooltip;
import com.tomas65107.moretraffic.registration.MTBE;
import com.tomas65107.moretraffic.registration.MTBlocks;
import com.tomas65107.moretraffic.rendering.TrafficLightBlockEntityRenderer;
import de.mrjulsen.trafficcraft.client.TintedTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import static com.tomas65107.moretraffic.registration.MTMenus.CONTROL_CABINET_MENU;
import static com.tomas65107.moretraffic.registration.MTMenus.TRAFFIC_LIGHT_SETUP;

@Mod(value = MoreTraffic.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MoreTraffic.MODID, value = Dist.CLIENT)
public class MoreTrafficClient {

    public MoreTrafficClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        //BE register
        BlockEntityRenderers.register(
                MTBE.ADVANCED_TRAFFIC_LIGHT_BE.get(),
                TrafficLightBlockEntityRenderer::new
        );

        //Block Tinting register
        Minecraft.getInstance().getBlockColors().register(
                new TintedTextures.TintedBlock(),
                MTBlocks.ADV_1_TRAFFIC_LIGHT.get(),
                MTBlocks.ADV_2_TRAFFIC_LIGHT.get(),
                MTBlocks.ADV_3_TRAFFIC_LIGHT.get()
        );
        Minecraft.getInstance().getItemColors().register(
                new TintedTextures.TintedItem(),
                MTBlocks.ADV_1_TRAFFIC_LIGHT.get(),
                MTBlocks.ADV_2_TRAFFIC_LIGHT.get(),
                MTBlocks.ADV_3_TRAFFIC_LIGHT.get()
        );

        // Some client setup code
        MoreTraffic.LOGGER.info("MoreTraffic Client registration...");
    }

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        ClientCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void registerTooltips(net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(NoticeBoxTooltip.class, NoticeBoxTooltip.Client::new);
        event.register(BodyTooltip.class, BodyTooltip.Client::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(TRAFFIC_LIGHT_SETUP.get(), AdvancedTrafficLightScreen::new);
        event.register(CONTROL_CABINET_MENU.get(), LightControlCabinetScreen::new);
    }
}
