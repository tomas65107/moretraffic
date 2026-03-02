package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateTrafficLightStatePacket(
        BlockPos pos,
        String valueName,
        String valueData
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateTrafficLightStatePacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MoreTraffic.MODID, "clientsender_trafficlight_state"));

    public static final StreamCodec<FriendlyByteBuf, UpdateTrafficLightStatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    UpdateTrafficLightStatePacket::pos,
                    ByteBufCodecs.STRING_UTF8,
                    UpdateTrafficLightStatePacket::valueName,
                    ByteBufCodecs.STRING_UTF8,
                    UpdateTrafficLightStatePacket::valueData,
                    UpdateTrafficLightStatePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}