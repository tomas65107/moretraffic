package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.data.helpers.BlockStateHelper;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerTrafficLightStateHandler {

    public static void handle(
            UpdateTrafficLightStatePacket pkt,
            IPayloadContext ctx
    ) {
        ctx.enqueueWork(() -> {
            MoreTraffic.LOGGER.debug("Server received traffic light update for pos: {} state: {}", pkt.pos(), pkt.valueName());
           Player player = ctx.player();

            Level level = player.level();
            BlockPos pos = pkt.pos();

            if (!level.isLoaded(pos)) return;

            BlockState state = level.getBlockState(pos);

            BlockState newState = BlockStateHelper.setValueFromString(
                    state,
                    pkt.valueName(),
                    pkt.valueData()
            );

            if (newState != state) {
                level.setBlock(pos, newState, 3);
            }
        });
    }

}
