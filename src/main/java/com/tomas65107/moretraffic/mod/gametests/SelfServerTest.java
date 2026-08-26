package com.tomas65107.moretraffic.mod.gametests;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SelfServerTest {

    public static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        LOGGER.info("Starting MoreTraffic self-test wizard!");
        LOGGER.warn("THIS SHOULD RUN ONLY UNDER CERTAIN CIRCUMSTANCES, NOT ON PUBLIC SERVERS!");

        try {
            runTests(server);
            LOGGER.info("All Self-Tests passed successfully!");
            LOGGER.info("MORETRAFFIC_SELF_TEST:SUCCESS");

        } catch (Throwable e) {
            LOGGER.error("Test failed!", e);
            e.printStackTrace();
            LOGGER.info("MORETRAFFIC_SELF_TEST:FAIL");
        }

    }

    private static void runTests(@NotNull MinecraftServer server) {
        if (server == null) {
            throw new IllegalStateException("MinecraftServer is null");
        }
        // add more in future
    }
}