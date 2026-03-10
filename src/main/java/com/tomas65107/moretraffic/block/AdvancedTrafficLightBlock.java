package com.tomas65107.moretraffic.block;

import com.mojang.serialization.MapCodec;
import com.sun.jdi.request.InvalidRequestStateException;
import com.tomas65107.moretraffic.data.trafficlightproperties.TrafficLightOrientation;
import com.tomas65107.moretraffic.data.trafficlightproperties.TrafficLightPosition;
import com.tomas65107.moretraffic.data.trafficlightproperties.TrafficLightScale;
import com.tomas65107.moretraffic.gui.containers.AdvancedTrafficLightMenu;
import com.tomas65107.moretraffic.registration.MTBlocks;
import com.tomas65107.moretraffic.registration.MTItems;
import com.tomas65107.moretraffic.rendering.BlockBoundingBoxes;
import de.mrjulsen.trafficcraft.block.data.ColorableBlock;
import de.mrjulsen.trafficcraft.block.data.ITrafficPostLike;
import de.mrjulsen.trafficcraft.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.tomas65107.moretraffic.data.helpers.HelperFunctions.rotateShape;
import static com.tomas65107.moretraffic.rendering.BlockBoundingBoxes.*;

public class AdvancedTrafficLightBlock extends ColorableBlock implements SimpleWaterloggedBlock, EntityBlock, ITrafficPostLike {
    public AdvancedTrafficLightBlock(Properties properties) {
        super(properties
                .mapColor(MapColor.METAL)
                .strength(4.0f, 12.0f)
                .sound(SoundType.ANVIL)
        );
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
    protected @NotNull List<ItemStack> getDrops(BlockState state, LootParams.@NotNull Builder params) {
        return List.of(state.getBlock().asItem().getDefaultInstance());
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return new ItemStack(MTItems.LIGHT_DIODE.asItem());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(SCALE, TrafficLightScale.S1_0X)
                .setValue(ORIENTATION, TrafficLightOrientation.VERTICAL)
                .setValue(POSITION, TrafficLightPosition.TOP);
    }

    private VoxelShape boundingBoxGetter(BlockState state) throws InvalidRequestStateException {
        if (state.getBlock() == MTBlocks.ADV_1_TRAFFIC_LIGHT.get()) {
            return rotateShape(state.getValue(FACING), advancedTraffic1Light);
        } else if (state.getBlock() == MTBlocks.ADV_2_TRAFFIC_LIGHT.get()) {
            return rotateShape(state.getValue(FACING), advancedTraffic2Light);
        } else if (state.getBlock() == MTBlocks.ADV_3_TRAFFIC_LIGHT.get()) {
            return rotateShape(state.getValue(FACING), advancedTraffic3Light);
        }
        throw new InvalidRequestStateException();
    }

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<TrafficLightScale> SCALE = EnumProperty.create("scale", TrafficLightScale.class);
    public static final EnumProperty<TrafficLightOrientation> ORIENTATION = EnumProperty.create("orientation", TrafficLightOrientation.class);
    public static final EnumProperty<TrafficLightPosition> POSITION = EnumProperty.create("position", TrafficLightPosition.class);

    public static final MapCodec<AdvancedTrafficLightBlock> CODEC = simpleCodec(AdvancedTrafficLightBlock::new);

    @Override public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {return boundingBoxGetter(state);}
    @Override public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {return boundingBoxGetter(state);}

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (stack.getItem() instanceof de.mrjulsen.trafficcraft.item.WrenchItem) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (id, inventory, p) -> new AdvancedTrafficLightMenu(id, inventory, pos),
                        Component.empty()
                ), buf -> buf.writeBlockPos(pos));
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, SCALE, ORIENTATION, POSITION);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new com.tomas65107.moretraffic.block.AdvancedTrafficLightBlockEntity(blockPos, blockState);
    }

    public @NotNull BlockState rotate(BlockState pState, Rotation pRotation) {
        return (BlockState)pState.setValue(FACING, pRotation.rotate((Direction)pState.getValue(FACING)));
    }

    public @NotNull BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation((Direction)pState.getValue(FACING)));
    }

    @Override
    public boolean canConnect(BlockState pState, Direction pDirection) {
        return pState.getValue(ORIENTATION) == TrafficLightOrientation.VERTICAL_SUPPORT;
    }

    @Override
    public boolean canAttach(BlockState pState, BlockPos pPos, Direction pDirection) {
        return pDirection != pState.getValue(FACING);
    }
}
