package com.bawnorton.modid.platform;

import com.bawnorton.modid.ModId;

//? if fabric {
/*import net.fabricmc.api.ModInitializer;

public final class ModIdWrapper implements ModInitializer {
    @Override
    public void onInitialize() {
        ModId.init();
    }
}
*///?} elif neoforge {
import net.neoforged.fml.common.Mod;

@Mod(ModId.MOD_ID)
public final class ModIdWrapper {
    public ModIdWrapper() {
        ModId.init();
    }
}
//?}
