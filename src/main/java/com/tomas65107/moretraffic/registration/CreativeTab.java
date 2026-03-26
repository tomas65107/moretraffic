package com.tomas65107.moretraffic.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.tomas65107.moretraffic.mod.MoreTraffic.MODID;
import static com.tomas65107.moretraffic.registration.MTItems.*;

public class CreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TRAFFICCRAFT_TAB = CREATIVE_MODE_TABS.register("trafficcraft_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.moretraffic"))
            .icon(() -> LIGHT_CONTROL_CABINET.asItem().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(LIGHT_DIODE);
                output.accept(BLINKER);
                output.accept(LIGHT_CONTROL_CABINET);
                output.accept(TRAFFIC_DISPLAY_ITEM);
                output.accept(LEDSTRIP);

                output.accept(BALLAST_GRAY);
                output.accept(BALLAST_BROWN);
                output.accept(TRAFFIC_PILLAR);
                output.accept(TRAFFIC_TRUSS);
                output.accept(TRAFFIC_TRUSS_WALKWAY);
    }).build());

}
