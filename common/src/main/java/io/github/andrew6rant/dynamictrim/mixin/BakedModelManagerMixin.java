package io.github.andrew6rant.dynamictrim.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.architectury.platform.Platform;
import io.github.andrew6rant.dynamictrim.DynamicTrimClient;
import io.github.andrew6rant.dynamictrim.json.JsonHelper;
import io.github.andrew6rant.dynamictrim.resource.EquipmentResource;
import io.github.andrew6rant.dynamictrim.resource.OverrideResource;
import io.github.andrew6rant.dynamictrim.resource.TrimModelHelper;
import io.github.andrew6rant.dynamictrim.resource.TrimmableEquipment;
import io.github.andrew6rant.dynamictrim.util.*;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(value = BakedModelManager.class, priority = 500)
public abstract class BakedModelManagerMixin {
    @SuppressWarnings("unused")
    @ModifyExpressionValue(method = "method_45895(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceFinder;findResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"))
    private static Map<Identifier, Resource> addDynamicItemTrimModels(Map<Identifier, Resource> resourceMap) {
        for (TrimmableEquipment equipment : TrimModelHelper.TRIMMABLES) {
            Identifier equipmentId = equipment.id();
            String armourType = equipment.armourType();
            if (armourType == null) {
                DynamicTrimClient.LOGGER.warn("Item " + equipmentId + "'s slot type is not an armour slot type, skipping");
                continue;
            }

            EquipmentResource equipmentResource = TrimModelHelper.buildResource(equipment, resourceMap);
            if (equipmentResource == null) {
                DynamicTrimClient.LOGGER.warn("Item " + equipmentId + "'s resource could not be built, skipping");
                continue;
            }

            resourceMap.put(equipment.resourceId(), equipmentResource.createDynamicResource());
            if(Platform.isDevelopmentEnvironment()) {
                final String modelString = equipmentResource.modelString();
                DebugHelper.createDebugFile("models", equipmentId + ".json", modelString);
            }

            equipmentResource.forEachOverride((override, material) -> {
                for(Identifier patternId: TrimModelHelper.TEMPLATE_IDS) {
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
        return resourceMap;
    }
}