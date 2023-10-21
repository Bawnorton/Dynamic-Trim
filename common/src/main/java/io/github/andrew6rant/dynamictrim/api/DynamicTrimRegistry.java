package io.github.andrew6rant.dynamictrim.api;

import io.github.andrew6rant.dynamictrim.resource.DynamicTrimLoader;
import io.github.andrew6rant.dynamictrim.resource.TrimmableItem;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class DynamicTrimRegistry {
    public static void add(TrimmableItem item) {
        DynamicTrimLoader.addCustom(() -> Collections.singletonList(item));
    }

    public static void addAll(Collection<TrimmableItem> items) {
        DynamicTrimLoader.addCustom(() -> items);
    }

    public static void addAll(Supplier<Collection<TrimmableItem>> items) {
        DynamicTrimLoader.addCustom(items);
    }
}
