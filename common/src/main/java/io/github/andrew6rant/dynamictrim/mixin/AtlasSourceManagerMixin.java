package io.github.andrew6rant.dynamictrim.mixin;

import com.mojang.serialization.Codec;
import io.github.andrew6rant.dynamictrim.resource.GroupPermutationsAtlasSource;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.andrew6rant.dynamictrim.resource.GroupPermutationsAtlasSource.CODEC;

@Mixin(AtlasSourceManager.class)
public abstract class AtlasSourceManagerMixin {
    @Shadow private static AtlasSourceType register(String id, Codec<? extends AtlasSource> codec) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lnet/minecraft/client/texture/atlas/AtlasSourceManager;CODEC:Lcom/mojang/serialization/Codec;"))
    private static void registerGroupPermutation(CallbackInfo ci) {
        GroupPermutationsAtlasSource.TYPE = register("group_permutations", CODEC);
    }
}
