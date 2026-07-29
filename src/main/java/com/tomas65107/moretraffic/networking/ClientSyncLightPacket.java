package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientSyncLightPacket(BlockPos pos, CompoundTag tag) {
    public static final ResourceLocation ID = new ResourceLocation(MoreTraffic.MODID, "clientsender_light");

    public static void encode(ClientSyncLightPacket message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos());
        buffer.writeNbt(message.tag());
    }

    public static ClientSyncLightPacket decode(FriendlyByteBuf buffer) {
        return new ClientSyncLightPacket(buffer.readBlockPos(), buffer.readNbt());
    }
}
