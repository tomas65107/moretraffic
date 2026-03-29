package com.tomas65107.moretraffic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.tomas65107.moretraffic.data.helpers.HelperFunctions.rotateShapeSpecial;
import static com.tomas65107.moretraffic.rendering.BlockBoundingBoxes.*;

public class TrafficPillarBlock extends Block {

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public TrafficPillarBlock(Properties properties) {
        super(properties.strength(10.0f, 40.0f).mapColor(MapColor.TERRACOTTA_WHITE));
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(BlockState state, LootParams.@NotNull Builder params) {
        return List.of(state.getBlock().asItem().getDefaultInstance());
    }

    @Override
    public float getDestroyProgress(@NotNull BlockState state, Player player, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        var item = player.getMainHandItem().getItem();
        if (item instanceof PickaxeItem pickaxe) {
            return pickaxe.getTier().getSpeed() / 70;
        }
        return super.getDestroyProgress(state, player, world, pos);
    }

    @Override public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {return rotateShapeSpecial(state.getValue(FACING), TRAFFIC_PILLAR);}
    @Override public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {return rotateShapeSpecial(state.getValue(FACING), TRAFFIC_PILLAR);}

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction dir = context.getNearestLookingDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, dir);
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
