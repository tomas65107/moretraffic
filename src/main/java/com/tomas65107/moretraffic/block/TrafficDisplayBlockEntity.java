package com.tomas65107.moretraffic.block;

import com.tomas65107.moretraffic.data.TrafficDisplayPixels;
import com.tomas65107.moretraffic.data.TrafficLightLight;
import com.tomas65107.moretraffic.registration.MTBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static com.tomas65107.moretraffic.data.TrafficDisplayPixels.deserialize;

public class TrafficDisplayBlockEntity extends BlockEntity {

    public TrafficDisplayPixels pixelMask = new TrafficDisplayPixels();

    public TrafficDisplayBlockEntity(BlockPos pos, BlockState blockState) {
        super(MTBE.TRAFFIC_DISPLAY_BE.get(), pos, blockState);
    }

    public void modifyDisplayPixels(TrafficDisplayPixels newMask) {
        pixelMask = newMask;
        setChanged();
        assert level != null;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("DisplayPixels")) pixelMask = deserialize(tag.getString("DisplayPixels"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("DisplayPixels", pixelMask.serialize());
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, lookup);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(this);
        return packet;
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookup) {
        super.onDataPacket(connection, packet, lookup);
        this.loadAdditional(packet.getTag(), lookup);
    }



}
