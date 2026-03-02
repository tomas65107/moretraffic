package com.tomas65107.moretraffic.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.tomas65107.moretraffic.mod.MoreTraffic.MODID;
import static com.tomas65107.moretraffic.registration.MTItems.*;

public class CreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TRAFFICCRAFT_TAB = CREATIVE_MODE_TABS.register("trafficcraft_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.moretraffic"))
            .icon(() -> ADV_3_TRAFFIC_LIGHT_ITEM.asItem().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ADV_1_TRAFFIC_LIGHT_ITEM);
                output.accept(ADV_2_TRAFFIC_LIGHT_ITEM);
                output.accept(ADV_3_TRAFFIC_LIGHT_ITEM);
                output.accept(LIGHT_CONTROL_CABINET);
                output.accept(LIGHT_DIODE);
    }).build());

}
