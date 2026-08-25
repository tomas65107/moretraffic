package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlockEntity;
import com.tomas65107.moretraffic.gui.containers.AdvancedTrafficLightMenu;
import com.tomas65107.moretraffic.helpers.BlockStateHelper;
import com.tomas65107.moretraffic.mod.registration.MTNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class ServerTrafficLightStateHandler {
    private ServerTrafficLightStateHandler() {
    }

    public static void handle(UpdateTrafficLightStatePacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = context.getSender();

        if (player != null
                && MTNetworking.canEdit(player, message.pos())
                && player.containerMenu instanceof AdvancedTrafficLightMenu menu
                && menu.pos.equals(message.pos())
                && player.level().getBlockEntity(message.pos()) instanceof AdvancedTrafficLightBlockEntity) {
            BlockState state = player.level().getBlockState(message.pos());
            BlockState newState = BlockStateHelper.setValueFromString(state, message.valueName(), message.valueData());
            if (newState != state) {
                player.level().setBlock(message.pos(), newState, 3);
            }
        }

        context.setPacketHandled(true);
    }
}
