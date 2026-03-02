package com.tomas65107.moretraffic.mod;

import de.mrjulsen.trafficcraft.block.TrafficSignPostBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = MoreTraffic.MODID)
public class MoreTrafficServer {

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
