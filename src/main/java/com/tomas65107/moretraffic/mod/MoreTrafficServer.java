package com.tomas65107.moretraffic.mod;

import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlock;
import com.tomas65107.moretraffic.registration.MTBlocks;
import com.tomas65107.moretraffic.registration.MTItems;
import de.mrjulsen.trafficcraft.block.TrafficSignPostBlock;
import de.mrjulsen.trafficcraft.item.WrenchItem;
import de.mrjulsen.trafficcraft.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Objects;

@EventBusSubscriber(modid = MoreTraffic.MODID)
public class MoreTrafficServer {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        BlockState state = level.getBlockState(pos);

        if ((state.getBlock() instanceof TrafficSignPostBlock || state.getBlock() instanceof AdvancedTrafficLightBlock)) {
            if (!level.isClientSide) {
                BlockState newState;

                Direction oldFacing = Direction.NORTH;
                if (state.hasProperty(AdvancedTrafficLightBlock.FACING)) oldFacing = state.getValue(AdvancedTrafficLightBlock.FACING);

                CompoundTag tag = null;
                if (level.getBlockEntity(pos) != null) tag = Objects.requireNonNull(level.getBlockEntity(pos)).saveWithoutMetadata(level.registryAccess());

                boolean shouldApplyBe = false;

                if (player.isShiftKeyDown()) {
                    if (!(player.getMainHandItem().getItem() instanceof WrenchItem)) return;

                    if (state.getBlock() == MTBlocks.ADV_1_TRAFFIC_LIGHT.get()) {
                        newState = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("trafficcraft", "traffic_sign_post")).defaultBlockState();
                        shouldApplyBe = true;
                    } else if (state.getBlock() == MTBlocks.ADV_2_TRAFFIC_LIGHT.get()) {
                        newState = MTBlocks.ADV_1_TRAFFIC_LIGHT.get().defaultBlockState().setValue(AdvancedTrafficLightBlock.FACING, oldFacing);;
                        shouldApplyBe = true;
                    } else if (state.getBlock() == MTBlocks.ADV_3_TRAFFIC_LIGHT.get()) {
                        newState = MTBlocks.ADV_2_TRAFFIC_LIGHT.get().defaultBlockState().setValue(AdvancedTrafficLightBlock.FACING, oldFacing);;
                        shouldApplyBe = true;
                    } else return;
                    if (!player.getAbilities().instabuild) player.addItem(MTItems.LIGHT_DIODE.toStack());

                } else {
                    if (!(player.getMainHandItem().is(MTItems.LIGHT_DIODE))) return;

                    if (state.getBlock() instanceof TrafficSignPostBlock) {
                        newState = MTBlocks.ADV_1_TRAFFIC_LIGHT.get().defaultBlockState().setValue(AdvancedTrafficLightBlock.FACING, player.getDirection().getOpposite());
                    } else if (state.getBlock() == MTBlocks.ADV_1_TRAFFIC_LIGHT.get()) {
                        newState = MTBlocks.ADV_2_TRAFFIC_LIGHT.get().defaultBlockState().setValue(AdvancedTrafficLightBlock.FACING, oldFacing);;
                        shouldApplyBe = true;
                    } else if (state.getBlock() == MTBlocks.ADV_2_TRAFFIC_LIGHT.get()) {
                        newState = MTBlocks.ADV_3_TRAFFIC_LIGHT.get().defaultBlockState().setValue(AdvancedTrafficLightBlock.FACING, oldFacing);;
                        shouldApplyBe = true;
                    } else return;
                    if (!player.getAbilities().instabuild) player.getMainHandItem().shrink(1);
                }

                level.setBlock(pos, newState, 3);

                if (tag != null && shouldApplyBe) {
                    BlockEntity newBe = level.getBlockEntity(pos);
                    if (newBe != null) {
                        newBe.loadWithComponents(tag, level.registryAccess());
                        newBe.setChanged();
                    }
                }

            }
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        BlockState state = event.getState();

        if (state.getBlock() instanceof TrafficSignPostBlock) {
            if (event.getEntity().getMainHandItem().getItem() instanceof PickaxeItem pickaxe) {
                event.setNewSpeed(pickaxe.getTier().getSpeed() / 2);
            }
        }
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Player player = event.getPlayer();

        if (state.getBlock() instanceof TrafficSignPostBlock) {
            if (!level.isClientSide && player.getMainHandItem().getItem() instanceof PickaxeItem) {
                Block.popResource(level, pos, new ItemStack(state.getBlock()));
            }
        }
    }


}
