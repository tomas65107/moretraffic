package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlockEntity;
import com.tomas65107.moretraffic.data.TrafficLightLight;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.tomas65107.moretraffic.networking.ClientSenderPacketTrafficLight.bytesToShorts;

public class ServerTrafficLightHandler {

    public static void handle(ClientSenderPacketTrafficLight payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            MoreTraffic.LOGGER.debug("Server received traffic light update for pos: {} lightIndex: {}", payload.pos(), payload.lightIndex());

            Level level = context.player().level();
            BlockPos pos = payload.pos();
            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof AdvancedTrafficLightBlockEntity trafficBE) {
                short[] maskRows = bytesToShorts(payload.maskRowsByte());

                trafficBE.modifyLightColor(payload.lightIndex(), DyeColor.byId(payload.colorId()));

                if (payload.maskRowsByte().length > 2 && payload.maskRowsByte() != null) {
                    trafficBE.modifyLightMask(payload.lightIndex(), new TrafficLightLight.TrafficLightMask(maskRows));
                }

                level.sendBlockUpdated(pos, trafficBE.getBlockState(), trafficBE.getBlockState(), 3);

            } else throw new RuntimeException();
        });
    }
}
