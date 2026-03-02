package com.tomas65107.moretraffic.mod;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;

public class ClientCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("copypos")
                .executes(ctx -> copyPos(ctx.getSource(), "commas"))

                .then(Commands.literal("commas")
                        .executes(ctx -> copyPos(ctx.getSource(), "commas")))

                .then(Commands.literal("spaces")
                        .executes(ctx -> copyPos(ctx.getSource(), "spaces")))
        );
    }

    private static int copyPos(CommandSourceStack source, String mode) {
        Minecraft mc = Minecraft.getInstance();

        if (!(mc.hitResult instanceof BlockHitResult hit)) {
            mc.player.sendSystemMessage(Component.translatable("moretraffic.commands.copypos.error"));
            return 0;
        }

        BlockPos pos = hit.getBlockPos();

        String text = mode.equals("spaces")
                ? pos.getX() + " " + pos.getY() + " " + pos.getZ()
                : pos.getX() + ", " + pos.getY() + ", " + pos.getZ();

        mc.keyboardHandler.setClipboard(text);

        mc.player.sendSystemMessage(
                Component.translatable("moretraffic.commands.copypos.success", Component.literal(text).withStyle(ChatFormatting.ITALIC))
        );

        return 1;
    }
}