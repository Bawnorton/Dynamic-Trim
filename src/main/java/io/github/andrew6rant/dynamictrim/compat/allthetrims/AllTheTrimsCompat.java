package io.github.andrew6rant.dynamictrim.compat.allthetrims;

import com.bawnorton.allthetrims.client.extend.InlinedConditionExtender;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.item.trim.ArmorTrim;

public class AllTheTrimsCompat {
    public static boolean matchCustomPredicate(ModelOverrideList.InlinedCondition condition, ArmorTrim trim) {
        String assetName = trim.getMaterial().value().assetName();
        if (!(condition instanceof InlinedConditionExtender extender)) return false;

        String conditionMaterial = extender.allTheTrims$getMaterial();
        return conditionMaterial != null && conditionMaterial.equals(assetName);
    }
}
