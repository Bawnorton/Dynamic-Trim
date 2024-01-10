package io.github.andrew6rant.dynamictrim.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.andrew6rant.dynamictrim.json.BlocksAtlas;
import io.github.andrew6rant.dynamictrim.json.JsonHelper;
import net.minecraft.client.texture.atlas.AtlasLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.io.BufferedReader;
import java.util.List;

@Mixin(AtlasLoader.class)
public abstract class AtlasLoaderMixin {
    @Unique


    @ModifyExpressionValue(method = "of", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/Resource;getReader()Ljava/io/BufferedReader;"))
    private static BufferedReader appendGroupPermutationsToAtlasSources(BufferedReader original, ResourceManager manager, Identifier id) {
        if (!id.equals(new Identifier("blocks"))) return original;

        BlocksAtlas atlas = JsonHelper.fromJson(original, BlocksAtlas.class);
        atlas.getPalettedPermutationsSource("trims/color_palettes/trim_palette").ifPresent(source -> atlas.addSource(
                source.copy()
                      .withType("group_permutations")
                      .withDirectories(List.of(
                              "trims/items/helmet",
                              "trims/items/chestplate",
                              "trims/items/leggings",
                              "trims/items/boots"
                      ))
                      .withTextures(null)
        ));
        return atlas.toReader();
    }
}
