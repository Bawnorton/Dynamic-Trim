package com.bawnorton.dynamictrim.client.mixin;

import com.bawnorton.dynamictrim.DynamicTrim;
import com.bawnorton.dynamictrim.client.extend.ArmorTrimPatternExtender;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModelPredicateProviderRegistry.class)
public abstract class ModelPredicateProviderRegistryMixin {
    @Shadow
    private static ClampedModelPredicateProvider register(Identifier id, ClampedModelPredicateProvider provider) {
        throw new AssertionError();
    }

    static {
        register(DynamicTrim.TRIM_PATTERN, (stack, world, entity, seed) -> {
            ArmorTrim trim = stack.get(DataComponentTypes.TRIM);
            if(trim == null) return Float.NEGATIVE_INFINITY;

            return ((ArmorTrimPatternExtender) (Object) trim.getPattern().value()).runtimetrims$itemModelIndex();
        });
    }
}
