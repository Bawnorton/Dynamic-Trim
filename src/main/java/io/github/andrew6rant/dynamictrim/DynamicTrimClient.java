package io.github.andrew6rant.dynamictrim;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicTrimClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new ArmorModelProvider());
    }
}