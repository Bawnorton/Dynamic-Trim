package io.github.andrew6rant.dynamictrim.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.andrew6rant.dynamictrim.resource.DynamicTrimLoader;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(value = BakedModelManager.class, priority = 500)
public abstract class BakedModelManagerMixin {
    @SuppressWarnings("unused")
    @ModifyExpressionValue(method = "method_45895(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceFinder;findResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"))
    private static Map<Identifier, Resource> addDynamicItemTrimModels(Map<Identifier, Resource> resourceMap) {
        DynamicTrimLoader.loadDynamicTrims(resourceMap);
        return resourceMap;
    }
}