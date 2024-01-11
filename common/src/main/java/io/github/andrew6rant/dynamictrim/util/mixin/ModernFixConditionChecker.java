package io.github.andrew6rant.dynamictrim.util.mixin;

import io.github.andrew6rant.dynamictrim.DynamicTrimMixinConfigPlugin;
import io.github.andrew6rant.dynamictrim.compat.ModernFixCompat;

public class ModernFixConditionChecker implements AdvancedConditionChecker {
    @Override
    public boolean shouldApply() {
        return DynamicTrimMixinConfigPlugin.isModLoaded("modernfix") && ModernFixCompat.isDynamicResourcesEnabled();
    }
}
