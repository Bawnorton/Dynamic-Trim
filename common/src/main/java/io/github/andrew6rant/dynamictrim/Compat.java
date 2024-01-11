package io.github.andrew6rant.dynamictrim;

import dev.architectury.injectables.annotations.ExpectPlatform;

public abstract class Compat {
    @ExpectPlatform
    public static boolean isAllTheTrimsLoaded() {
        throw new AssertionError();
    }
}
