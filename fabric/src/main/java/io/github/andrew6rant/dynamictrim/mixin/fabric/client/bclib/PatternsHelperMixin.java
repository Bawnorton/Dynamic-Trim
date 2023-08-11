package io.github.andrew6rant.dynamictrim.mixin.fabric.client.bclib;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.andrew6rant.dynamictrim.annotation.ConditionalMixin;
import net.minecraft.util.Identifier;
import org.betterx.bclib.client.models.PatternsHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(value = PatternsHelper.class, remap = false)
@ConditionalMixin(modid = "bclib")
public abstract class PatternsHelperMixin {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @ModifyReturnValue(method = "createItemGenerated", at = @At("RETURN"))
    private static Optional<String> addTrimsToArmourItems(Optional<String> original, Identifier identifier) {
        // TODO - See AllTheTrims for possible implementation
        return original;
    }
}
