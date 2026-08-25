package com.tomas65107.moretraffic.mod.compat;

import com.tomas65107.moretraffic.gui.containers.AdvancedTrafficLightScreen;
import com.tomas65107.moretraffic.gui.containers.LEDStripScreen;
import com.tomas65107.moretraffic.gui.containers.LightControlCabinetScreen;
import com.tomas65107.moretraffic.gui.containers.SignboardScreen;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IScreenHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class MoreTrafficJeiPlugin implements IModPlugin {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MoreTraffic.MODID, "jei_plugin");

    private static <T extends Screen> IScreenHandler<T> hidden() {
        return s -> null;
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiScreenHandler(AdvancedTrafficLightScreen.class, hidden());
        registration.addGuiScreenHandler(LEDStripScreen.class, hidden());
        registration.addGuiScreenHandler(LightControlCabinetScreen.class, hidden());
        registration.addGuiScreenHandler(SignboardScreen.class, hidden());
    }
}
