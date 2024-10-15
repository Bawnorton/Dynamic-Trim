package com.bawnorton.dynamictrim.client.adapters;

import com.bawnorton.dynamictrim.DynamicTrim;
import com.bawnorton.runtimetrims.RuntimeTrims;
import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.bawnorton.runtimetrims.client.model.item.JsonParser;
import com.bawnorton.runtimetrims.client.model.item.TrimmableResource;
import com.bawnorton.runtimetrims.client.model.item.adapter.DefaultTrimModelLoaderAdapter;
import com.bawnorton.runtimetrims.client.model.item.json.ModelOverride;
import com.bawnorton.runtimetrims.client.model.item.json.TrimmableItemModel;
import com.bawnorton.runtimetrims.util.Memoizer;
import com.bawnorton.dynamictrim.client.extend.SmithingTemplateItemExtender;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DynamicTrimsTrimModelLoaderAdapter extends DefaultTrimModelLoaderAdapter {
    public static final Supplier<Map<Identifier, Float>> TEMPLATE_PATTERN_INDEX_SUPPLIER = Memoizer.memoize(() -> {
        Set<Identifier> ids = Registries.ITEM.stream()
                .filter(item -> item instanceof SmithingTemplateItemExtender)
                .map(item -> ((SmithingTemplateItemExtender) item).runtimetrims$getPatternAssetId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        int increment = 10000 / ids.size();
        Map<Identifier, Float> patternIndexes = new HashMap<>(ids.size());
        for (Identifier id : ids) {
            patternIndexes.put(id, increment / 10000f);
            increment += 10000 / ids.size();
        }
        return patternIndexes;
    });

    private Identifier generatingModelId;

    @Override
    public Integer getLayerCount(Item item) {
        TrimInfo info = TrimInfo.fromModelId(item, generatingModelId);
        if(info == null) return super.getLayerCount(item);

        Identifier patternTextureId = info.getPatternTextureId(getEquipmentType((Equipment) item));
        int maxLayerCount = RuntimeTrimsClient.getLayerData().getMaxSupportedLayer(patternTextureId);
        if(maxLayerCount == 0) return info.isDynamic() ? super.getLayerCount(item) : 1;

        return maxLayerCount;
    }

    @Override
    public String getLayerName(Item item, int layerIndex) {
        TrimInfo info = TrimInfo.fromModelId(item, generatingModelId);
        if (info == null) return super.getLayerName(item, layerIndex);

        Identifier patternTextureId = info.getPatternTextureId(getEquipmentType((Equipment) item));
        if(RuntimeTrimsClient.getLayerData().getMaxSupportedLayer(patternTextureId) == 0) {
            if(info.isDynamic()) {
                return super.getLayerName(item, layerIndex);
            } else {
                return "minecraft:trims/items/%s_trim_%s".formatted(
                        getEquipmentType((Equipment) item),
                        info.trimType()
                );
            }
        }

        if (info.isDynamic()) {
            return "minecraft:trims/items/%s/%s_%s_%s".formatted(
                    getEquipmentType((Equipment) item),
                    info.trimPattern(),
                    layerIndex,
                    info.trimType()
            );
        } else {
            return "minecraft:trims/items/%s/%s_%s".formatted(
                    getEquipmentType((Equipment) item),
                    info.trimPattern(),
                    info.trimType()
            );
        }
    }

    @Override
    public Map<Identifier, TrimmableItemModel> supplyOverrides(JsonParser jsonParser, TrimmableItemModel itemModel, TrimmableResource resource, BiFunction<TrimmableItemModel, TrimmableResource, TrimmableItemModel> overrideCreator) {
        Map<Identifier, TrimmableItemModel> overrides = new HashMap<>(super.supplyOverrides(jsonParser, itemModel, resource, overrideCreator));
        List<Pair<Identifier, ModelOverride>> trimPatternModelOverrides = new ArrayList<>();
        itemModel.overrides.forEach(modelOverride -> {
            for (Map.Entry<Identifier, Float> pattern : TEMPLATE_PATTERN_INDEX_SUPPLIER.get().entrySet()) {
                Identifier id = pattern.getKey();
                String modelType = id.toString().replace(":", "-");
                String overrideModelId = modelOverride.model + "-" + modelType;
                Float index = pattern.getValue();
                JsonObject exisitngPredicate = modelOverride.predicate.deepCopy();
                exisitngPredicate.addProperty(DynamicTrim.TRIM_PATTERN.toString(), index);
                trimPatternModelOverrides.add(Pair.of(
                        Identifier.of(overrideModelId),
                        ModelOverride.builder()
                                .withPredicate(exisitngPredicate)
                                .withModel(overrideModelId)
                                .build()
                ));
            }
        });
        TrimmableItemModel base = itemModel.copy();
        trimPatternModelOverrides.forEach((pair) -> {
            Identifier modelId = pair.first();
            ModelOverride override = pair.second();
            itemModel.addOverride(override);
            generatingModelId = modelId;
            overrides.put(modelId, overrideCreator.apply(base, resource));
            generatingModelId = null;
        });
        return overrides;
    }

    private record TrimInfo(String trimType, String trimPattern) {
        private static final Function<String, Pattern> TRIM_INFO_PATTERN_FUNCTION = Memoizer.memoize(itemId -> Pattern.compile("[^:]+:item/" + itemId + "_(?<trimType>.+)(?=_trim)_trim-(?<trimPattern>.*)"));

        public static TrimInfo fromModelId(Item item, Identifier modelId) {
            if (modelId == null) return null;

            Pattern pattern = TRIM_INFO_PATTERN_FUNCTION.apply(Registries.ITEM.getId(item).getPath());
            Matcher matcher = pattern.matcher(modelId.toString());
            if (!matcher.find()) {
                RuntimeTrims.LOGGER.debug("Couldn't find match trim info for {}", modelId);
                return null;
            }
            String trimType = matcher.group("trimType");
            String trimPattern = matcher.group("trimPattern");
            return new TrimInfo(trimType, trimPattern);
        }

        public boolean isDynamic() {
            return trimType.equals(RuntimeTrims.DYNAMIC);
        }

        public Identifier getPatternTextureId(String equipmentType) {
            return Identifier.ofVanilla("textures/trims/items/%s/%s.png".formatted(equipmentType, trimPattern));
        }
    }
}
