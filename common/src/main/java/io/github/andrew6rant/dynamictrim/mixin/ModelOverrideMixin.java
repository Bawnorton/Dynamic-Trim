package io.github.andrew6rant.dynamictrim.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.andrew6rant.dynamictrim.extend.ModelOverrideConditionExtender;
import io.github.andrew6rant.dynamictrim.util.ThreadedLocals;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelOverride.class)
public abstract class ModelOverrideMixin {
    @Mixin(ModelOverride.Condition.class)
    abstract static class ConditionMixin implements ModelOverrideConditionExtender {
        @Unique
        private String dynamicTrim$pattern;

        @Inject(method = "<init>", at = @At("RETURN"))
        private void onInit(Identifier type, float threshold, CallbackInfo ci) {
            this.dynamicTrim$pattern = ThreadedLocals.PATTERN.get();
            ThreadedLocals.PATTERN.remove();
        }

        @Override
        public String dynamicTrim$getPattern() {
            return dynamicTrim$pattern;
        }
    }

    @Mixin(ModelOverride.Deserializer.class)
    abstract static class DeserializerMixin {
        @SuppressWarnings("unusued")
        @WrapOperation(method = "deserializeMinPropertyValues", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/JsonHelper;asFloat(Lcom/google/gson/JsonElement;Ljava/lang/String;)F"))
        private static float capturePattern(JsonElement value, String name, Operation<Float> original) {
            try {
                return original.call(value, name);
            } catch (JsonSyntaxException e) {
                if(!name.equals("trim_pattern")) throw e;
                if(!value.isJsonPrimitive()) throw e;

                JsonPrimitive primitive = value.getAsJsonPrimitive();
                String pattern = primitive.getAsString();
                ThreadedLocals.PATTERN.set(pattern);
                return Float.MAX_VALUE;
            }
        }
    }
}
