package com.tomas65107.moretraffic.block;


import com.tomas65107.moretraffic.data.ISimpleBlockProperties;
import com.tomas65107.moretraffic.data.blocktypes.MTNormalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

import static com.tomas65107.moretraffic.helpers.ColorHelper.rgb;

public class ExposedConcrete extends MTNormalBlock {

    public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 4);
    private static final int[] WEIGHTS = {
            1, 1, 1,
            2, 2, 2,
            3, 3,
            4
    };

    public ExposedConcrete(BlockBehaviour.Properties properties) {
        super(ISimpleBlockProperties.set(properties, SoundType.GILDED_BLACKSTONE, DyeColor.WHITE.getMapColor(), ISimpleBlockProperties.Material.FULLBLOCK_TOUGH), null, Shapes.block(), PickaxeItem.class);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (level.isClientSide()) return;

        if (state.getValue(TYPE).equals(0) && level.getBlockState(pos).getBlock() instanceof ExposedConcrete) level.setBlock(pos, state.setValue(TYPE, WEIGHTS[level.random.nextInt(WEIGHTS.length)]), 3);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof de.mrjulsen.trafficcraft.item.WrenchItem || stack.getItem() instanceof com.simibubi.create.content.equipment.wrench.WrenchItem) {
            int next = state.getValue(TYPE) + 1;
            if (next > 4) next = 1;

            if (!level.isClientSide) level.setBlock(pos, state.setValue(TYPE, next), 3);

            player.displayClientMessage(Component.translatable("interaction.moretraffic.type", next), true);
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, hit);
    }
}
