package com.bawnorton.dynamictrim.client.mixin;

import com.bawnorton.dynamictrim.client.adapters.DynamicTrimsTrimModelLoaderAdapter;
import com.bawnorton.dynamictrim.client.extend.ArmorTrimPatternExtender;
import com.bawnorton.runtimetrims.RuntimeTrims;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Map;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(
            method = "joinWorld",
            at = @At("TAIL")
    )
    private void initPatternIndexes(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason, CallbackInfo ci) {
        Map<Identifier, Float> indexes = DynamicTrimsTrimModelLoaderAdapter.TEMPLATE_PATTERN_INDEX_SUPPLIER.get();
        Registry<ArmorTrimPattern> registry = world.getRegistryManager().get(RegistryKeys.TRIM_PATTERN);
        indexes.forEach((id, index) -> {
            ArmorTrimPattern pattern = registry.get(id);
            if(pattern == null) {
                RuntimeTrims.LOGGER.warn("No pattern found for {}", id);
                return;
            }
            ((ArmorTrimPatternExtender) (Object) pattern).runtimetrims$setItemModelIndex(index);
        });
    }
}
