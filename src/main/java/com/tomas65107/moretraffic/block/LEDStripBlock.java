package com.tomas65107.moretraffic.block;

import com.mojang.serialization.MapCodec;
import com.tomas65107.moretraffic.data.ISimpleBlockProperties;
import com.tomas65107.moretraffic.gui.containers.LEDStripMenu;
import com.tomas65107.moretraffic.registration.MTRegistrate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.tomas65107.moretraffic.data.helpers.HelperFunctions.rotateShape;
import static com.tomas65107.moretraffic.data.helpers.HelperFunctions.rotateShapeSpecial;
import static com.tomas65107.moretraffic.rendering.BlockBoundingBoxes.*;

public class LEDStripBlock extends Block implements EntityBlock {

    public static final MapCodec<LEDStripBlock> CODEC = simpleCodec(LEDStripBlock::new);

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public LEDStripBlock(Properties properties) {
        super(ISimpleBlockProperties.set(properties, SoundType.METAL, MapColor.NONE, ISimpleBlockProperties.Material.MODEL_NORMAL));
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

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
        if (stack.getItem() instanceof de.mrjulsen.trafficcraft.item.WrenchItem || stack.getItem() instanceof com.simibubi.create.content.equipment.wrench.WrenchItem) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (id, inventory, p) -> new LEDStripMenu(id, inventory, pos), Component.empty()
                ), buf -> buf.writeBlockPos(pos));
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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

    public static VoxelShape getVoxelShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof LEDStripBlockEntity be) {
            float depth  = 2.6f;
            return Block.box(
                    be.startPosX,
                    0,
                    16 - (be.startPosY + be.sizeY),
                    be.startPosX + be.sizeX,
                    depth,
                    16 - be.startPosY
            );
        } else {
            return LEDSTRIP; //block placement check
        }
    }

    @Override public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {return rotateShapeSpecial(state.getValue(FACING), getVoxelShape(state, level, pos));}
    @Override public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {return rotateShapeSpecial(state.getValue(FACING), getVoxelShape(state, level, pos));}

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

    public @NotNull BlockState rotate(BlockState pState, Rotation pRotation) {
        return (BlockState)pState.setValue(FACING, pRotation.rotate((Direction)pState.getValue(FACING)));
    }

    public @NotNull BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation((Direction)pState.getValue(FACING)));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new LEDStripBlockEntity(MTRegistrate.LED_STRIP_BE.get(), blockPos, blockState);
    }


    @Override
    protected MapCodec<LEDStripBlock> codec() {
        return CODEC;
    }
}
