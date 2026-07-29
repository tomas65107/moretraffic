package com.tomas65107.moretraffic.mod.registration;

import com.tomas65107.moretraffic.mod.MoreTraffic;
import com.tomas65107.moretraffic.networking.ClientSenderPacketTrafficLight;
import com.tomas65107.moretraffic.networking.ClientSyncCabinetPacket;
import com.tomas65107.moretraffic.networking.ClientSyncLightPacket;
import com.tomas65107.moretraffic.networking.ClientSyncSignboard;
import com.tomas65107.moretraffic.networking.ServerCabinetHandle;
import com.tomas65107.moretraffic.networking.ServerHandleSignboard;
import com.tomas65107.moretraffic.networking.ServerLightHandle;
import com.tomas65107.moretraffic.networking.ServerTrafficLightHandler;
import com.tomas65107.moretraffic.networking.ServerTrafficLightStateHandler;
import com.tomas65107.moretraffic.networking.UpdateTrafficLightStatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class MTNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final ResourceLocation CHANNEL_ID = new ResourceLocation(MoreTraffic.MODID, "main");
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(CHANNEL_ID)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static boolean registered;

    private MTNetworking() {
    }

    public static synchronized void register() {
        if (registered) return;
        registered = true;

        int discriminator = 0;
        CHANNEL.messageBuilder(ClientSenderPacketTrafficLight.class, discriminator++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ClientSenderPacketTrafficLight::encode)
                .decoder(ClientSenderPacketTrafficLight::decode)
                .consumerMainThread(ServerTrafficLightHandler::handle)
                .add();
        CHANNEL.messageBuilder(UpdateTrafficLightStatePacket.class, discriminator++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(UpdateTrafficLightStatePacket::encode)
                .decoder(UpdateTrafficLightStatePacket::decode)
                .consumerMainThread(ServerTrafficLightStateHandler::handle)
                .add();
        CHANNEL.messageBuilder(ClientSyncCabinetPacket.class, discriminator++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ClientSyncCabinetPacket::encode)
                .decoder(ClientSyncCabinetPacket::decode)
                .consumerMainThread(ServerCabinetHandle::handle)
                .add();
        CHANNEL.messageBuilder(ClientSyncLightPacket.class, discriminator++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ClientSyncLightPacket::encode)
                .decoder(ClientSyncLightPacket::decode)
                .consumerMainThread(ServerLightHandle::handle)
                .add();
        CHANNEL.messageBuilder(ClientSyncSignboard.class, discriminator, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ClientSyncSignboard::encode)
                .decoder(ClientSyncSignboard::decode)
                .consumerMainThread(ServerHandleSignboard::handle)
                .add();
    }

    public static void sendToServer(Object message) {
        CHANNEL.sendToServer(message);
    }

    public static boolean canEdit(ServerPlayer player, BlockPos pos) {
        return player != null
                && player.level().isLoaded(pos)
                && player.distanceToSqr(Vec3.atCenterOf(pos)) <= 64.0D;
    }
}
