package com.bawnorton.dynamictrim.client.platform;

import com.bawnorton.dynamictrim.DynamicTrim;
import com.bawnorton.dynamictrim.client.DynamicTrimClient;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;

public final class DynamicTrimClientWrapper implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DynamicTrimClient.init();
    }
}
//?} elif neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = DynamicTrim.MOD_ID, dist = Dist.CLIENT)
public final class DynamicTrimClientWrapper {
    public DynamicTrimClientWrapper() {
        DynamicTrimClient.init();
    }
}
*///?}
