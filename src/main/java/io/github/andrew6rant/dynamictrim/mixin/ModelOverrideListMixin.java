package io.github.andrew6rant.dynamictrim.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.andrew6rant.dynamictrim.compat.Compat;
import io.github.andrew6rant.dynamictrim.compat.allthetrims.AllTheTrimsCompat;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ModelOverrideList.class)
public abstract class ModelOverrideListMixin {
    @Inject(method = "method_33696", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelOverrideList$InlinedCondition;<init>(IF)V"))
    private static void capturePattern(Object2IntMap map, ModelOverride.Condition condition, CallbackInfoReturnable<ModelOverrideList.InlinedCondition> cir) {
        String pattern = ((ModelOverrideConditionExtender) condition).dynamicTrim$getPattern();
        if (pattern == null) return;
        ThreadedLocals.PATTERN.set(pattern);
    }

    @ModifyExpressionValue(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelOverrideList$BakedOverride;test([F)Z"))
    private boolean matchCustomPredicate(boolean original, BakedModel model, ItemStack stack, ClientWorld world, LivingEntity entity, int seed, @Local ModelOverrideList.BakedOverride bakedOverride, @Local float[] fs) {
        if(!(stack.getItem() instanceof ArmorItem)) return original;
        if (world == null) return original;

        Optional<ArmorTrim> optionalTrim = ArmorTrim.getTrim(world.getRegistryManager(), stack);
        if (optionalTrim.isEmpty()) return original;

        ArmorTrim trim = optionalTrim.get();
        Identifier assetId = trim.getPattern().value().assetId();
        String patternString = assetId.toString().replace(':', '-');

        ModelOverrideList.InlinedCondition[] conditions = bakedOverride.conditions;
        for (ModelOverrideList.InlinedCondition condition : conditions) {
            if (!(condition instanceof InlinedConditionExtender extender)) continue;
            if (!Compat.isAllTheTrimsLoaded() && fs[condition.index] < condition.threshold) continue;

            String conditionPattern = extender.dynamicTrim$getPattern();
            if (conditionPattern == null || !conditionPattern.equals(patternString)) continue;
            if(Compat.isAllTheTrimsLoaded() && !AllTheTrimsCompat.matchCustomPredicate(condition, trim)) continue;

            return true;
        }
        return original;
    }

    @Mixin(ModelOverrideList.InlinedCondition.class)
    abstract static class InlinedConditionMixin implements InlinedConditionExtender {
        @Unique
        private String dynamicTrim$pattern;

        @Inject(method = "<init>", at = @At("RETURN"))
        private void setPattern(CallbackInfo ci) {
            this.dynamicTrim$pattern = ThreadedLocals.PATTERN.get();
        }

        @Override
        public String dynamicTrim$getPattern() {
            return dynamicTrim$pattern;
        }
    }
}
