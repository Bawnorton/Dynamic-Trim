package io.github.andrew6rant.dynamictrim.compat;

import net.fabricmc.loader.api.FabricLoader;

public abstract class Compat {
    public static boolean isStackedTrimsLoaded() {
        return FabricLoader.getInstance().isModLoaded("stacked_trims");
    }

    public static boolean isAllTheTrimsLoaded() {
        return FabricLoader.getInstance().isModLoaded("allthetrims");
    }
}
