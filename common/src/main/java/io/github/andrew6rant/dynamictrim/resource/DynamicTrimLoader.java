package io.github.andrew6rant.dynamictrim.resource;

import dev.architectury.platform.Platform;
import io.github.andrew6rant.dynamictrim.DynamicTrimClient;
import io.github.andrew6rant.dynamictrim.json.JsonHelper;
import io.github.andrew6rant.dynamictrim.util.DebugHelper;
import net.minecraft.item.ArmorItem;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Supplier;

public class DynamicTrimLoader {
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

    private static List<TrimmableItem> getArmourEntries() {
        List<TrimmableItem> entries = new ArrayList<>();
        Registries.ITEM.forEach(item -> {
            if (!(item instanceof ArmorItem armour)) return;
            Identifier id = Registries.ITEM.getId(item);
            if (id.getNamespace().equals("betterend"))
                return; // Better End dynamically generates models elsewhere. See bclib package - TODO

            String armourType = getArmourType(armour);
            if (armourType != null) entries.add(new TrimmableItem(armourType, id));
        });
        return entries;
    }

    public static void loadDynamicTrims(Map<Identifier, Resource> resourceMap) {
        List<TrimmableItem> trimmables = getArmourEntries();
        CUSTOM.forEach(supplier -> trimmables.addAll(supplier.get()));
        for (TrimmableItem item : trimmables) {
            loadTrims(item, resourceMap);
        }
    }

    private static void loadTrims(TrimmableItem item, Map<Identifier, Resource> resourceMap) {
        Identifier equipmentId = item.id();

        TrimmableResource equipmentResource = TrimModelHelper.buildResource(item, resourceMap);
        if (equipmentResource == null) {
            DynamicTrimClient.LOGGER.warn("Item %s's resource could not be built, skipping".formatted(equipmentId));
            return;
        }

        resourceMap.put(item.resourceId(), equipmentResource.createDynamicResource());
        if (Platform.isDevelopmentEnvironment()) {
            final String modelString = equipmentResource.modelString();
            DebugHelper.createDebugFile("models", "%s.json".formatted(equipmentId), modelString);
        }

        equipmentResource.forEachOverride((override, material) -> {
            for (Identifier patternId : TrimModelHelper.TEMPLATE_IDS) {
                OverrideResource overrideResource = equipmentResource.createOverrideResource(patternId, material);
                resourceMap.put(overrideResource.modelId(), overrideResource.modelResource(equipmentResource.resource().getPack()));
                if (Platform.isDevelopmentEnvironment()) {
                    DebugHelper.createDebugFile("models", "models/%s/trims/%s/%s_trim.json".formatted(
                            equipmentResource.baseTextureId().getPath(),
                            patternId.toString().replace(":", "-"),
                            material
                    ), JsonHelper.toJsonString(overrideResource.modelResourceJson()));
                }
            }
        });
    }
}
