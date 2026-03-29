package com.tomas65107.moretraffic.mod;

import com.simibubi.create.content.trains.graph.EdgePointType;
import com.tomas65107.moretraffic.block.Derailer;
import net.minecraft.resources.ResourceLocation;

import static com.simibubi.create.content.trains.graph.EdgePointType.register;

public final class MoreTrafficCompat {
    public static EdgePointType<Derailer> DERAILER;

    public static void init() {
        DERAILER = register(ResourceLocation.fromNamespaceAndPath("moretraffic", "derailer"), Derailer::new);
    }
}