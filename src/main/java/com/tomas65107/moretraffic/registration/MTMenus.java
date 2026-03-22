package com.tomas65107.moretraffic.registration;

import com.tomas65107.moretraffic.gui.containers.AdvancedTrafficLightMenu;
import com.tomas65107.moretraffic.gui.containers.LEDStripMenu;
import com.tomas65107.moretraffic.gui.containers.LightControlCabinetMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

public class MTMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, "moretraffic");

    public static final Supplier<MenuType<AdvancedTrafficLightMenu>> TRAFFIC_LIGHT_SETUP =
            MENUS.register("terminal_block_menu", () -> IMenuTypeExtension.create(AdvancedTrafficLightMenu::new));

    public static final Supplier<MenuType<LightControlCabinetMenu>> CONTROL_CABINET_MENU =
            MENUS.register("control_cabinet_menu", () -> IMenuTypeExtension.create(LightControlCabinetMenu::new));

    public static final Supplier<MenuType<LEDStripMenu>> LED_STRIP_MENU =
            MENUS.register("led_light_menu", () -> IMenuTypeExtension.create(LEDStripMenu::new));
}