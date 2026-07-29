package com.tomas65107.moretraffic.mod.registration;

import com.tomas65107.moretraffic.gui.containers.AdvancedTrafficLightMenu;
import com.tomas65107.moretraffic.gui.containers.LEDStripMenu;
import com.tomas65107.moretraffic.gui.containers.LightControlCabinetMenu;
import com.tomas65107.moretraffic.gui.containers.SignboardMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

import net.minecraftforge.registries.RegistryObject;

public class MTMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, "moretraffic");

    public static final RegistryObject<MenuType<AdvancedTrafficLightMenu>> TRAFFIC_LIGHT_SETUP =
            MENUS.register("terminal_block_menu", () -> IForgeMenuType.create(AdvancedTrafficLightMenu::new));

    public static final RegistryObject<MenuType<LightControlCabinetMenu>> CONTROL_CABINET_MENU =
            MENUS.register("control_cabinet_menu", () -> IForgeMenuType.create(LightControlCabinetMenu::new));

    public static final RegistryObject<MenuType<LEDStripMenu>> LED_STRIP_MENU =
            MENUS.register("led_light_menu", () -> IForgeMenuType.create(LEDStripMenu::new));

    public static final RegistryObject<MenuType<SignboardMenu>> SIGNBOARD_MENU =
            MENUS.register("signboard_menu", () -> IForgeMenuType.create(SignboardMenu::new));
}
