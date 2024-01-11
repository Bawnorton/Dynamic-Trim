package io.github.andrew6rant.dynamictrim.resource;

import io.github.andrew6rant.dynamictrim.DynamicTrimClient;
import io.github.andrew6rant.dynamictrim.json.JsonHelper;
import io.github.andrew6rant.dynamictrim.util.DebugHelper;
import net.minecraft.item.ArmorItem;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.*;
import java.util.function.Supplier;

public class DynamicTrimLoader {
    private static final Map<Identifier, TrimmableItem> TRIMMABLE_ITEMS = new HashMap<>();
    private static final Set<Supplier<Collection<TrimmableItem>>> CUSTOM = new HashSet<>();

    public static void addCustom(Supplier<Collection<TrimmableItem>> item) {
        CUSTOM.add(item);
    }

    private static String getArmourType(ArmorItem value) {
        return switch (value.getSlotType()) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            case MAINHAND, OFFHAND -> null;
        };
    }

    private static Map<Identifier, TrimmableItem> getTrimmableItems() {
        if(TRIMMABLE_ITEMS.isEmpty()) {
            Registries.ITEM.forEach(item -> {
                if (!(item instanceof ArmorItem armour)) return;
                Identifier id = Registries.ITEM.getId(item);
                if (id.getNamespace().equals("betterend"))
                    return; // Better End dynamically generates models elsewhere. See bclib package - TODO

                String armourType = getArmourType(armour);
                TrimmableItem trimmable = new TrimmableItem(armourType, id);
                if (armourType != null) TRIMMABLE_ITEMS.put(trimmable.resourceId(), trimmable);
            });
            CUSTOM.forEach(supplier -> {
                Collection<TrimmableItem> trimmableItems = supplier.get();
                trimmableItems.forEach(item -> TRIMMABLE_ITEMS.put(item.resourceId(), item));
            });
        }
        return TRIMMABLE_ITEMS;
    }

    public static boolean isTrimmable(Identifier location) {
        return getTrimmableItems().containsKey(location);
    }

    public static void loadDynamicTrims(Map<Identifier, Resource> resourceMap) {
        for (TrimmableItem item : getTrimmableItems().values()) {
            loadTrims(item, resourceMap);
        }
    }

    public static Map<Identifier, Resource> generateResourceMapForSingleTrim(Identifier location, Resource original) {
        TrimmableItem item = getTrimmableItems().get(location);
        if(item == null) return Map.of(location, original);

        Map<Identifier, Resource> resourceMap = Util.make(new HashMap<>(), map -> map.put(location, original));
        loadTrims(item, resourceMap);
        return resourceMap;
    }

    private static void loadTrims(TrimmableItem item, Map<Identifier, Resource> resourceMap) {
        Identifier equipmentId = item.id();

        TrimmableResource equipmentResource = TrimModelHelper.buildResource(item, resourceMap);
        if (equipmentResource == null) {
            DynamicTrimClient.LOGGER.warn("Item %s's resource could not be built, skipping".formatted(equipmentId));
            return;
        }

        resourceMap.put(item.resourceId(), equipmentResource.createDynamicResource());
        DebugHelper.createDebugFile("models", "%s.json".formatted(equipmentId), equipmentResource.modelString());

        equipmentResource.forEachOverride((override, material) -> {
            for (Identifier patternId : TrimModelHelper.TEMPLATE_IDS) {
                OverrideResource overrideResource = equipmentResource.createOverrideResource(patternId, material);
                resourceMap.put(overrideResource.modelId(), overrideResource.toResource(equipmentResource.resource().getPack()));
                DebugHelper.createDebugFile("models", "models/%s/trims/%s/%s_trim.json".formatted(
                        equipmentResource.baseTextureId().getPath(),
                        patternId.toString().replace(":", "-"),
                        material
                ), JsonHelper.toJsonString(overrideResource.modelResourceJson()));
            }
        });
    }
}
