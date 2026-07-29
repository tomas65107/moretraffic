package com.tomas65107.moretraffic.block;

import com.tomas65107.moretraffic.data.BoardElement;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SignboardBlockEntity extends BlockEntity implements MenuProvider {

    public List<BoardElement> elements;
    public boolean firstPlace;

    public SignboardBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        elements = new ArrayList<>();
        elements.add(new BoardElement.Background(0, 0, DyeColor.WHITE));
        firstPlace = true;
    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return null;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        firstPlace = tag.getBoolean("firstPlace");
        elements = new ArrayList<>();

        CompoundTag elementsTag = tag.getCompound("elements");

        List<String> keys = new ArrayList<>(elementsTag.getAllKeys());
        keys.sort(Comparator.comparingInt(Integer::parseInt));

        for (String key : keys) {
            elements.add(BoardElement.deserialize(elementsTag.getCompound(key)));
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        CompoundTag elementsTag = new CompoundTag();

        int index = 0;
        for (BoardElement element : elements) {
            elementsTag.put(String.valueOf(index), element.serialize());
            index++;
        }

        tag.put("elements", elementsTag);
        tag.putBoolean("firstPlace", firstPlace);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
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
