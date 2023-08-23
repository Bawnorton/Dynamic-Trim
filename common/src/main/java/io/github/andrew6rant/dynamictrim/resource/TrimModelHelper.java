package io.github.andrew6rant.dynamictrim.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.andrew6rant.dynamictrim.DynamicTrimClient;
import io.github.andrew6rant.dynamictrim.extend.SmithingTemplateItemExtender;
import io.github.andrew6rant.dynamictrim.json.JsonHelper;
import net.minecraft.item.Equipment;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.util.*;

public class TrimModelHelper {
    public static final Set<TrimmableEquipment> TRIMMABLES = new HashSet<>();
    public static final List<Identifier> TEMPLATE_IDS = new ArrayList<>();

    static {
        Registries.ITEM.stream()
                .filter(item -> item instanceof Equipment)
                .forEach(item -> {
                    Equipment equipment = (Equipment) item;
                    Identifier equipmentId = Registries.ITEM.getId(item);
                    if(equipmentId.getNamespace().equals("betterend")) return; // Better End dynamically generates models elsewhere. See bclib package - TODO

                    TRIMMABLES.add(new TrimmableEquipment(equipmentId, equipment));
                });
        Registries.ITEM.stream()
                .filter(item -> item instanceof SmithingTemplateItemExtender)
                .map(item -> ((SmithingTemplateItemExtender) item).dynamicTrim$getPatternAssetId())
                .filter(Objects::nonNull)
                .forEach(TEMPLATE_IDS::add);
        // you can inject here to add more items or patterns
    }

    public static EquipmentResource buildResource(TrimmableEquipment equipment, Map<Identifier, Resource> lookupMap) {
        Resource resource = getResource(equipment, lookupMap);
        if(resource == null) return null;

        JsonObject model = getModel(resource);
        if(model == null) return null;

        JsonObject textures = getTextures(model, equipment);
        if(textures == null) return null;

        String baseTexture = getBaseTexture(textures, equipment);
        if(baseTexture == null) return null;

        JsonArray overrides = getOverrides(model, equipment);
        if(overrides == null) return null;

        return new EquipmentResource(equipment, resource, model, textures, baseTexture, overrides);
    }

    private static Resource getResource(TrimmableEquipment equipment, Map<Identifier, Resource> lookupMap) {
        Resource resource = lookupMap.get(equipment.resourceId());
        if(resource == null) {
            DynamicTrimClient.LOGGER.debug("Could not find resource " + equipment.resourceId() + " for item " + equipment.id() + ", skipping");
            return null;
        }
        return resource;
    }

    private static JsonObject getModel(Resource resource) {
        try (BufferedReader reader = resource.getReader()) {
            return JsonHelper.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            DynamicTrimClient.LOGGER.debug("Could not read model file", e);
            return null;
        }
    }

    private static JsonObject getTextures(JsonObject model, TrimmableEquipment equipment) {
        if(!model.has("textures")) {
            DynamicTrimClient.LOGGER.debug("Item " + equipment.id() + "'s model does not have a textures parameter, skipping");
            return null;
        }
        return model.get("textures").getAsJsonObject();
    }

    private static String getBaseTexture(JsonObject textures, TrimmableEquipment equipment) {
        if (!textures.has("layer0")) {
            DynamicTrimClient.LOGGER.debug("Item " + equipment.id() + "'s model does not have a layer0 texture, skipping");
            return null;
        }

        return textures.get("layer0").getAsString();
    }

    private static JsonArray getOverrides(JsonObject model, TrimmableEquipment equipment) {
        if (!model.has("overrides")) {
            DynamicTrimClient.LOGGER.debug("Item " + equipment.id() + "'s model does not have an overrides parameter, skipping");
            return null;
        }
        return model.get("overrides").getAsJsonArray();
    }
}
