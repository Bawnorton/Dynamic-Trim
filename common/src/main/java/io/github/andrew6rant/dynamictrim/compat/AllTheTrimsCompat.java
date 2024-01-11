package io.github.andrew6rant.dynamictrim.compat;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.extend.InlinedConditionExtender;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllTheTrimsCompat {
    public static boolean matchCustomPredicate(ModelOverrideList.InlinedCondition[] conditions, float[] fs, ArmorTrim trim) {
        String trimMaterial = trim.getMaterial().value().assetName();
        String trimPattern = trim.getPattern().value().assetId().toString().replace(":", "-");
        for (ModelOverrideList.InlinedCondition condition : conditions) {
            if (!(condition instanceof InlinedConditionExtender attExtender && condition instanceof io.github.andrew6rant.dynamictrim.extend.InlinedConditionExtender dtExtender)) continue;

            String pattern = dtExtender.dynamicTrim$getPattern();
            if (pattern == null) continue;
            if (!pattern.equals(trimPattern)) continue;

            if (condition.threshold > 1) {
                String material = attExtender.allTheTrims$getMaterial();
                if (material == null) continue;
                if (!material.equals(trimMaterial)) continue;
            } else {
                if (fs[condition.index] < condition.threshold) continue;
            }
            return true;
        }

        return false;
    }

    public static void generateAdditionalLayers(List<Identifier> originalTextures) {
        List<Identifier> newTextures = new ArrayList<>(originalTextures.size() * 9);
        for (Identifier texture : originalTextures) {
            for (int i = 0; i < 8; i++) {
                newTextures.add(texture.withSuffixedPath("_" + i));
            }
        }
        originalTextures.addAll(newTextures);
    }

    public static Map<String, Identifier> withBlankPermutation(Map<String, Identifier> permutations) {
        Map<String, Identifier> newPermutations = new HashMap<>(permutations);
        newPermutations.put(AllTheTrims.TRIM_ASSET_NAME, new Identifier("trims/color_palettes/blank"));
        return newPermutations;
    }
}
