package io.github.andrew6rant.dynamictrim.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class CompatImpl {
    public static boolean isAllTheTrimsLoaded() {
        return FabricLoader.getInstance().isModLoaded("allthetrims");
    }

    public static boolean isModernFixLoaded() {
        return FabricLoader.getInstance().isModLoaded("modernfix");
    }
}
