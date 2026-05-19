package com.tomas65107.moretraffic.data.blocktypes;

import com.tterrag.registrate.util.nullness.NonNullSupplier;
import de.mrjulsen.trafficcraft.block.entity.ColoredBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static com.tomas65107.moretraffic.mod.registration.MTRegistrate.GIRDED_TRUSS;
import static com.tomas65107.moretraffic.mod.registration.MTRegistrate.TRAFFIC_PILLAR;

public class MTBaseColoredBlockEntity extends ColoredBlockEntity {
    public MTBaseColoredBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static NonNullSupplier<? extends Block>[] getValidHookedBlocks() {
        return new NonNullSupplier[] { GIRDED_TRUSS, TRAFFIC_PILLAR };
    }

}