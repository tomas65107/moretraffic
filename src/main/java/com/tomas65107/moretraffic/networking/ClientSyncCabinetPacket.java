package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientSyncCabinetPacket(BlockPos pos, CompoundTag tag) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientSyncCabinetPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MoreTraffic.MODID, "clientsender_cabinet"));

    public static final StreamCodec<FriendlyByteBuf, ClientSyncCabinetPacket> STREAM_CODEC =
            CustomPacketPayload.codec(ClientSyncCabinetPacket::encode, ClientSyncCabinetPacket::decode);

    public static void encode(ClientSyncCabinetPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos());
        buf.writeNbt(msg.tag());
    }

    public static ClientSyncCabinetPacket decode(FriendlyByteBuf buf) {
        return new ClientSyncCabinetPacket(buf.readBlockPos(), buf.readNbt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}