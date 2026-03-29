package com.tomas65107.moretraffic.block;

import com.tomas65107.moretraffic.gui.containers.LightControlCabinetMenu;
import com.tomas65107.moretraffic.registration.MTRegistrate;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LightControlCabinetBlock extends Block implements EntityBlock {
    public LightControlCabinetBlock(Properties properties) {
        super(properties
                .mapColor(MapColor.METAL)
                .strength(10.0f, 40.0f)
                .sound(SoundType.NETHERITE_BLOCK)
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, WATERLOGGED);
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;

        // Use reference from your registration
        if (type == MTRegistrate.LIGHT_CONTROL_CABINET_BE.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof LightControlCabinetBlockEntity cabinetBE) {
                    LightControlCabinetBlockEntity.serverTick(lvl, pos, st, cabinetBE);
                }
            };
        }

        return null;
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
                        (id, inventory, p) -> new LightControlCabinetMenu(id, inventory, pos), Component.empty()
                ), buf -> buf.writeBlockPos(pos));
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new LightControlCabinetBlockEntity(MTRegistrate.LIGHT_CONTROL_CABINET_BE.get(), blockPos, blockState);
    }
}
