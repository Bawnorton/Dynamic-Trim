package io.github.andrew6rant.dynamictrim.mixin.invoker;

import com.mojang.serialization.Codec;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AtlasSourceManager.class)
public interface AtlasSourceManagerInvoker {
    @Invoker
    static AtlasSourceType invokeRegister(String id, Codec<? extends AtlasSource> codec) {
        throw new AssertionError();
    }
}
