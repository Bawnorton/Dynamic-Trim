package io.github.andrew6rant.dynamictrim.fabric.client;

import io.github.andrew6rant.dynamictrim.DynamicTrimClient;
import net.fabricmc.api.ClientModInitializer;

public class DynamicTrimFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DynamicTrimClient.init();
    }
}
