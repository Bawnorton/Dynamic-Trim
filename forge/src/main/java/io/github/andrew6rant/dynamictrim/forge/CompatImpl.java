package io.github.andrew6rant.dynamictrim.forge;

import net.minecraftforge.fml.ModList;

public class CompatImpl {
    public static boolean isAllTheTrimsLoaded() {
        return ModList.get().isLoaded("allthetrims");
    }
}
