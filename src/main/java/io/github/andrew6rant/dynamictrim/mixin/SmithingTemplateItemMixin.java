package io.github.andrew6rant.dynamictrim.mixin;

import io.github.andrew6rant.dynamictrim.extend.SmithingTemplateItemExtender;
import io.github.andrew6rant.dynamictrim.util.ThreadedLocals;
import net.minecraft.item.Item;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SmithingTemplateItem.class)
public abstract class SmithingTemplateItemMixin extends Item implements SmithingTemplateItemExtender {
    protected SmithingTemplateItemMixin(Settings settings) {
        super(settings);
    }

    @Shadow public abstract String getTranslationKey();

    @Shadow @Final private Text titleText;
    @Unique
    private Identifier assetId;

    @ModifyVariable(method = "of(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/item/SmithingTemplateItem;", at = @At("LOAD"), argsOnly = true)
    private static RegistryKey<ArmorTrimPattern> capturePattern(RegistryKey<ArmorTrimPattern> value) {
        ThreadedLocals.ASSET_ID.set(value.getValue());
        return value;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(Text appliesToText, Text ingredientsText, Text titleText, Text baseSlotDescriptionText, Text additionsSlotDescriptionText, List emptyBaseSlotTextures, List emptyAdditionsSlotTextures, CallbackInfo ci) {
        assetId = ThreadedLocals.ASSET_ID.get();
        ThreadedLocals.ASSET_ID.remove();
    }

    @Override
    public Identifier dynamicTrim$getPatternAssetId() {
        return assetId;
    }
}
