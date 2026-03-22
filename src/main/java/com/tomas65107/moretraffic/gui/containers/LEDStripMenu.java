package com.tomas65107.moretraffic.gui.containers;

import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlockEntity;
import com.tomas65107.moretraffic.block.LEDStripBlockEntity;
import com.tomas65107.moretraffic.registration.MTMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

public class LEDStripMenu extends AbstractContainerMenu {

    private final ContainerLevelAccess access;
    public LEDStripBlockEntity be;
    public final BlockPos pos;

    public LEDStripMenu(int id, Inventory playerInv, FriendlyByteBuf extraData) {
        this(id, playerInv, BlockPos.of(extraData.readLong()));
    }

    public LEDStripMenu(int id, Inventory inventory, BlockPos pos) {
        super(MTMenus.LED_STRIP_MENU.get(), id);
        this.pos = pos;
        this.be = (LEDStripBlockEntity) inventory.player.level().getBlockEntity(pos);
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
