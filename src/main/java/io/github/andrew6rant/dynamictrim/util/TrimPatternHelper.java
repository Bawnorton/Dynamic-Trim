package io.github.andrew6rant.dynamictrim.util;

import io.github.andrew6rant.dynamictrim.extend.SmithingTemplateItemExtender;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public abstract class TrimPatternHelper {
    public static void loopTrimPaterns(Consumer<Identifier> patternIdConsumer) {
        for(Item item: Registries.ITEM) {
            if (!(item instanceof SmithingTemplateItemExtender extender)) continue;

            Identifier assetId = extender.getPatternAssetId();
            if(assetId == null) continue;

            patternIdConsumer.accept(assetId);
        }
    }
}
