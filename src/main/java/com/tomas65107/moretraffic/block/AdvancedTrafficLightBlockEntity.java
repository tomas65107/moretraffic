package com.tomas65107.moretraffic.block;

import com.tomas65107.moretraffic.data.TrafficLightLight;
import com.tomas65107.moretraffic.gui.containers.AdvancedTrafficLightMenu;
import com.tomas65107.moretraffic.registration.MTRegistrate;
import de.mrjulsen.trafficcraft.block.entity.ColoredBlockEntity;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AdvancedTrafficLightBlockEntity extends ColoredBlockEntity implements MenuProvider {

    public List<TrafficLightLight> lights = new ArrayList<>();

    public AdvancedTrafficLightBlockEntity(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        for (int i = 0; i < getTheLightNumberVariant(); i++) {
            lights.add(new TrafficLightLight(DyeColor.BLACK, new TrafficLightLight.TrafficLightMask()));
        }
    }

    private int getTheLightNumberVariant() {
        Block block = getBlockState().getBlock();

        if (block == MTRegistrate.ADV_1_TRAFFIC_LIGHT.get()) {
            return 1;
        } else if (block == MTRegistrate.ADV_2_TRAFFIC_LIGHT.get()) {
            return 2;
        } else if (block == MTRegistrate.ADV_3_TRAFFIC_LIGHT.get()) {
            return 3;
        } else throw new IllegalArgumentException("Invalid value passed into method");
    }

    public void modifyLightColor(int lightIndex, DyeColor newColor) {
        if (lightIndex >= this.lights.size()) return;

        lights.get(lightIndex).color = newColor;
        setChanged();
        assert level != null;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
    public void modifyLightMask(int lightIndex, TrafficLightLight.TrafficLightMask newMask) {
        if (lightIndex >= this.lights.size()) throw new IllegalArgumentException();

        lights.get(lightIndex).mask = newMask;
        setChanged();
        assert level != null;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);

        for (int i = 0; i < this.lights.size(); i++) {
            if (tag.contains("LightColor_" + i)) {
                lights.get(i).color = DyeColor.byName(tag.getString("LightColor_" + i), lights.get(i).color);
            }
            if (tag.contains("LightMask_" + i)) {
                lights.get(i).mask = TrafficLightLight.TrafficLightMask.deserialize(tag.getString("LightMask_" + i));
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);

        for (int i = 0; i < lights.size(); i++) {
            tag.putString("LightColor_" + i, lights.get(i).color.getName());
        }
        for (int i = 0; i < lights.size(); i++) {
            tag.putString("LightMask_"+i, lights.get(i).mask.serialize());
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
        return Component.literal("null");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new AdvancedTrafficLightMenu(id, inventory, getBlockPos());
    }
}