package io.github.andrew6rant.dynamictrim.forge.client;

import io.github.andrew6rant.dynamictrim.DynamicTrimClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DynamicTrimClient.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DynamicTrimForgeClient {
    public static void init(FMLClientSetupEvent event) {
        DynamicTrimClient.init();
    }
}
