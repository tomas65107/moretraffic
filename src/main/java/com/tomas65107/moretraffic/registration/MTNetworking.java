package com.tomas65107.moretraffic.registration;

import com.tomas65107.moretraffic.mod.MoreTraffic;
import com.tomas65107.moretraffic.networking.*;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MoreTraffic.MODID)
public class MTNetworking {

    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MoreTraffic.MODID);

        registrar.playToServer(
                ClientSenderPacketTrafficLight.TYPE,
                ClientSenderPacketTrafficLight.STREAM_CODEC,
                ServerTrafficLightHandler::handle
        );
        registrar.playToServer(
                UpdateTrafficLightStatePacket.TYPE,
                UpdateTrafficLightStatePacket.STREAM_CODEC,
                ServerTrafficLightStateHandler::handle
        );


        registrar.playToServer(
                ClientSyncCabinetPacket.TYPE,
                ClientSyncCabinetPacket.STREAM_CODEC,
                ServerCabinetHandle::handle
        );
    }
}