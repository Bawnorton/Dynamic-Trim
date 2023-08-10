package io.github.andrew6rant.dynamictrim.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.andrew6rant.dynamictrim.DynamicTrimClient;
import io.github.andrew6rant.dynamictrim.compat.Compat;
import io.github.andrew6rant.dynamictrim.extend.SmithingTemplateItemExtender;
import io.github.andrew6rant.dynamictrim.json.JsonHelper;
import io.github.andrew6rant.dynamictrim.util.DebugHelper;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.item.ArmorItem;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

@Mixin(value = BakedModelManager.class, priority = 500)
public abstract class BakedModelManagerMixin {
    @ModifyExpressionValue(method = "method_45895(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceFinder;findResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"))
    private static Map<Identifier, Resource> addDynamicItemTrimModels(Map<Identifier, Resource> original) {
        Set<ArmorItem> armourItems = new HashSet<>();
        Registries.ITEM.forEach(item -> {
            if (item instanceof ArmorItem armourItem) armourItems.add(armourItem);
        });
        List<Identifier> smithingTemplatePatternIds = Registries.ITEM.stream()
                .filter(item -> item instanceof SmithingTemplateItemExtender)
                .map(item -> ((SmithingTemplateItemExtender) item).dynamicTrim$getPatternAssetId())
                .filter(Objects::nonNull)
                .toList();
        for (ArmorItem armour : armourItems) {
            Identifier armourId = Registries.ITEM.getId(armour);
            Identifier resourceId = new Identifier(armourId.getNamespace(), "models/item/" + armourId.getPath() + ".json");
            String armourType = switch (armour.getSlotType()) {
                case HEAD -> "helmet";
                case CHEST -> "chestplate";
                case LEGS -> "leggings";
                case FEET -> "boots";
                case MAINHAND, OFFHAND -> null;
            };
            if (armourType == null) {
                DynamicTrimClient.LOGGER.warn("Item " + armourId + "'s slot type is not an armour slot type, skipping");
                continue;
            }
            Resource resource = original.get(resourceId);
            if(resource == null) {
                DynamicTrimClient.LOGGER.warn("Could not find resource " + resourceId + " for item " + armourId + ", skipping");
                continue;
            }
            try (BufferedReader reader = resource.getReader()) {
                JsonObject model = JsonHelper.fromJson(reader, JsonObject.class);
                if(!model.has("textures")) {
                    DynamicTrimClient.LOGGER.warn("Item " + armourId + "'s model does not have a textures parameter, skipping");
                    continue;
                }

                JsonObject textures = model.get("textures").getAsJsonObject();
                if (!textures.has("layer0")) {
                    DynamicTrimClient.LOGGER.warn("Item " + armourId + "'s model does not have a layer0 texture, skipping");
                    continue;
                }

                String baseTexture = textures.get("layer0").getAsString();
                if (!model.has("overrides")) {
                    DynamicTrimClient.LOGGER.warn("Item " + armourId + "'s model does not have an overrides parameter, skipping");
                    continue;
                }

                JsonArray overrides = model.get("overrides").getAsJsonArray();
                JsonArray dynamicOverrides = new JsonArray();
                for(JsonElement override: overrides) {
                    if(!(override.isJsonObject())) continue;

                    JsonObject overrideJson = override.getAsJsonObject();
                    String overrideModel = overrideJson.get("model").getAsString();
                    if(overrideModel == null) {
                        DynamicTrimClient.LOGGER.warn("Item " + armourId + "'s model override does not have a model parameter, skipping");
                        continue;
                    }

                    String material = StringUtils.substringBetween(overrideModel, baseTexture + "_", "_trim");
                    if(material == null) {
                        try {
                            String modid = armourId.getNamespace();
                            if(modid.equals("frostiful")) {
                                String[] segments = overrideModel.split("/");
                                material = segments[segments.length - 1];
                            }
                        } catch (Exception e) {
                            DynamicTrimClient.LOGGER.debug("Can't parse frostiful override model " + overrideModel, e);
                        }
                        if(material == null) {
                            DynamicTrimClient.LOGGER.debug("Can't find material for item " + armourId + "'s model override: " + overrideModel + ", skipping");
                            continue;
                        }
                    }

                    JsonObject predicate = overrideJson.getAsJsonObject("predicate");

                    for(Identifier patternId: smithingTemplatePatternIds) {
                        String pattern = patternId.toString().replace(":", "-");
                        JsonObject dynamicOverride = new JsonObject();
                        dynamicOverride.addProperty("model", "%s_%s_%s_trim_%s".formatted(
                                baseTexture, DynamicTrimClient.PATTERN_ASSET_NAME, pattern, material
                        ));
                        JsonObject dynamicPredicate = predicate.deepCopy();
                        dynamicPredicate.addProperty("trim_pattern", pattern);
                        dynamicOverride.add("predicate", dynamicPredicate);
                        dynamicOverrides.add(dynamicOverride);

                        JsonObject modelOverrideJson = new JsonObject();
                        modelOverrideJson.addProperty("parent", model.get("parent").getAsString());
                        JsonObject overrideTextures = new JsonObject();
                        modelOverrideJson.add("textures", overrideTextures);
                        overrideTextures.addProperty("layer0", baseTexture);

                        int layer = 0;
                        while(true) {
                            layer++;
                            JsonElement layerElement = textures.get("layer" + layer);
                            if(layerElement != null) {
                                overrideTextures.addProperty("layer" + layer, layerElement.getAsString());
                                continue;
                            }

                            if(!material.equals("dynamic")) {
                                Identifier trimLayerTextureId = new Identifier(baseTexture).withPath("trims/items/%s_trim_%s_%s".formatted(
                                        armourType, pattern, material
                                ));
                                Identifier asMinecraft = new Identifier("minecraft", trimLayerTextureId.getPath());
                                overrideTextures.addProperty("layer" + layer, asMinecraft.toString());
                            } else {
                                for(int i = 0; i < 8; i++) {
                                    Identifier trimLayerTextureId = new Identifier(baseTexture).withPath("trims/items/%s_trim_%s_%s_%s".formatted(
                                            armourType, pattern, i, material
                                    ));
                                    Identifier asMinecraft = new Identifier("minecraft", trimLayerTextureId.getPath());
                                    overrideTextures.addProperty("layer" + (layer + i), asMinecraft.toString());
                                }
                            }
                            break;
                        }

                        Identifier overrideResourceModelId = new Identifier(armourId.getNamespace(), "models/%s_%s_%s_trim_%s.json".formatted(
                                new Identifier(baseTexture).getPath(), DynamicTrimClient.PATTERN_ASSET_NAME, pattern, material
                        ));
                        Resource overrideResourceModel = new Resource(resource.getPack(), () -> IOUtils.toInputStream(JsonHelper.toJsonString(modelOverrideJson), "UTF-8"));
                        original.put(overrideResourceModelId, overrideResourceModel);
                        DebugHelper.createDebugFile("models", "%s_%s_%s_trim_%s.json".formatted(
                                baseTexture, DynamicTrimClient.PATTERN_ASSET_NAME, pattern, material
                        ), JsonHelper.toJsonString(modelOverrideJson));
                    }
                }
                model.add("overrides", dynamicOverrides);
                resource = new Resource(resource.getPack(), () -> IOUtils.toInputStream(JsonHelper.toJsonString(model), "UTF-8"));
                DebugHelper.createDebugFile("models", armourId + ".json", JsonHelper.toJsonString(model));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            original.put(resourceId, resource);
        }
        return original;
    }
}