package com.tomas65107.moretraffic.block;

import com.tomas65107.moretraffic.registration.MTRegistrate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.tomas65107.moretraffic.data.helpers.HelperFunctions.rotateShape;
import static com.tomas65107.moretraffic.rendering.BlockBoundingBoxes.*;

public class TrafficTrussBlock extends Block {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public TrafficTrussBlock(Properties properties) {
        super(properties.mapColor(MapColor.TERRACOTTA_WHITE));
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(BlockState state, LootParams.@NotNull Builder params) {
        return List.of(state.getBlock().asItem().getDefaultInstance());
    }

    @Override
    public float getDestroyProgress(@NotNull BlockState state, Player player, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        var item = player.getMainHandItem().getItem();
        if (item instanceof ShovelItem pickaxe) {
            return pickaxe.getTier().getSpeed() / 70;
        }
        return super.getDestroyProgress(state, player, world, pos);
    }

    public static VoxelShape getVoxelShape(BlockState block) {
        if (block.is(MTRegistrate.TRAFFIC_TRUSS_WALKWAY)) {
            return Shapes.or(TRUSS_BASE, Shapes.or(RAIL_LEFT, RAIL_RIGHT));
        } else {
            return TRUSS_BASE;
        }
    }

    @Override public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {return rotateShape(state.getValue(FACING), getVoxelShape(state));}
    @Override public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {return rotateShape(state.getValue(FACING), getVoxelShape(state));}

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    public @NotNull BlockState rotate(BlockState pState, Rotation pRotation) {
        return (BlockState)pState.setValue(FACING, pRotation.rotate((Direction)pState.getValue(FACING)));
    }

}
