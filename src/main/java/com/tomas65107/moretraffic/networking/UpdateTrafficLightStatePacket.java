package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record UpdateTrafficLightStatePacket(
        BlockPos pos,
        String valueName,
        String valueData
) {
    public static final ResourceLocation ID = new ResourceLocation(MoreTraffic.MODID, "clientsender_trafficlight_state");

    public static void encode(UpdateTrafficLightStatePacket message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos());
        buffer.writeUtf(message.valueName(), 64);
        buffer.writeUtf(message.valueData(), 64);
    }

    public static UpdateTrafficLightStatePacket decode(FriendlyByteBuf buffer) {
        return new UpdateTrafficLightStatePacket(
                buffer.readBlockPos(),
                buffer.readUtf(64),
                buffer.readUtf(64)
        );
    }
}
