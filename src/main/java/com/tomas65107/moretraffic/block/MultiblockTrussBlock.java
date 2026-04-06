package com.tomas65107.moretraffic.block;

import com.mojang.serialization.MapCodec;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.tomas65107.moretraffic.data.ISimpleBlockProperties;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import com.tomas65107.moretraffic.registration.MTRegistrate;
import de.mrjulsen.trafficcraft.block.data.ColorableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MultiblockTrussBlock extends ColorableBlock implements EntityBlock, ISimpleBlockProperties {

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public static final MapCodec<MultiblockTrussBlock> CODEC = simpleCodec(MultiblockTrussBlock::new);

    public MultiblockTrussBlock(Properties properties) {
        super(ISimpleBlockProperties.set(properties, SoundType.NETHERITE_BLOCK, MapColor.COLOR_LIGHT_GRAY, Material.FULLBLOCK_NORMAL));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MTBaseColoredBlockEntity(MTRegistrate.BASE_COLORED_BE.get(), blockPos, blockState);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);

        BlockState neighbor = level.getBlockState(neighborPos);

        if (!(neighbor.getBlock() instanceof MultiblockTrussBlock)) return;

        if (neighbor.getValue(FACING).equals(state.getValue(FACING))) {
            level.setBlock(pos, state.setValue(FACING, neighbor.getValue(FACING).getOpposite()), 3);

        }
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof de.mrjulsen.trafficcraft.item.WrenchItem || stack.getItem() instanceof com.simibubi.create.content.equipment.wrench.WrenchItem) {
            level.setBlock(pos, state.setValue(FACING, state.getValue(FACING).getOpposite()), 3);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public float getDestroyProgress(@NotNull BlockState state, Player player, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        var item = player.getMainHandItem().getItem();
        if (item instanceof PickaxeItem pickaxe) {
            return pickaxe.getTier().getSpeed() / 70;
        }
        return super.getDestroyProgress(state, player, world, pos);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction newFacing = context.getNearestLookingDirection().getOpposite();
        BlockPos clickedBlockPos = context.getClickedPos().relative(context.getNearestLookingDirection(), 1);
        BlockState clickedBlockState = context.getLevel().getBlockState(clickedBlockPos);

        assert context.getPlayer() != null;
        if (context.getPlayer().isShiftKeyDown()) return this.defaultBlockState().setValue(FACING, newFacing);

        if (clickedBlockState.getBlock() instanceof MultiblockTrussBlock) {
            if (newFacing.equals(clickedBlockState.getValue(FACING)) || newFacing.equals(clickedBlockState.getValue(FACING).getOpposite())) {
                newFacing = clickedBlockState.getValue(FACING).getOpposite();
            }
        }

        return this.defaultBlockState().setValue(FACING, newFacing);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public @NotNull BlockState rotate(BlockState pState, Rotation pRotation) {
        return (BlockState)pState.setValue(FACING, pRotation.rotate((Direction)pState.getValue(FACING)));
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.@NotNull Builder params) {
        return ISimpleBlockProperties.super.getDrops(state, params);
    }
}
