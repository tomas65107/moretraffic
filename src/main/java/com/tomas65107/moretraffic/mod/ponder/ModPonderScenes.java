package com.tomas65107.moretraffic.mod.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tomas65107.moretraffic.registration.MTRegistrate;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class ModPonderScenes {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(MTRegistrate.LIGHT_CONTROL_CABINET)
                .addStoryBoard("cabinet_1", ControlCabinetScenes::scene1, AllCreatePonderTags.REDSTONE);

//        HELPER.forComponents(MTRegistrate.DERAILER)
//                .addStoryBoard("derailer_1", DerailerScenes::scene1, AllCreatePonderTags.TRAIN_RELATED);

    }
}