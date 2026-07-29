package com.tomas65107.moretraffic.data.blocktypes;


import com.tomas65107.moretraffic.data.ISimpleBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.tomas65107.moretraffic.helpers.HelperFunctions.rotateShape;
import static com.tomas65107.moretraffic.helpers.HelperFunctions.rotateShapeSpecial;

public class MTNormalBlock extends Block implements ISimpleBlockProperties {

    public final DirectionProperty INTERNAL_FACING; //DirectionalBlock or HorizontalDirectionalBlock
    public final VoxelShape INTERNAL_SHAPE;
    public final Class<? extends DiggerItem> INTERNAL_DIGGER_ITEM;

    public MTNormalBlock(@NotNull Properties properties, DirectionProperty FACING, VoxelShape SHAPE, @NotNull Class<? extends DiggerItem> item) {
        super(properties);
        INTERNAL_FACING = FACING;
        INTERNAL_SHAPE = SHAPE;
        INTERNAL_DIGGER_ITEM = item;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder params) {
        return List.of(state.getBlock().asItem().getDefaultInstance());
    }

    @Override
    public float getDestroyProgress(@NotNull BlockState state, @NotNull Player player, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        var item = player.getMainHandItem().getItem();
        if (INTERNAL_DIGGER_ITEM.isInstance(item)) {
            DiggerItem digger = (DiggerItem) item;
            return digger.getTier().getSpeed() / 70;
        }
        return super.getDestroyProgress(state, player, world, pos);
    }

    protected VoxelShape figureOutShapeRotation(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (INTERNAL_FACING == null) return INTERNAL_SHAPE;
        if (INTERNAL_FACING.equals(DirectionalBlock.FACING)) return rotateShapeSpecial(state.getValue(INTERNAL_FACING), INTERNAL_SHAPE);
        if (INTERNAL_FACING.equals(HorizontalDirectionalBlock.FACING)) return rotateShape(state.getValue(INTERNAL_FACING), INTERNAL_SHAPE);
        return INTERNAL_SHAPE;
    }

    @Override public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {return figureOutShapeRotation(state, level, pos, context);}
    @Override public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {return figureOutShapeRotation(state, level, pos, context);}

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState pState, @NotNull Rotation pRotation) {
        if (INTERNAL_FACING == null) return pState;
        return (BlockState)pState.setValue((DirectionProperty)INTERNAL_FACING, pRotation.rotate(pState.getValue((DirectionProperty)INTERNAL_FACING)));
    }

    @Override
    public @NotNull BlockState mirror(@NotNull BlockState pState, @NotNull Mirror pMirror) {
        if (INTERNAL_FACING == null) return pState;
        return pState.rotate(pMirror.getRotation((Direction)pState.getValue((DirectionProperty)INTERNAL_FACING)));
    }
}
