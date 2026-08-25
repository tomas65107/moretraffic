package com.tomas65107.moretraffic.block;

import com.tomas65107.moretraffic.data.ISimpleBlockProperties;
import com.tomas65107.moretraffic.data.blocktypes.MTNormalBlock;
import com.tomas65107.moretraffic.mod.registration.MTRegistrate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import static com.tomas65107.moretraffic.rendering.BlockBoundingBoxes.FLOODLIGHT;

public class FloodlightBlock extends MTNormalBlock implements EntityBlock {

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public FloodlightBlock(Properties properties) {
        super(ISimpleBlockProperties.set(properties, SoundType.METAL, MapColor.METAL, ISimpleBlockProperties.Material.MODEL_NORMAL), FACING, FLOODLIGHT, PickaxeItem.class);
    }
@Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
//        if (level.getBlockEntity(pos) instanceof FlashingBlinkerBlockEntity be) {
//            be.lightStatus = level.hasNeighborSignal(pos);
//            be.setChanged();
//            level.sendBlockUpdated(pos, state, state, 3);
//        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FloodlightBlockEntity(MTRegistrate.FLOODLIGHT_BE.get(), blockPos, blockState);
    }
}
