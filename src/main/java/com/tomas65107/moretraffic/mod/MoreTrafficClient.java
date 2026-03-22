package com.tomas65107.moretraffic.mod;

import com.tomas65107.moretraffic.gui.containers.AdvancedTrafficLightScreen;
import com.tomas65107.moretraffic.gui.containers.LEDStripScreen;
import com.tomas65107.moretraffic.gui.containers.LightControlCabinetScreen;
import com.tomas65107.moretraffic.gui.tooltip.BodyTooltip;
import com.tomas65107.moretraffic.gui.tooltip.NoticeBoxTooltip;
import com.tomas65107.moretraffic.registration.MTBE;
import com.tomas65107.moretraffic.registration.MTBlocks;
import com.tomas65107.moretraffic.rendering.BlinkerBlockEntityRenderer;
import com.tomas65107.moretraffic.rendering.LedStripBlockEntityRenderer;
import com.tomas65107.moretraffic.rendering.TrafficDisplayEntityRenderer;
import com.tomas65107.moretraffic.rendering.TrafficLightBlockEntityRenderer;
import de.mrjulsen.trafficcraft.client.TintedTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
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

import static com.tomas65107.moretraffic.registration.MTMenus.*;

@Mod(value = MoreTraffic.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MoreTraffic.MODID, value = Dist.CLIENT)
public class MoreTrafficClient {

    public MoreTrafficClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

        ItemBlockRenderTypes.setRenderLayer(MTBlocks.BALLAST_GRAY.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(MTBlocks.LEDSTRIP.get(), RenderType.translucent());

        //BE register
        BlockEntityRenderers.register(
                MTBE.ADVANCED_TRAFFIC_LIGHT_BE.get(),
                TrafficLightBlockEntityRenderer::new
        );

        BlockEntityRenderers.register(
                MTBE.BLINKER_BE.get(),
                BlinkerBlockEntityRenderer::new
        );

        BlockEntityRenderers.register(
                MTBE.TRAFFIC_DISPLAY_BE.get(),
                TrafficDisplayEntityRenderer::new
        );

        BlockEntityRenderers.register(
                MTBE.LEDSTRIP.get(),
                LedStripBlockEntityRenderer::new
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

        Minecraft.getInstance().getBlockColors().register(
                new TintedTextures.TintedBlock(),
                MTBlocks.BLINKER.get()
        );
        Minecraft.getInstance().getItemColors().register(
                new TintedTextures.TintedItem(),
                MTBlocks.BLINKER.get()
        );

        ItemBlockRenderTypes.setRenderLayer(
                MTBlocks.BLINKER.get(),
                RenderType.translucent()
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
        event.register(LED_STRIP_MENU.get(), LEDStripScreen::new);
    }
}
