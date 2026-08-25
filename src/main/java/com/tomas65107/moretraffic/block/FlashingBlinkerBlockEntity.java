package com.tomas65107.moretraffic.block;

import com.tomas65107.moretraffic.data.ICabinetPulsable;
import de.mrjulsen.trafficcraft.block.entity.ColoredBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FlashingBlinkerBlockEntity extends ColoredBlockEntity implements ICabinetPulsable {

    public boolean lightStatus;

    public FlashingBlinkerBlockEntity(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handlePulseLight(Boolean newStatus) {
        lightStatus = newStatus;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        lightStatus = tag.getBoolean("light_status");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putBoolean("light_status", lightStatus);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(this);
        return packet;
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet) {
        super.onDataPacket(connection, packet);
        this.load(packet.getTag());
    }
}
