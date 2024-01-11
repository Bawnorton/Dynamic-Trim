package io.github.andrew6rant.dynamictrim.mixin.modernfix;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.andrew6rant.dynamictrim.annotation.ConditionalMixin;
import io.github.andrew6rant.dynamictrim.resource.DynamicTrimLoader;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Debug(export = true)
@Mixin(value = BakedModelManager.class, priority = 1500)
@ConditionalMixin(modid = "modernfix")
public abstract class BakedModelManagerMixin {
    @Unique
    private static final Map<Identifier, Resource> dynamicTrim$Overrides = new HashMap<>();

    @SuppressWarnings({"InvalidMemberReference", "MixinAnnotationTarget", "UnresolvedMixinReference"})
    @TargetHandler(
            mixin = "org.embeddedt.modernfix.common.mixin.perf.dynamic_resources.ModelManagerMixin",
            name = "loadSingleBlockModel"
    )
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "net/minecraft/resource/ResourceManager.getResource(Lnet/minecraft/util/Identifier;)Ljava/util/Optional;"))
    private Optional<Resource> loadSingleBlockModel(ResourceManager instance, Identifier location, Operation<Optional<Resource>> original) {
        if(dynamicTrim$Overrides.containsKey(location)) {
            return Optional.of(dynamicTrim$Overrides.remove(location));
        }

        Optional<Resource> resource = original.call(instance, location);
        if(!DynamicTrimLoader.isTrimmable(location) || resource.isEmpty()) {
            return resource;
        }

        Map<Identifier, Resource> toLoad = DynamicTrimLoader.generateResourceMapForSingleTrim(location, resource.orElseThrow());
        Resource requested = toLoad.remove(location);
        dynamicTrim$Overrides.putAll(toLoad);
        return Optional.of(requested);
    }
}
