package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.block.SignboardBlockEntity;
import com.tomas65107.moretraffic.gui.containers.SignboardMenu;
import com.tomas65107.moretraffic.mod.registration.MTNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.Set;
import java.util.function.Supplier;

public final class ServerHandleSignboard {
    private static final int MAX_ELEMENTS = 128;
    private static final Set<String> ELEMENT_TYPES = Set.of("Text", "Background", "Sprite");

    private ServerHandleSignboard() {
    }

    public static void handle(ClientSyncSignboard message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = context.getSender();

        if (player != null
                && isValidTag(message.tag())
                && MTNetworking.canEdit(player, message.pos())
                && player.containerMenu instanceof SignboardMenu menu
                && menu.pos.equals(message.pos())) {
            BlockEntity blockEntity = player.level().getBlockEntity(message.pos());
            if (blockEntity instanceof SignboardBlockEntity signboard) {
                signboard.load(message.tag());
                signboard.setChanged();
                player.level().sendBlockUpdated(message.pos(), signboard.getBlockState(), signboard.getBlockState(), 3);
            }
        }

        context.setPacketHandled(true);
    }

    private static boolean isValidTag(CompoundTag tag) {
        if (tag == null || tag.getAllKeys().size() > 8) return false;
        CompoundTag elements = tag.getCompound("elements");
        if (elements.getAllKeys().size() > MAX_ELEMENTS) return false;

        for (String key : elements.getAllKeys()) {
            try {
                int index = Integer.parseInt(key);
                if (index < 0 || index >= MAX_ELEMENTS) return false;
            } catch (NumberFormatException exception) {
                return false;
            }

            CompoundTag element = elements.getCompound(key);
            String type = element.getString("Type");
            if (!ELEMENT_TYPES.contains(type)) return false;
            if (element.getString("json").length() > 4096) return false;
            if (type.equals("Sprite") && element.getString("rows").length() != 256) return false;
            float size = element.getFloat("size");
            if (!Float.isFinite(size) || Math.abs(size) > 64.0F) return false;
            if (Math.abs(element.getInt("startX")) > 1024 || Math.abs(element.getInt("startY")) > 1024) return false;
        }
        return true;
    }
}
