package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.block.SignboardBlockEntity;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerHandleSignboard {

    public static void handle(ClientSyncSignboard payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Level level = ctx.player().level();
            BlockEntity be = level.getBlockEntity(payload.pos());

            if (be instanceof SignboardBlockEntity led) {
                led.loadAdditional(payload.tag(), level.registryAccess());
                led.setChanged();

                level.sendBlockUpdated(
                        payload.pos(),
                        led.getBlockState(),
                        led.getBlockState(),
                        3
                );
            }
        });
    }

}
