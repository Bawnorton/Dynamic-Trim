package io.github.andrew6rant.dynamictrim.mixin.accessor;

import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(PalettedPermutationsAtlasSource.class)
public interface PalettedPermutationsAtlasSourceAccessor {
    @Accessor
    List<Identifier> getTextures();

    @Accessor
    Identifier getPaletteKey();

    @Accessor
    Map<String, Identifier> getPermutations();
}
