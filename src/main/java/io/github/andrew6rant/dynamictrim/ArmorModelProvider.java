package io.github.andrew6rant.dynamictrim;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ArmorModelProvider implements ModelResourceProvider {
    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {
        if(resourceId.toString().contains("helmet")) {
            return new DynamicArmorModel("helmet");
        } else if(resourceId.toString().contains("chestplate")) {
            return new DynamicArmorModel("chestplate");
        } else if(resourceId.toString().contains("leggings")) {
            return new DynamicArmorModel("leggings");
        } else if(resourceId.toString().contains("boots")) {
            return new DynamicArmorModel("boots");
        } else {
            return null;
        }
    }
}
