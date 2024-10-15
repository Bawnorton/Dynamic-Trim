package com.bawnorton.dynamictrim.client.mixin;

import com.bawnorton.dynamictrim.client.extend.ArmorTrimPatternExtender;
import net.minecraft.item.trim.ArmorTrimPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ArmorTrimPattern.class)
public abstract class ArmorTrimPatternMixin implements ArmorTrimPatternExtender {
    @Unique
    private float runtimetrims$itemModelIndex = Float.NEGATIVE_INFINITY;

    @Override
    public float runtimetrims$itemModelIndex() {
        return runtimetrims$itemModelIndex;
    }

    @Override
    public void runtimetrims$setItemModelIndex(float itemModelIndex) {
        this.runtimetrims$itemModelIndex = itemModelIndex;
    }
}
