package com.tomas65107.moretraffic.block;

import com.mojang.serialization.MapCodec;
import com.tomas65107.moretraffic.registration.MTRegistrate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.tomas65107.moretraffic.data.helpers.HelperFunctions.rotateShape;
import static com.tomas65107.moretraffic.rendering.BlockBoundingBoxes.traffic_display;

public class TrafficDisplayBlock extends Block implements EntityBlock {

    public static final MapCodec<FlashingBlinkerBlock> CODEC = simpleCodec(FlashingBlinkerBlock::new);

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public TrafficDisplayBlock(Properties properties) {
        super(properties
                .mapColor(MapColor.METAL)
                .strength(10.0f, 40.0f)
                .sound(SoundType.METAL)
        );
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

    @Override public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {return rotateShape(state.getValue(FACING), traffic_display);}
    @Override public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {return rotateShape(state.getValue(FACING), traffic_display);}

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

    public @NotNull BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation((Direction)pState.getValue(FACING)));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TrafficDisplayBlockEntity(MTRegistrate.TRAFFIC_DISPLAY_BE.get(), blockPos, blockState);
    }


    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
