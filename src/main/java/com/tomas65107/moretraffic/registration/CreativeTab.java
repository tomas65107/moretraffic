package com.tomas65107.moretraffic.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.tomas65107.moretraffic.mod.MoreTraffic.MODID;
import static com.tomas65107.moretraffic.registration.MTRegistrate.*;

public class CreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TRAFFICCRAFT_TAB =
            CREATIVE_MODE_TABS.register("trafficcraft_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.moretraffic"))
                    .icon(() -> LIGHT_CONTROL_CABINET.get().asItem().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(LIGHT_DIODE.get().asItem().getDefaultInstance());
                        output.accept(BLINKER.get().asItem().getDefaultInstance());
                        output.accept(LIGHT_CONTROL_CABINET.get().asItem().getDefaultInstance());
                        output.accept(TRAFFIC_DISPLAY.get().asItem().getDefaultInstance());
                        output.accept(LEDSTRIP.get().asItem().getDefaultInstance());
                        output.accept(DERAILER.get().asItem().getDefaultInstance());

                        output.accept(BALLAST_GRAY.get().asItem().getDefaultInstance());
                        output.accept(BALLAST_BROWN.get().asItem().getDefaultInstance());
                        output.accept(TRAFFIC_PILLAR.get().asItem().getDefaultInstance());
                        output.accept(GIRDED_TRUSS.get().asItem().getDefaultInstance());
                        output.accept(TRAFFIC_TRUSS.get().asItem().getDefaultInstance());
                    }).build());

}
