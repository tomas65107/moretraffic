package com.tomas65107.moretraffic.block;

import net.minecraftforge.network.NetworkHooks;

import com.tomas65107.moretraffic.data.ISimpleBlockProperties;
import com.tomas65107.moretraffic.gui.containers.LightControlCabinetMenu;
import com.tomas65107.moretraffic.mod.registration.MTRegistrate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LightControlCabinetBlock extends Block implements EntityBlock {
    public LightControlCabinetBlock(Properties properties) {
        super(ISimpleBlockProperties.set(properties, SoundType.NETHERITE_BLOCK, MapColor.METAL, ISimpleBlockProperties.Material.FULLBLOCK_TOUGH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection());
    }

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
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
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.@NotNull Builder params) {
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
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof de.mrjulsen.trafficcraft.item.WrenchItem || stack.getItem() instanceof com.simibubi.create.content.equipment.wrench.WrenchItem) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
                        (id, inventory, p) -> new LightControlCabinetMenu(id, inventory, pos), Component.empty()
                ), buf -> buf.writeBlockPos(pos));
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new LightControlCabinetBlockEntity(MTRegistrate.LIGHT_CONTROL_CABINET_BE.get(), blockPos, blockState);
    }
}
