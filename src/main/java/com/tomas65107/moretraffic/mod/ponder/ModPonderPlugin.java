package com.tomas65107.moretraffic.mod.ponder;

import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ModPonderPlugin implements PonderPlugin {

    @Override
    public @NotNull String getModId() {
        return MoreTraffic.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        ModPonderScenes.register(helper);
    }
}
