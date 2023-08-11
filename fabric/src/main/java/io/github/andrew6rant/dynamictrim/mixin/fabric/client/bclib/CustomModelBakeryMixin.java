package io.github.andrew6rant.dynamictrim.mixin.fabric.client.bclib;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.andrew6rant.dynamictrim.annotation.ConditionalMixin;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.util.Identifier;
import org.betterx.bclib.client.models.CustomModelBakery;
import org.betterx.bclib.interfaces.ItemModelProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CustomModelBakery.class, remap = false)
@ConditionalMixin(modid = "bclib")
public abstract class CustomModelBakeryMixin {
    @Inject(method = "addItemModel", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private void modifyItemModels(Identifier itemID, ItemModelProvider provider, CallbackInfo ci, @Local(name = "model") JsonUnbakedModel model) {
        // TODO - See AllTheTrims for possible implementation
    }
}
