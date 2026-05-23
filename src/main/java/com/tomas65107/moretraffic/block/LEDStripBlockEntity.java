package com.tomas65107.moretraffic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static com.tomas65107.moretraffic.block.LEDStripBlock.LIGHT_LEVEL;

public class LEDStripBlockEntity extends BlockEntity implements MenuProvider {

    public int startPosX;
    public int startPosY;
    public int sizeX;
    public int sizeY;
    public boolean emitsLight;
    public DyeColor color;

    public LEDStripBlockEntity(BlockEntityType type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        startPosX = (16 - 2) / 2;
        startPosY = (16 - 2) / 2;
        sizeX = 2;
        sizeY = 2;
        color = DyeColor.BLACK;
        emitsLight = false;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        startPosX = tag.getInt("StartPosX");
        startPosY = tag.getInt("StartPosY");
        sizeX = tag.getInt("SizeX");
        sizeY = tag.getInt("SizeY");
        emitsLight = tag.getBoolean("shouldEmitLight");
        color = DyeColor.byName(tag.getString("Color"), DyeColor.BLACK);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("StartPosX", startPosX);
        tag.putInt("StartPosY", startPosY);
        tag.putInt("SizeX", sizeX);
        tag.putInt("SizeY", sizeY);
        tag.putBoolean("shouldEmitLight", emitsLight);
        tag.putString("Color", color.getName());

        if (level.getBlockEntity(getBlockPos()) instanceof LEDStripBlockEntity be) {
            if (be.emitsLight && !be.color.equals(DyeColor.BLACK)) {
                level.setBlock(getBlockPos(), getBlockState().setValue(LIGHT_LEVEL, 8), 3);
            } else {
                level.setBlock(getBlockPos(), getBlockState().setValue(LIGHT_LEVEL, 0), 3);
            }
        }
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

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return null;
    }
}
