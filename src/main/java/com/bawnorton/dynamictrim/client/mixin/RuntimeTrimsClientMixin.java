package com.bawnorton.dynamictrim.client.mixin;

import com.bawnorton.dynamictrim.client.adapters.DynamicTrimsTrimModelLoaderAdapter;
import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.bawnorton.runtimetrims.client.model.item.ItemTrimModelLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RuntimeTrimsClient.class)
public abstract class RuntimeTrimsClientMixin {
    @Shadow @Final private static ItemTrimModelLoader itemModelLoader;

    @Inject(
            method = "init",
            at = @At("TAIL"),
            remap = false
    )
    private static void overwriteDefaultModelLoaderAdapater(CallbackInfo ci) {
        itemModelLoader.setDefaultAdapter(new DynamicTrimsTrimModelLoaderAdapter());
    }
}
