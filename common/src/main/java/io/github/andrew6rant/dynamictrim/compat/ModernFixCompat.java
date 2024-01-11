package io.github.andrew6rant.dynamictrim.compat;

import org.embeddedt.modernfix.core.ModernFixMixinPlugin;
import org.embeddedt.modernfix.core.config.ModernFixEarlyConfig;
import org.embeddedt.modernfix.core.config.Option;

import java.util.Map;

public class ModernFixCompat {
    public static boolean isDynamicResourcesEnabled() {
        ModernFixEarlyConfig config = ModernFixMixinPlugin.instance.config;
        Map<String, Option> options = config.getOptionMap();
        return options.get("mixin.perf.dynamic_resources").isEnabled();
    }
}
