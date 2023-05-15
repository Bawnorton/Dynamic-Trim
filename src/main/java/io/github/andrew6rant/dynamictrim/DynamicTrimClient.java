package io.github.andrew6rant.dynamictrim;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicTrimClient implements ClientModInitializer {
    public static final Boolean isStackedTrimsEnabled = FabricLoader.getInstance().isModLoaded("stacked_trims");

    @Override
    public void onInitializeClient() {
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new ArmorModelProvider());
    }
}