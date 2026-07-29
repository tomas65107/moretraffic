package com.tomas65107.moretraffic.networking;

import com.tomas65107.moretraffic.block.LightControlCabinetBlockEntity;
import com.tomas65107.moretraffic.gui.containers.LightControlCabinetMenu;
import com.tomas65107.moretraffic.mod.registration.MTNetworking;
import com.tomas65107.moretraffic.mod.registration.MTRegistrate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class ServerCabinetHandle {
    private static final int MAX_INSTRUCTIONS = 64;
    private static final int MAX_GROUPS = 64;
    private static final int MAX_POSITIONS_PER_GROUP = 256;

    private ServerCabinetHandle() {
    }

    public static void handle(ClientSyncCabinetPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = context.getSender();

        if (player != null
                && isValidTag(message.tag())
                && MTNetworking.canEdit(player, message.pos())
                && player.containerMenu instanceof LightControlCabinetMenu menu
                && menu.pos.equals(message.pos())) {
            BlockEntity blockEntity = player.level().getBlockEntity(message.pos());
            if (blockEntity instanceof LightControlCabinetBlockEntity cabinet
                    && player.level().getBlockState(message.pos()).is(MTRegistrate.LIGHT_CONTROL_CABINET.get())) {
                cabinet.load(message.tag());
                cabinet.setChanged();
                player.level().sendBlockUpdated(message.pos(), cabinet.getBlockState(), cabinet.getBlockState(), 3);
            }
        }

        context.setPacketHandled(true);
    }

    private static boolean isValidTag(CompoundTag tag) {
        if (tag == null || tag.getAllKeys().size() > 16) return false;
        ListTag instructions = tag.getList("Instructions", Tag.TAG_COMPOUND);
        ListTag groups = tag.getList("Groups", Tag.TAG_COMPOUND);
        if (instructions.size() > MAX_INSTRUCTIONS || groups.size() > MAX_GROUPS) return false;
        if (tag.getShort("ProgramStep") < 0 || tag.getShort("ProgramStep") > instructions.size()) return false;

        for (int i = 0; i < instructions.size(); i++) {
            CompoundTag instruction = instructions.getCompound(i);
            if (instruction.getString("Type").length() > 64
                    || instruction.getString("Group").length() > 64
                    || instruction.getString("TrafficDisplayPixels").length() > 4096) {
                return false;
            }
        }

        for (int i = 0; i < groups.size(); i++) {
            CompoundTag group = groups.getCompound(i);
            if (group.getString("Name").length() > 64
                    || group.getList("Positions", Tag.TAG_LONG).size() > MAX_POSITIONS_PER_GROUP) {
                return false;
            }
        }
        return true;
    }
}
