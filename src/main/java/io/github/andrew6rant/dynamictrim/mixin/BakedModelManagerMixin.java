package io.github.andrew6rant.dynamictrim.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.andrew6rant.dynamictrim.DynamicTrimClient;
import io.github.andrew6rant.dynamictrim.json.JsonHelper;
import io.github.andrew6rant.dynamictrim.util.DebugHelper;
import io.github.andrew6rant.dynamictrim.util.TrimPatternHelper;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(BakedModelManager.class)
public abstract class BakedModelManagerMixin {
    @ModifyExpressionValue(method = "method_45895(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceFinder;findResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"))
    private static Map<Identifier, Resource> addTrimModels(Map<Identifier, Resource> original) {
        Set<ArmorItem> armourItems = new HashSet<>();
        Registries.ITEM.forEach(item -> {
            if (item instanceof ArmorItem armourItem) armourItems.add(armourItem);
        });
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
                JsonObject modelJson = JsonHelper.fromJson(reader, JsonObject.class);
                if(!modelJson.has("overrides")) {
                    modelJson.add("overrides", new JsonArray());
                }
                JsonArray overrides = modelJson.getAsJsonArray("overrides");
                JsonArray newOverrides = new JsonArray();
                for(JsonElement element: overrides) {
                    if(!element.isJsonObject()) continue;

                    JsonObject override = element.getAsJsonObject();
                    String model = override.get("model").getAsString();
                    JsonObject predicate = override.getAsJsonObject("predicate");
                    float trimType = predicate.get("trim_type").getAsFloat();

                    int lastUnderscore = model.lastIndexOf('_');
                    int secondLastUnderscore = model.lastIndexOf('_', lastUnderscore - 1);
                    if(lastUnderscore == -1 || secondLastUnderscore == -1) continue;

                    String material = model.substring(secondLastUnderscore + 1, lastUnderscore);
                    String preMaterial = model.substring(0, secondLastUnderscore);
                    if(material.equals("darker")) {
                        int thirdLastUnderscore = model.lastIndexOf('_', secondLastUnderscore - 1);
                        if(thirdLastUnderscore == -1) continue;
                        material = model.substring(thirdLastUnderscore + 1, secondLastUnderscore);
                        preMaterial = model.substring(0, thirdLastUnderscore);
                    }

                    final Resource finalResource = resource;
                    final String finalMaterial = material;
                    final String finalPreMaterial = preMaterial;
                    TrimPatternHelper.loopTrimPaterns(patternId -> {
                        String pattern = patternId.toString().replace(':', '-');
                        JsonObject newOverride = new JsonObject();
                        newOverride.addProperty("model", finalPreMaterial + "_dynamic-trim_" + pattern + "_trim_" + finalMaterial);
                        JsonObject newPredicate = new JsonObject();
                        newPredicate.addProperty("trim_type", trimType);
                        newPredicate.addProperty("dynamic-trim-pattern", pattern);
                        newOverride.add("predicate", newPredicate);
                        newOverrides.add(newOverride);

                        String overrideResourceString;
                        if(armour instanceof DyeableArmorItem) {
                            overrideResourceString = """
                                    {
                                       "parent": "minecraft:item/generated",
                                       "textures": {
                                         "layer0": "%s:item/%s",
                                         "layer1": "minecraft:item/%s_overlay",
                                         "layer2": "minecraft:trims/items/%s_trim_%s_%s"
                                       }
                                    }
                                    """.formatted(armourId.getNamespace(), armourId.getPath(), armourId.getPath(), armourType, pattern, finalMaterial);
                        } else {
                            overrideResourceString = """
                                    {
                                       "parent": "minecraft:item/generated",
                                       "textures": {
                                         "layer0": "%s:item/%s",
                                         "layer1": "minecraft:trims/items/%s_trim_%s_%s"
                                       }
                                    }
                                    """.formatted(armourId.getNamespace(), armourId.getPath(), armourType, pattern, finalMaterial);
                        }
                        Identifier overrideResourceModelId = new Identifier(armourId.getNamespace(), "models/item/" + armourId.getPath() + "_dynamic-trim_" + pattern + "_trim_" + finalMaterial + ".json");
                        Resource overrideResource = new Resource(finalResource.getPack(), () -> IOUtils.toInputStream(overrideResourceString, "UTF-8"));
                        original.put(overrideResourceModelId, overrideResource);
                        DebugHelper.createDebugFile("models", armourId + "_dynamic-trim_" + pattern + "_trim_" + finalMaterial + ".json", overrideResourceString);
                    });
                }
                modelJson.add("overrides", newOverrides);

                resource = new Resource(resource.getPack(), () -> IOUtils.toInputStream(JsonHelper.toJson(modelJson), "UTF-8"));

                DebugHelper.createDebugFile("models", armourId + ".json", JsonHelper.toJson(modelJson));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            original.put(resourceId, resource);
        }
        return original;
    }
}