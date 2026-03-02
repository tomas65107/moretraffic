package com.tomas65107.moretraffic.gui.containers;

import com.tomas65107.moretraffic.block.LightControlCabinetBlockEntity;
import com.tomas65107.moretraffic.registration.MTMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class LightControlCabinetMenu extends AbstractContainerMenu {
    public LightControlCabinetBlockEntity be;
    public final BlockPos pos;

    public LightControlCabinetMenu(int id, Inventory playerInv, FriendlyByteBuf extraData) {
        this(id, playerInv, BlockPos.of(extraData.readLong()));
    }

    public LightControlCabinetMenu(int id, Inventory inventory, BlockPos pos) {
        super(MTMenus.CONTROL_CABINET_MENU.get(), id);
        this.pos = pos;
        this.be = (LightControlCabinetBlockEntity) inventory.player.level().getBlockEntity(pos);
//        this.access = ContainerLevelAccess.create(inventory.player.level(), pos);
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