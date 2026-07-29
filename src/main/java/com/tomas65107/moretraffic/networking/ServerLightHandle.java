package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.block.LEDStripBlockEntity;
import com.tomas65107.moretraffic.gui.containers.LEDStripMenu;
import com.tomas65107.moretraffic.mod.registration.MTNetworking;
import com.tomas65107.moretraffic.mod.registration.MTRegistrate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class ServerLightHandle {
    private ServerLightHandle() {
    }

    public static void handle(ClientSyncLightPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = context.getSender();

        if (player != null
                && isValidTag(message.tag())
                && MTNetworking.canEdit(player, message.pos())
                && player.containerMenu instanceof LEDStripMenu menu
                && menu.pos.equals(message.pos())) {
            BlockEntity blockEntity = player.level().getBlockEntity(message.pos());
            if (blockEntity instanceof LEDStripBlockEntity led
                    && player.level().getBlockState(message.pos()).is(MTRegistrate.LEDSTRIP.get())) {
                led.load(message.tag());
                led.updateLightLevel();
                led.setChanged();
                player.level().sendBlockUpdated(message.pos(), led.getBlockState(), led.getBlockState(), 3);
            }
        }

        context.setPacketHandled(true);
    }

    private static boolean isValidTag(CompoundTag tag) {
        if (tag == null || tag.getAllKeys().size() > 8) return false;
        int startX = tag.getInt("StartPosX");
        int startY = tag.getInt("StartPosY");
        int sizeX = tag.getInt("SizeX");
        int sizeY = tag.getInt("SizeY");
        String colorName = tag.getString("Color");
        return startX > -16 && startX < 16
                && startY > -16 && startY < 16
                && sizeX >= 2 && sizeX <= 16
                && sizeY >= 2 && sizeY <= 16
                && colorName.length() <= 32
                && DyeColor.byName(colorName, null) != null;
    }
}
