package com.tomas65107.moretraffic.mod;

import com.tomas65107.moretraffic.block.DerailerVisual;
import com.tomas65107.moretraffic.gui.containers.SignboardScreen;
import com.tomas65107.moretraffic.helpers.ClientScheduler;
import com.tomas65107.moretraffic.gui.containers.AdvancedTrafficLightScreen;
import com.tomas65107.moretraffic.gui.containers.LEDStripScreen;
import com.tomas65107.moretraffic.gui.containers.LightControlCabinetScreen;
import com.tomas65107.moretraffic.gui.tooltip.BodyTooltip;
import com.tomas65107.moretraffic.gui.tooltip.NoticeBoxTooltip;
import com.tomas65107.moretraffic.mod.ponder.ModPonderPlugin;
import com.tomas65107.moretraffic.mod.registration.MTPartials;
import com.tomas65107.moretraffic.rendering.*;
import net.minecraftforge.event.TickEvent;
import de.mrjulsen.trafficcraft.client.TintedTextures;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraft.client.gui.screens.MenuScreens;

import static com.tomas65107.moretraffic.mod.registration.MTMenus.*;
import static com.tomas65107.moretraffic.mod.registration.MTRegistrate.*;


@Mod.EventBusSubscriber(modid = MoreTraffic.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MoreTrafficClient {


    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        MTPartials.init();

        event.enqueueWork(() -> {
            SimpleBlockEntityVisualizer.builder(DERAILER_BE.get())
                    .factory(DerailerVisual::new).skipVanillaRender(be -> false).apply();
            registerScreens();
        });

        ItemBlockRenderTypes.setRenderLayer(LEDSTRIP.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(SIGNSBOARD.get(), RenderType.translucent());

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

        BlockEntityRenderers.register(
                SIGNSBOARD_BE.get(),
                SignboardStripBlockEntityRenderer::new
        );

        //Block Tinting register
        Minecraft.getInstance().getBlockColors().register(
                new TintedTextures.TintedBlock(),
                ADV_1_TRAFFIC_LIGHT.get(),
                ADV_2_TRAFFIC_LIGHT.get(),
                ADV_3_TRAFFIC_LIGHT.get(),
                BLINKER.get(),
                GIRDED_TRUSS.get(),
                TRAFFIC_PILLAR.get()
        );
        Minecraft.getInstance().getItemColors().register(
                new TintedTextures.TintedItem(),
                ADV_1_TRAFFIC_LIGHT.get(),
                ADV_2_TRAFFIC_LIGHT.get(),
                ADV_3_TRAFFIC_LIGHT.get(),
                BLINKER.get(),
                GIRDED_TRUSS.get(),
                TRAFFIC_PILLAR.get()
        );

        ItemBlockRenderTypes.setRenderLayer(
                BLINKER.get(),
                RenderType.translucent()
        );

        PonderIndex.addPlugin(new ModPonderPlugin());

        MoreTraffic.LOGGER.info("MoreTraffic Client registration complete");
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientScheduler.tick();
        }
    }

    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        ClientCommands.register(event.getDispatcher());
    }

    @Mod.EventBusSubscriber(modid = MoreTraffic.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            MoreTrafficClient.onClientTick(event);
        }

        @SubscribeEvent
        public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
            MoreTrafficClient.onRegisterClientCommands(event);
        }
    }

    @SubscribeEvent
    public static void registerTooltips(net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(NoticeBoxTooltip.class, NoticeBoxTooltip.Client::new);
        event.register(BodyTooltip.class, BodyTooltip.Client::new);
    }

    public static void registerScreens() {
        MenuScreens.register(TRAFFIC_LIGHT_SETUP.get(), AdvancedTrafficLightScreen::new);
        MenuScreens.register(CONTROL_CABINET_MENU.get(), LightControlCabinetScreen::new);
        MenuScreens.register(LED_STRIP_MENU.get(), LEDStripScreen::new);
        MenuScreens.register(SIGNBOARD_MENU.get(), SignboardScreen::new);
    }
}
