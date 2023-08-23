package io.github.andrew6rant.dynamictrim.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
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
                            Codec.list(Identifier.CODEC).fieldOf("directories").forGetter(source -> source.textures),
                            Identifier.CODEC.fieldOf("palette_key").forGetter(source -> source.paletteKey),
                            Codec.unboundedMap(Codec.STRING, Identifier.CODEC)
                                    .fieldOf("permutations")
                                    .forGetter(source -> source.permutations)
                    )
                    .apply(instance, GroupPermutationsAtlasSource::new)
    );
    private static final AtlasSourceType TYPE = AtlasSourceManager.register("group_permutations", CODEC);

    public static void bootstrap() {
        // no-op
    }

    private GroupPermutationsAtlasSource(List<Identifier> directories, Identifier paletteKey, Map<String, Identifier> permutations) {
        super(new ArrayList<>(directories), paletteKey, permutations);
    }

    @Override
    public void load(ResourceManager resourceManager, SpriteRegions regions) {
        List<Identifier> combinedTextures = new ArrayList<>();
        for (Identifier dir : textures) {
            ResourceFinder resourceFinder = new ResourceFinder("textures/" + dir.getPath(), ".png");
            resourceFinder.findResources(resourceManager).forEach((identifier, resource) ->
                    combinedTextures.add(resourceFinder.toResourceId(identifier).withPrefixedPath(dir.getPath() + "/")));
        }
        textures.clear();
        textures.addAll(combinedTextures);
        super.load(resourceManager, regions);
    }

    @Override
    public AtlasSourceType getType() {
        return TYPE;
    }
}
