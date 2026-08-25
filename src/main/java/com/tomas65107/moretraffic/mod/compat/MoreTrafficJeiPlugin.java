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

/// Loaded by JEI only, so the mod keeps working without JEI installed.
@JeiPlugin
public class MoreTrafficJeiPlugin implements IModPlugin {

    private static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(MoreTraffic.MODID, "jei_plugin");

    /// No gui properties means JEI treats the screen as "not a gui": the ingredient list
    /// is neither drawn nor asked for the ingredient under the mouse, so it stops eating
    /// clicks. The screens fill the window with their own widgets and hold no slots,
    /// so an overlay on top of them is only in the way.
    private static <T extends Screen> IScreenHandler<T> hidden() {
        return screen -> null;
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // Registered per concrete screen, not on the shared AbstractTomiContainerScreen
        // parent: JEI looks the handler up by exact class first and only falls back to
        // scanning the handler map for an assignable key. That scan takes the first
        // matching entry in map order, where JEI's own AbstractContainerScreen handler
        // also matches, so a parent-class registration wins only by luck.
        registration.addGuiScreenHandler(AdvancedTrafficLightScreen.class, hidden());
        registration.addGuiScreenHandler(LEDStripScreen.class, hidden());
        registration.addGuiScreenHandler(LightControlCabinetScreen.class, hidden());
        registration.addGuiScreenHandler(SignboardScreen.class, hidden());
    }
}
