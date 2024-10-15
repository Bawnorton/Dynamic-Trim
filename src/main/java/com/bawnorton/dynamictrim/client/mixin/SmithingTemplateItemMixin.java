package com.bawnorton.dynamictrim.client.mixin;

import com.bawnorton.dynamictrim.client.extend.SmithingTemplateItemExtender;
import net.minecraft.item.Item;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingTemplateItem.class)
public abstract class SmithingTemplateItemMixin extends Item implements SmithingTemplateItemExtender {
    @Unique
    private static final ThreadLocal<Identifier> runtimetrims$ASSET_ID_CAPTURE = new ThreadLocal<>();

    @Unique
    private Identifier runtimetrims$assetId;

    protected SmithingTemplateItemMixin(Settings settings) {
        super(settings);
    }

    @ModifyVariable(
            method = "of(Lnet/minecraft/util/Identifier;[Lnet/minecraft/resource/featuretoggle/FeatureFlag;)Lnet/minecraft/item/SmithingTemplateItem;",
            at = @At("LOAD"),
            argsOnly = true
    )
    private static Identifier capturePattern(Identifier value) {
        runtimetrims$ASSET_ID_CAPTURE.set(value);
        return value;
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void onInit(CallbackInfo ci) {
        runtimetrims$assetId = runtimetrims$ASSET_ID_CAPTURE.get();
        runtimetrims$ASSET_ID_CAPTURE.remove();
    }

    @Override
    public Identifier runtimetrims$getPatternAssetId() {
        return runtimetrims$assetId;
    }
}
