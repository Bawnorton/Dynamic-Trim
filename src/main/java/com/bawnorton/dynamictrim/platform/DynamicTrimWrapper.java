package com.bawnorton.dynamictrim.platform;

import com.bawnorton.dynamictrim.DynamicTrim;

//? if fabric {
import net.fabricmc.api.ModInitializer;

public final class DynamicTrimWrapper implements ModInitializer {
    @Override
    public void onInitialize() {
        DynamicTrim.init();
    }
}
//?} elif neoforge {
/*import net.neoforged.fml.common.Mod;

@Mod(DynamicTrim.MOD_ID)
public final class DynamicTrimWrapper {
    public DynamicTrimWrapper() {
        DynamicTrim.init();
    }
}
*///?}
