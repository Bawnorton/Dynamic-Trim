package io.github.andrew6rant.dynamictrim.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.andrew6rant.dynamictrim.mixin.accessor.PalettedPermutationsAtlasSourceAccessor;
import io.github.andrew6rant.dynamictrim.mixin.invoker.AtlasSourceManagerInvoker;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A {@link PalettedPermutationsAtlasSource} that combines textures from multiple directories. Since Dynamic Trim stores all textures in a single namespace, this can be used to avoid writing walls of json.
 */
// Also improves mod compatibility. If Dynamic trim is not installed, atlas loader will ignore all "group_permutations" sources, avoiding creating unnecessary textures.
public class GroupPermutationsAtlasSource extends PalettedPermutationsAtlasSource {
    public static final Codec<GroupPermutationsAtlasSource> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.list(Identifier.CODEC).fieldOf("directories").forGetter(source -> ((PalettedPermutationsAtlasSourceAccessor) source).getTextures()),
                            Identifier.CODEC.fieldOf("palette_key").forGetter(source -> ((PalettedPermutationsAtlasSourceAccessor) source).getPaletteKey()),
                            Codec.unboundedMap(Codec.STRING, Identifier.CODEC)
                                    .fieldOf("permutations")
                                    .forGetter(source -> ((PalettedPermutationsAtlasSourceAccessor) source).getPermutations())
                    )
                    .apply(instance, GroupPermutationsAtlasSource::new)
    );
    private static final AtlasSourceType TYPE = AtlasSourceManagerInvoker.invokeRegister("group_permutations", CODEC);

    public static void bootstrap() {
        // no-op
    }

    private GroupPermutationsAtlasSource(List<Identifier> directories, Identifier paletteKey, Map<String, Identifier> permutations) {
        super(new ArrayList<>(directories), paletteKey, permutations);
    }

    @Override
    public void load(ResourceManager resourceManager, SpriteRegions regions) {
        List<Identifier> combinedTextures = new ArrayList<>();
        for (Identifier dir : ((PalettedPermutationsAtlasSourceAccessor) this).getTextures()) {
            ResourceFinder resourceFinder = new ResourceFinder("textures/" + dir.getPath(), ".png");
            resourceFinder.findResources(resourceManager).forEach((identifier, resource) ->
                    combinedTextures.add(resourceFinder.toResourceId(identifier).withPrefixedPath(dir.getPath() + "/")));
        }
        ((PalettedPermutationsAtlasSourceAccessor) this).getTextures().clear();
        ((PalettedPermutationsAtlasSourceAccessor) this).getTextures().addAll(combinedTextures);
        super.load(resourceManager, regions);
    }

    @Override
    public AtlasSourceType getType() {
        return TYPE;
    }
}
