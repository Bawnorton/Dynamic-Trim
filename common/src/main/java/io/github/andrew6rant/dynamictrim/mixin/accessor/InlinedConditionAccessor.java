package io.github.andrew6rant.dynamictrim.mixin.accessor;

import net.minecraft.client.render.model.json.ModelOverrideList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelOverrideList.InlinedCondition.class)
public interface InlinedConditionAccessor {
    @Accessor
    int getIndex();

    @Accessor
    float getThreshold();
}
