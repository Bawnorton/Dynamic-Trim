package io.github.andrew6rant.dynamictrim.resource;

import com.google.gson.JsonObject;
import io.github.andrew6rant.dynamictrim.json.JsonHelper;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

public record OverrideResource(Identifier modelId, JsonObject modelResourceJson) {
    public Resource modelResource(ResourcePack pack) {
        return new Resource(pack, () -> IOUtils.toInputStream(JsonHelper.toJsonString(modelResourceJson), "UTF-8"));
    }
}
