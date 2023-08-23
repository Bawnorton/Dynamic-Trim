package io.github.andrew6rant.dynamictrim.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.andrew6rant.dynamictrim.DynamicTrimClient;
import io.github.andrew6rant.dynamictrim.json.JsonHelper;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiConsumer;

public record EquipmentResource(TrimmableEquipment equipment, Resource resource, JsonObject model, JsonObject textures, String baseTexture, JsonArray overrides) {
    public String modelString() {
        return JsonHelper.toJsonString(model);
    }

    public void forEachOverride(BiConsumer<JsonObject, String> overrideMaterialConsumer) {
        for(JsonElement override: overrides) {
            if (!(override.isJsonObject())) continue;

            JsonObject overrideJson = override.getAsJsonObject();
            String overrideModel = overrideJson.get("model").getAsString();
            if (overrideModel == null) {
                DynamicTrimClient.LOGGER.debug("Item " + equipment.id() + "'s model override does not have a model parameter, skipping");
                continue;
            }

            String material = getMaterial(overrideModel);
            if (material == null) continue;

            overrideMaterialConsumer.accept(overrideJson, material);
        }
    }


    private String getMaterial(String overrideModel) {
        String material = StringUtils.substringBetween(overrideModel, baseTexture + "_", "_trim");
        if(material == null) {
            try {
                String modid = equipment.id().getNamespace();
                if(modid.equals("frostiful")) {
                    String[] segments = overrideModel.split("/");
                    material = segments[segments.length - 1];
                }
            } catch (Exception e) {
                DynamicTrimClient.LOGGER.debug("Can't parse frostiful override model " + overrideModel, e);
            }
            if(material == null) {
                DynamicTrimClient.LOGGER.debug("Can't find material for item " + equipment.id() + "'s model override: " + overrideModel + ", skipping");
                return null;
            }
        }
        return material;
    }

    public Resource createDynamicResource() {
        JsonArray dynamicOverrides = new JsonArray();
        forEachOverride((override, material) -> {
            JsonObject predicate = override.getAsJsonObject("predicate");

            for (Identifier patternId : TrimModelHelper.TEMPLATE_IDS) {
                String pattern = patternId.toString().replace(":", "-");
                JsonObject dynamicOverride = new JsonObject();
                dynamicOverride.addProperty("model", "%s:%s/trims/%s/%s_trim".formatted(
                        baseTextureId().getNamespace(), baseTextureId().getPath(), pattern, material
                ));
                JsonObject dynamicPredicate = predicate.deepCopy();
                dynamicPredicate.addProperty("trim_pattern", pattern);
                dynamicOverride.add("predicate", dynamicPredicate);
                dynamicOverrides.add(dynamicOverride);
            }
        });

        model.add("overrides", dynamicOverrides);
        return new Resource(resource.getPack(), () -> IOUtils.toInputStream(modelString(), "UTF-8"));
    }

    public OverrideResource createOverrideResource(Identifier patternId, String material) {
        String pattern = patternId.toString().replace(":", "-");
        JsonObject modelOverrideJson = new JsonObject();
        modelOverrideJson.addProperty("parent", model().get("parent").getAsString());
        JsonObject overrideTextures = new JsonObject();
        modelOverrideJson.add("textures", overrideTextures);
        overrideTextures.addProperty("layer0", baseTexture());

        int layer = 0;
        while(true) {
            layer++;
            JsonElement layerElement = textures().get("layer" + layer);
            if(layerElement != null) {
                overrideTextures.addProperty("layer" + layer, layerElement.getAsString());
                continue;
            }

            if(!material.equals("dynamic")) {
                Identifier trimLayerTextureId = new Identifier(baseTexture()).withPath("trims/items/%s/%s_%s".formatted(
                        equipment.armourType(), pattern, material
                ));
                Identifier asMinecraft = new Identifier("minecraft", trimLayerTextureId.getPath());
                overrideTextures.addProperty("layer" + layer, asMinecraft.toString());
            } else {
                for(int i = 0; i < 8; i++) {
                    Identifier trimLayerTextureId = new Identifier(baseTexture()).withPath("trims/items/%s/%s_%s_%s".formatted(
                            equipment.armourType(),
                            pattern,
                            i,
                            material
                    ));
                    Identifier asMinecraft = new Identifier("minecraft", trimLayerTextureId.getPath());
                    overrideTextures.addProperty("layer" + (layer + i), asMinecraft.toString());
                }
            }
            break;
        }

        Identifier overrideResourceModelId = new Identifier(equipment.id().getNamespace(), "models/%s/trims/%s/%s_trim.json".formatted(
                new Identifier(baseTexture()).getPath(), pattern, material
        ));
        return new OverrideResource(overrideResourceModelId, modelOverrideJson);
    }

    public Identifier baseTextureId() {
        return new Identifier(baseTexture());
    }
}
