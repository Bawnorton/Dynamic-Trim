package io.github.andrew6rant.dynamictrim.util.mixin;

import io.github.andrew6rant.dynamictrim.Compat;
import io.github.andrew6rant.dynamictrim.compat.ModernFixCompat;

public class ModernFixConditionChecker implements AdvancedConditionChecker {
    @Override
    public boolean shouldApply() {
        return Compat.isModernFixLoaded() && ModernFixCompat.isDynamicResourcesEnabled();
    }
}
