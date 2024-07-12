package com.bawnorton.modid.client;

import com.bawnorton.modid.ModId;

public final class ModIdClient {
    public static void init() {
        ModId.LOGGER.info("{} Client Initialized", ModId.MOD_ID);
    }
}
