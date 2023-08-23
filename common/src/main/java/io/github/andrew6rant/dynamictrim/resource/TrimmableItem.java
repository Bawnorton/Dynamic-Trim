package io.github.andrew6rant.dynamictrim.resource;

import net.minecraft.util.Identifier;

/**
 * @param type item type used for subfolders. E.g. "chestplate", "helmet", "elytra", "pickaxe".
 * @param model original item texture location.
 */
public record TrimmableItem(String type, Identifier model) {
    public Identifier resourceId() {
        return model.withPath("models/item/%s.json"::formatted);
    }
}
