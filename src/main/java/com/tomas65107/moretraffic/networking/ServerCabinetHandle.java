package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.block.LightControlCabinetBlockEntity;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import com.tomas65107.moretraffic.mod.registration.MTRegistrate;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerCabinetHandle {

    public static void handle(ClientSyncCabinetPacket payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Level level = ctx.player().level();
            BlockEntity be = level.getBlockEntity(payload.pos());

            if (be instanceof LightControlCabinetBlockEntity cabinet && level.getBlockState(payload.pos()).is(MTRegistrate.LIGHT_CONTROL_CABINET.get())) {
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
