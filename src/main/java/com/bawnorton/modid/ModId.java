package com.bawnorton.modid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModId {
    public static final String MOD_ID = "modid";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("{} Initialized", MOD_ID);
    }
}
