package com.tomas65107.moretraffic.gui.containers;

import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlockEntity;
import com.tomas65107.moretraffic.registration.MTMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

public class AdvancedTrafficLightMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    public AdvancedTrafficLightBlockEntity be;
    public final BlockPos pos;

    public AdvancedTrafficLightMenu(int id, Inventory playerInv, FriendlyByteBuf extraData) {
        this(id, playerInv, BlockPos.of(extraData.readLong()));
    }

    public AdvancedTrafficLightMenu(int id, Inventory inventory, BlockPos pos) {
        super(MTMenus.TRAFFIC_LIGHT_SETUP.get(), id);
        this.pos = pos;
        this.be = (AdvancedTrafficLightBlockEntity) inventory.player.level().getBlockEntity(pos);
        this.access = ContainerLevelAccess.create(inventory.player.level(), pos);
    }


    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}