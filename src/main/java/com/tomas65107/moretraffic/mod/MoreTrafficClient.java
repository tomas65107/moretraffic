package com.tomas65107.moretraffic.mod;

import com.tomas65107.moretraffic.block.DerailerVisual;
import com.tomas65107.moretraffic.gui.containers.AdvancedTrafficLightScreen;
import com.tomas65107.moretraffic.gui.containers.LEDStripScreen;
import com.tomas65107.moretraffic.gui.containers.LightControlCabinetScreen;
import com.tomas65107.moretraffic.gui.tooltip.BodyTooltip;
import com.tomas65107.moretraffic.gui.tooltip.NoticeBoxTooltip;
import com.tomas65107.moretraffic.mod.ponder.ModPonderPlugin;
import com.tomas65107.moretraffic.registration.MTPartials;
import com.tomas65107.moretraffic.rendering.*;
import de.mrjulsen.trafficcraft.client.TintedTextures;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import net.minecraft.world.level.block.entity.BlockEntityType;
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
import static com.tomas65107.moretraffic.registration.MTRegistrate.*;

@Mod(value = MoreTraffic.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MoreTraffic.MODID, value = Dist.CLIENT)
public class MoreTrafficClient {

    public MoreTrafficClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        MTPartials.init();

        event.enqueueWork(() -> {
            SimpleBlockEntityVisualizer.builder(DERAILER_BE.get())
                    .factory(DerailerVisual::new).skipVanillaRender(be -> false).apply();
        });

        ItemBlockRenderTypes.setRenderLayer(BALLAST_GRAY.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(LEDSTRIP.get(), RenderType.translucent());

        //BER
        BlockEntityRenderers.register(
                DERAILER_BE.get(),
                DerailerRenderer::new
        );

        BlockEntityRenderers.register(
                ADV_TRAFFIC_LIGHT_BE.get(),
                TrafficLightBlockEntityRenderer::new
        );

        BlockEntityRenderers.register(
                BLINKER_BE.get(),
                BlinkerBlockEntityRenderer::new
        );

        BlockEntityRenderers.register(
                TRAFFIC_DISPLAY_BE.get(),
                TrafficDisplayEntityRenderer::new
        );

        BlockEntityRenderers.register(
                LED_STRIP_BE.get(),
                LedStripBlockEntityRenderer::new
        );

        //Block Tinting register
        Minecraft.getInstance().getBlockColors().register(
                new TintedTextures.TintedBlock(),
                ADV_1_TRAFFIC_LIGHT.get(),
                ADV_2_TRAFFIC_LIGHT.get(),
                ADV_3_TRAFFIC_LIGHT.get(),
                BLINKER.get(),
                GIRDED_TRUSS.get()
        );
        Minecraft.getInstance().getItemColors().register(
                new TintedTextures.TintedItem(),
                ADV_1_TRAFFIC_LIGHT.get(),
                ADV_2_TRAFFIC_LIGHT.get(),
                ADV_3_TRAFFIC_LIGHT.get(),
                BLINKER.get(),
                GIRDED_TRUSS.get()
        );

        ItemBlockRenderTypes.setRenderLayer(
                BLINKER.get(),
                RenderType.translucent()
        );

        PonderIndex.addPlugin(new ModPonderPlugin());

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
