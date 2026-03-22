package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientSyncLightPacket(BlockPos pos, CompoundTag tag) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientSyncLightPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MoreTraffic.MODID, "clientsender_light"));

    public static final StreamCodec<FriendlyByteBuf, ClientSyncLightPacket> STREAM_CODEC =
            CustomPacketPayload.codec(ClientSyncLightPacket::encode, ClientSyncLightPacket::decode);

    public static void encode(ClientSyncLightPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos());
        buf.writeNbt(msg.tag());
    }

    public static ClientSyncLightPacket decode(FriendlyByteBuf buf) {
        return new ClientSyncLightPacket(buf.readBlockPos(), buf.readNbt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}