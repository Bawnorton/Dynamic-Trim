package io.github.andrew6rant.dynamictrim.forge;

import dev.architectury.platform.forge.EventBuses;
import io.github.andrew6rant.dynamictrim.DynamicTrimClient;
import io.github.andrew6rant.dynamictrim.forge.client.DynamicTrimForgeClient;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DynamicTrimClient.MOD_ID)
public class DynamicTrimForge {
    public DynamicTrimForge() {
        EventBuses.registerModEventBus(DynamicTrimClient.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DynamicTrimForgeClient::init);
    }
}
