package com.bawnorton.modid.client.platform;

import com.bawnorton.modid.client.ModIdClient;

//? if fabric {
/*import net.fabricmc.api.ClientModInitializer;

public final class ModIdClientWrapper implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModIdClient.init();
    }
}
*///?} elif neoforge {
import com.bawnorton.modid.ModId;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = ModId.MOD_ID, dist = Dist.CLIENT)
public final class ModIdClientWrapper {
    public ModIdClientWrapper() {
        ModIdClient.init();
    }
}
//?}
