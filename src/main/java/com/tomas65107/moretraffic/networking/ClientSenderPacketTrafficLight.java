package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientSenderPacketTrafficLight(
        BlockPos pos,
        int lightIndex,
        int colorId,
        byte[] maskRowsByte
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientSenderPacketTrafficLight> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MoreTraffic.MODID, "clientsender_trafficlight"));


    public static final StreamCodec<FriendlyByteBuf, ClientSenderPacketTrafficLight> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    ClientSenderPacketTrafficLight::pos,
                    ByteBufCodecs.VAR_INT,
                    ClientSenderPacketTrafficLight::lightIndex,
                    ByteBufCodecs.VAR_INT,
                    ClientSenderPacketTrafficLight::colorId,
                    ByteBufCodecs.BYTE_ARRAY,
                    ClientSenderPacketTrafficLight::maskRowsByte,
                    ClientSenderPacketTrafficLight::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // Convert short array to byte array
    public static byte[] shortsToBytes(short[] shorts) {
        if (shorts == null) return null;
        byte[] bytes = new byte[shorts.length * 2];
        for (int i = 0; i < shorts.length; i++) {
            bytes[i * 2] = (byte) ((shorts[i] >> 8) & 0xFF);       // high byte
            bytes[i * 2 + 1] = (byte) (shorts[i] & 0xFF);          // low byte
        }
        return bytes;
    }

    // Convert byte array back to short array
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