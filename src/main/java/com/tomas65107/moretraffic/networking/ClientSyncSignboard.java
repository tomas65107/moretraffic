package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientSyncSignboard(BlockPos pos, CompoundTag tag) {
    public static final ResourceLocation ID = new ResourceLocation(MoreTraffic.MODID, "clientsender_signboard");

    public static void encode(ClientSyncSignboard message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos());
        buffer.writeNbt(message.tag());
    }

    public static ClientSyncSignboard decode(FriendlyByteBuf buffer) {
        return new ClientSyncSignboard(buffer.readBlockPos(), buffer.readNbt());
    }
}
