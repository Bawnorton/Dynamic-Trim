package io.github.andrew6rant.dynamictrim;

import io.github.andrew6rant.dynamictrim.util.LogWrapper;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.LoggerFactory;

public class DynamicTrimClient implements ClientModInitializer {
    public static final String MOD_ID = "dynamictrim";
    public static final LogWrapper LOGGER = LogWrapper.of(LoggerFactory.getLogger(MOD_ID), "[Dynamic Trim]");
    public static final String PATTERN_ASSET_NAME = "dynamic";

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Dynamic Trim Client");
    }
}