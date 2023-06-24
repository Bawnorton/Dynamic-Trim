package io.github.andrew6rant.dynamictrim;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicTrimClient implements ClientModInitializer {
    public static final String MOD_ID = "dynamictrim";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Boolean isStackedTrimsEnabled = FabricLoader.getInstance().isModLoaded("stacked_trims");
    public static final Boolean isAllTheTrimsEnabled = FabricLoader.getInstance().isModLoaded("allthetrims");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Dynamic Trim Client");
    }
}