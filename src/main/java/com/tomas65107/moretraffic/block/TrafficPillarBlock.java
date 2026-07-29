package com.tomas65107.moretraffic.block;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.tomas65107.moretraffic.data.ISimpleBlockProperties;
import com.tomas65107.moretraffic.data.blocktypes.MTBaseColoredBlockEntity;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import com.tomas65107.moretraffic.mod.registration.MTRegistrate;
import de.mrjulsen.trafficcraft.block.TrafficSignPostBlock;
import de.mrjulsen.trafficcraft.block.data.ColorableBlock;
import de.mrjulsen.trafficcraft.block.data.ITrafficPostLike;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.tomas65107.moretraffic.helpers.HelperFunctions.rotateShapeSpecial;
import static com.tomas65107.moretraffic.rendering.BlockBoundingBoxes.*;
import static de.mrjulsen.trafficcraft.registry.ModBlocks.TRAFFIC_SIGN_POST;

public class TrafficPillarBlock extends ColorableBlock implements ITrafficPostLike, IWrenchable {

    @Override
    public boolean canAttach(BlockState blockState, BlockPos blockPos, Direction direction) {
        return (Boolean)((blockState.getValue(TYPE).equals(PillarTypes.TRANSITION)));
    }

    @Override
    public boolean canConnect(BlockState pState, Direction pDirection) {
        return true;
    }

    public enum PillarTypes implements StringRepresentable {
        NORMAL("normal"),
        TRANSITION("transition"),
        LADDER("ladder"),
        L_BEAM("l_beam");

        public final String stringRepresentableName;

        PillarTypes(String stringRepresentableName) { this.stringRepresentableName = stringRepresentableName; }

        @Override
        public String getSerializedName() { return stringRepresentableName; }
    }

    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final EnumProperty<PillarTypes> TYPE = EnumProperty.create("type", PillarTypes.class);

    public TrafficPillarBlock(Properties properties) {
        super(ISimpleBlockProperties.set(properties, SoundType.STONE, MapColor.COLOR_LIGHT_GRAY, ISimpleBlockProperties.Material.MODEL_TOUGH));
    }
@Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MTBaseColoredBlockEntity(MTRegistrate.BASE_COLORED_BE.get(), blockPos, blockState);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.@NotNull Builder params) {
        return List.of(state.getBlock().asItem().getDefaultInstance());
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof de.mrjulsen.trafficcraft.item.WrenchItem || stack.getItem() instanceof com.simibubi.create.content.equipment.wrench.WrenchItem) {
            var newState = state.cycle(TYPE);
            if (newState.getValue(TYPE).equals(PillarTypes.L_BEAM)) newState = newState.cycle(TYPE);
            level.setBlock(pos, newState, 3);
            return InteractionResult.SUCCESS;
        }
        if (stack.getItem().equals(MTRegistrate.TRAFFIC_PILLAR.asItem())) {
            if (state.getValue(TYPE).equals(PillarTypes.L_BEAM)) return useDefaultInteraction(state, level, pos, player, hand, hit);
            MoreTraffic.LOGGER.debug(hit.getDirection().getName());

            if (state.getValue(FACING).equals(Direction.UP) || state.getValue(FACING).equals(Direction.DOWN)) {
                //model upright
                switch (hit.getDirection()) {
                    case UP, DOWN -> {return useDefaultInteraction(state, level, pos, player, hand, hit);}}
            } else {
                //model flat
                switch (hit.getDirection()) {
                    case SOUTH, EAST, WEST, NORTH, UP -> {return useDefaultInteraction(state, level, pos, player, hand, hit);}
                }
            }

            state = state.setValue(FACING, hit.getDirection());
            state = state.setValue(TYPE, PillarTypes.L_BEAM);
            level.setBlock(pos, state, 3);

            return InteractionResult.SUCCESS;
        }
        if (stack.getItem().equals(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("trafficcraft", "traffic_sign_post")).asItem())) {
            if (hit.getDirection().equals(Direction.UP) && state.getValue(FACING).equals(Direction.DOWN)) {
                level.setBlock(pos, state.setValue(TYPE, PillarTypes.TRANSITION), 3);
            }
        }

        return useDefaultInteraction(state, level, pos, player, hand, hit);
    }

    private InteractionResult useDefaultInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            level.setBlock(pos, state.setValue(FACING, state.getValue(FACING).getOpposite()), 3);
            return InteractionResult.SUCCESS;
        }

        return super.use(state, level, pos, player, hand, hitResult);
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
        Direction dir = context.getClickedFace().getOpposite();
        return this.defaultBlockState().setValue(FACING, dir).setValue(TYPE, PillarTypes.NORMAL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, TYPE);
    }

    public @NotNull BlockState rotate(BlockState pState, Rotation pRotation) {
        return (BlockState)pState.setValue(FACING, pRotation.rotate((Direction)pState.getValue(FACING)));
    }

}
