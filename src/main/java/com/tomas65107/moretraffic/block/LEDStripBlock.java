package com.tomas65107.moretraffic.block;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.tomas65107.moretraffic.data.ISimpleBlockProperties;
import com.tomas65107.moretraffic.data.blocktypes.MTNormalBlock;
import com.tomas65107.moretraffic.gui.containers.LEDStripMenu;
import com.tomas65107.moretraffic.mod.registration.MTRegistrate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static com.tomas65107.moretraffic.helpers.HelperFunctions.rotateShapeSpecial;
import static com.tomas65107.moretraffic.rendering.BlockBoundingBoxes.*;

public class LEDStripBlock extends MTNormalBlock implements EntityBlock, IWrenchable {

    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light_level", 0, 15);

    public LEDStripBlock(Properties properties) {
        super(ISimpleBlockProperties.set(properties.lightLevel(state -> state.getValue(LIGHT_LEVEL)), SoundType.METAL, MapColor.NONE, ISimpleBlockProperties.Material.MODEL_NORMAL), FACING, null, PickaxeItem.class);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
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
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof LEDStripBlockEntity be) {
            if (be.emitsLight && !be.color.equals(DyeColor.BLACK)) {
                level.setBlock(pos, state.setValue(LIGHT_LEVEL, 7), 3);
            } else {
                level.setBlock(pos, state.setValue(LIGHT_LEVEL, 0), 3);
            }
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    @Override
    protected VoxelShape figureOutShapeRotation(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof LEDStripBlockEntity be) {
            float depth  = 2.6f;
            return rotateShapeSpecial(state.getValue(FACING), Block.box(
                    be.startPosX,
                    0,
                    16 - (be.startPosY + be.sizeY),
                    be.startPosX + be.sizeX,
                    depth,
                    16 - be.startPosY
            ));
        } else {
            return rotateShapeSpecial(state.getValue(FACING), LEDSTRIP); //block placement check
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(LIGHT_LEVEL, 0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, LIGHT_LEVEL);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new LEDStripBlockEntity(MTRegistrate.LED_STRIP_BE.get(), blockPos, blockState);
    }

}
