package io.github.andrew6rant.dynamictrim.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.andrew6rant.dynamictrim.extend.InlinedConditionExtender;
import io.github.andrew6rant.dynamictrim.extend.ModelOverrideConditionExtender;
import io.github.andrew6rant.dynamictrim.util.ThreadedLocals;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ModelOverrideList.class)
public abstract class ModelOverrideListMixin {
    @Shadow @Final private ModelOverrideList.BakedOverride[] overrides;

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Inject(method = "method_33696", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelOverrideList$InlinedCondition;<init>(IF)V"))
    private static void capturePattern(Object2IntMap map, ModelOverride.Condition condition, CallbackInfoReturnable<ModelOverrideList.InlinedCondition> cir) {
        String pattern = ((ModelOverrideConditionExtender) condition).getPattern();
        ThreadedLocals.PATTERN.set(pattern);
    }

    @SuppressWarnings("MixinAnnotationTarget")
    @ModifyExpressionValue(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelOverrideList$BakedOverride;test([F)Z"))
    private boolean onApply(boolean original, BakedModel model, ItemStack stack, ClientWorld world, LivingEntity entity, int seed, @Local(name = "bakedOverride") ModelOverrideList.BakedOverride bakedOverride, @Local(name = "fs") float[] fs) {
        if(!(stack.getItem() instanceof ArmorItem)) return original;

        Optional<ArmorTrim> optionalTrim = ArmorTrim.getTrim(world.getRegistryManager(), stack);
        if (optionalTrim.isEmpty()) return original;

        ArmorTrim trim = optionalTrim.get();
        Identifier assetId = trim.getPattern().value().assetId();
        String patternString = assetId.toString().replace(':', '-');

        ModelOverrideList.InlinedCondition[] conditions = bakedOverride.conditions;
        for (ModelOverrideList.InlinedCondition condition : conditions) {
            if (fs[condition.index] < condition.threshold) continue;
            if (!(condition instanceof InlinedConditionExtender extender)) continue;

            String conditionPattern = extender.getPattern();
            if (conditionPattern == null || !conditionPattern.equals(patternString)) continue;

            return true;
        }
        return original;
    }

    @Mixin(ModelOverrideList.InlinedCondition.class)
    abstract static class InlinedConditionMixin implements InlinedConditionExtender {
        @Unique
        private String pattern;

        @Inject(method = "<init>", at = @At("RETURN"))
        private void onInit(CallbackInfo ci) {
            this.pattern = ThreadedLocals.PATTERN.get();
            ThreadedLocals.PATTERN.remove();
        }

        @Override
        public String getPattern() {
            return pattern;
        }
    }
}
