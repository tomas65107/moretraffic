package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.block.LEDStripBlockEntity;
import com.tomas65107.moretraffic.registration.MTRegistrate;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerLightHandle {

    public static void handle(ClientSyncLightPacket payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Level level = ctx.player().level();
            BlockEntity be = level.getBlockEntity(payload.pos());

            if (be instanceof LEDStripBlockEntity cabinet && level.getBlockState(payload.pos()).is(MTRegistrate.LEDSTRIP.get())) {
                cabinet.loadAdditional(payload.tag(), level.registryAccess());
                cabinet.setChanged();

                level.sendBlockUpdated(
                        payload.pos(),
                        cabinet.getBlockState(),
                        cabinet.getBlockState(),
                        3
                );
            }
        });
    }

}
