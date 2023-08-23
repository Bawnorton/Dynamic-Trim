package io.github.andrew6rant.dynamictrim.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.andrew6rant.dynamictrim.DynamicTrimClient;
import io.github.andrew6rant.dynamictrim.extend.SmithingTemplateItemExtender;
import io.github.andrew6rant.dynamictrim.json.JsonHelper;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.util.*;

public class TrimModelHelper {
    public static final List<Identifier> TEMPLATE_IDS = new ArrayList<>();

    static {
        Registries.ITEM.stream()
                .filter(item -> item instanceof SmithingTemplateItemExtender)
                .map(item -> ((SmithingTemplateItemExtender) item).dynamicTrim$getPatternAssetId())
                .filter(Objects::nonNull)
                .forEach(TEMPLATE_IDS::add);
    }

    public static TrimmableResource buildResource(TrimmableItem item, Map<Identifier, Resource> lookupMap) {
        Resource resource = getResource(item, lookupMap);
        if(resource == null) return null;

        JsonObject model = getModel(resource);
        if(model == null) return null;

        JsonObject textures = getTextures(model, item);
        if(textures == null) return null;

        String baseTexture = getBaseTexture(textures, item);
        if(baseTexture == null) return null;

        JsonArray overrides = getOverrides(model, item);
        if(overrides == null) return null;

        return new TrimmableResource(item, resource, model, textures, baseTexture, overrides);
    }

    private static Resource getResource(TrimmableItem item, Map<Identifier, Resource> lookupMap) {
        Resource resource = lookupMap.get(item.resourceId());
        if(resource == null) {
            DynamicTrimClient.LOGGER.debug("Could not find resource " + item.resourceId() + " for item " + item.model() + ", skipping");
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

    private static JsonObject getTextures(JsonObject model, TrimmableItem item) {
        if(!model.has("textures")) {
            DynamicTrimClient.LOGGER.debug("Item " + item.model() + "'s model does not have a textures parameter, skipping");
            return null;
        }
        return model.get("textures").getAsJsonObject();
    }

    private static String getBaseTexture(JsonObject textures, TrimmableItem item) {
        if (!textures.has("layer0")) {
            DynamicTrimClient.LOGGER.debug("Item " + item.model() + "'s model does not have a layer0 texture, skipping");
            return null;
        }

        return textures.get("layer0").getAsString();
    }

    private static JsonArray getOverrides(JsonObject model, TrimmableItem item) {
        if (!model.has("overrides")) {
            DynamicTrimClient.LOGGER.debug("Item " + item.model() + "'s model does not have an overrides parameter, skipping");
            return null;
        }
        return model.get("overrides").getAsJsonArray();
    }
}
