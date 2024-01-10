package io.github.andrew6rant.dynamictrim.json;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BlocksAtlas {
    public List<Source> sources;

    public Optional<Source> getPalettedPermutationsSource(String paletteKey) {
        for(Source source : sources) {
            if(source.type.equals("paletted_permutations") && source.paletteKey.equals(paletteKey)) {
                return Optional.of(source);
            }
        }
        return Optional.empty();
    }

    public void addSource(Source source) {
        sources.add(source);
    }

    public BufferedReader toReader() {
        return JsonHelper.toJsonReader(this);
    }

    public static class Source {
        public String type;
        public String source;
        public String prefix;
        public String resource;
        public List<String> textures;
        public List<String> directories;
        public String paletteKey;
        public Map<String, String> permutations;

        public Source withType(String type) {
            this.type = type;
            return this;
        }

        public Source withSource(String source) {
            this.source = source;
            return this;
        }

        public Source withPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Source withResource(String resource) {
            this.resource = resource;
            return this;
        }

        public Source withTextures(List<String> textures) {
            this.textures = textures;
            return this;
        }

        public Source withDirectories(List<String> directories) {
            this.directories = directories;
            return this;
        }

        public Source withPaletteKey(String paletteKey) {
            this.paletteKey = paletteKey;
            return this;
        }

        public Source withPermutations(Map<String, String> permutations) {
            this.permutations = permutations;
            return this;
        }

        public Source copy() {
            return new Source()
                .withType(type)
                .withSource(source)
                .withPrefix(prefix)
                .withResource(resource)
                .withTextures(textures)
                .withDirectories(directories)
                .withPaletteKey(paletteKey)
                .withPermutations(permutations);
        }
    }
}

