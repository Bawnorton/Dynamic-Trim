package io.github.andrew6rant.dynamictrim.util;

import net.minecraft.util.Identifier;

public abstract class ThreadedLocals {
    public static final ThreadLocal<String> PATTERN = new ThreadLocal<>();
    public static final ThreadLocal<Identifier> ASSET_ID = new ThreadLocal<>();
}
