package io.github.andrew6rant.dynamictrim.resource;

import net.minecraft.util.Identifier;

/**
 * @param type item type used for subfolders. E.g. "chestplate", "helmet", "elytra", "pickaxe".
 * @param id original item texture location.
 */
public record TrimmableItem(String type, Identifier id) {
    public Identifier resourceId() {
        return id.withPath("models/item/%s.json"::formatted);
    }
}
