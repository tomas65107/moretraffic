package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientSenderPacketTrafficLight(
        BlockPos pos,
        int lightIndex,
        int colorId,
        byte[] maskRowsByte
) {
    public static final ResourceLocation ID = new ResourceLocation(MoreTraffic.MODID, "clientsender_trafficlight");

    public static void encode(ClientSenderPacketTrafficLight message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos());
        buffer.writeVarInt(message.lightIndex());
        buffer.writeVarInt(message.colorId());
        buffer.writeByteArray(message.maskRowsByte());
    }

    public static ClientSenderPacketTrafficLight decode(FriendlyByteBuf buffer) {
        return new ClientSenderPacketTrafficLight(
                buffer.readBlockPos(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readByteArray(32)
        );
    }

    public static byte[] shortsToBytes(short[] shorts) {
        if (shorts == null) return null;
        byte[] bytes = new byte[shorts.length * 2];
        for (int i = 0; i < shorts.length; i++) {
            bytes[i * 2] = (byte) ((shorts[i] >> 8) & 0xFF);
            bytes[i * 2 + 1] = (byte) (shorts[i] & 0xFF);
        }
        return bytes;
    }

    public static short[] bytesToShorts(byte[] bytes) {
        if (bytes == null) return null;
        if (bytes.length % 2 != 0) throw new IllegalArgumentException("Byte array length must be even");
        short[] shorts = new short[bytes.length / 2];
        for (int i = 0; i < shorts.length; i++) {
            shorts[i] = (short) (((bytes[i * 2] & 0xFF) << 8) | (bytes[i * 2 + 1] & 0xFF));
        }
        return shorts;
    }
}
