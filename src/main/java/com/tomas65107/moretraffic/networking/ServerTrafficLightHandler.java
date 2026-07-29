package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlockEntity;
import com.tomas65107.moretraffic.data.TrafficLightLight;
import com.tomas65107.moretraffic.gui.containers.AdvancedTrafficLightMenu;
import com.tomas65107.moretraffic.mod.registration.MTNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static com.tomas65107.moretraffic.networking.ClientSenderPacketTrafficLight.bytesToShorts;

public final class ServerTrafficLightHandler {
    private ServerTrafficLightHandler() {
    }

    public static void handle(ClientSenderPacketTrafficLight message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = context.getSender();

        if (player != null
                && MTNetworking.canEdit(player, message.pos())
                && player.containerMenu instanceof AdvancedTrafficLightMenu menu
                && menu.pos.equals(message.pos())) {
            BlockEntity blockEntity = player.level().getBlockEntity(message.pos());
            if (blockEntity instanceof AdvancedTrafficLightBlockEntity trafficLight
                    && message.lightIndex() >= 0
                    && message.lightIndex() < trafficLight.lights.size()
                    && message.colorId() >= 0
                    && message.colorId() < DyeColor.values().length
                    && message.maskRowsByte() != null
                    && message.maskRowsByte().length == 32) {
                short[] maskRows = bytesToShorts(message.maskRowsByte());
                trafficLight.modifyLightColor(message.lightIndex(), DyeColor.byId(message.colorId()));
                trafficLight.modifyLightMask(message.lightIndex(), new TrafficLightLight.TrafficLightMask(maskRows));
                player.level().sendBlockUpdated(message.pos(), trafficLight.getBlockState(), trafficLight.getBlockState(), 3);
            }
        }

        context.setPacketHandled(true);
    }
}
