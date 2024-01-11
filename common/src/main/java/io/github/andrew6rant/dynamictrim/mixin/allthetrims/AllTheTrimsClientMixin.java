package io.github.andrew6rant.dynamictrim.mixin.allthetrims;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import io.github.andrew6rant.dynamictrim.util.mixin.annotation.ConditionalMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(AllTheTrimsClient.class)
@ConditionalMixin(modid = "allthetrims")
public abstract class AllTheTrimsClientMixin {
    @ModifyArg(method = "lambda$init$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I"), index = 0)
    private static int reducePaletteOffset(int tintIndex) {
        return 6 - tintIndex;
    }
}
