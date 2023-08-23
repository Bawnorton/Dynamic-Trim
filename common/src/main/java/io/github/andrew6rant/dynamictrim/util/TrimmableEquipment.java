package io.github.andrew6rant.dynamictrim.util;

import net.minecraft.item.Equipment;
import net.minecraft.util.Identifier;

public record TrimmableEquipment(Identifier id, Equipment value, String armourType) {
    public TrimmableEquipment(Identifier id, Equipment value) {
        this(id, value, getArmourType(value));
    }

    private static String getArmourType(Equipment value) {
        return switch (value.getSlotType()) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            case MAINHAND, OFFHAND -> null;
        };
    }

    public Identifier resourceId() {
        return new Identifier(id.getNamespace(), "models/item/" + id.getPath() + ".json");
    }
}
