package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientSyncSignboard(BlockPos pos, CompoundTag tag) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientSyncSignboard> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MoreTraffic.MODID, "clientsender_signboard"));

    public static final StreamCodec<FriendlyByteBuf, ClientSyncSignboard> STREAM_CODEC =
            CustomPacketPayload.codec(ClientSyncSignboard::encode, ClientSyncSignboard::decode);

    public static void encode(ClientSyncSignboard msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos());
        buf.writeNbt(msg.tag());
    }

    public static ClientSyncSignboard decode(FriendlyByteBuf buf) {
        return new ClientSyncSignboard(buf.readBlockPos(), buf.readNbt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}