package com.bawnorton.dynamictrim;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DynamicTrim {
    public static final String MOD_ID = "dynamictrim";
    public static final Identifier TRIM_PATTERN = id("trim_pattern");
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.debug("{} Initialized", MOD_ID);
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
