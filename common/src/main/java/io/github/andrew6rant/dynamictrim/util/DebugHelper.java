package io.github.andrew6rant.dynamictrim.util;

import dev.architectury.platform.Platform;
import io.github.andrew6rant.dynamictrim.DynamicTrimClient;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public abstract class DebugHelper {
    public static void createDebugFile(String directory, String filename, String content) {
        if(!Platform.isDevelopmentEnvironment()) return;
        try {
            Path gameDir = Platform.getGameFolder();
            File debugDir = gameDir.resolve("dynamic-trim-debug").resolve(directory).toFile();
            debugDir.mkdirs();
            File debugFile = debugDir.toPath().resolve(filename.replace("/", "_").replace(":", "_")).toFile();
            debugFile.createNewFile();

            Writer writer = new FileWriter(debugFile);
            IOUtils.copy(IOUtils.toInputStream(content, "UTF-8"), writer, "UTF-8");
            writer.close();
        } catch (IOException e) {
            DynamicTrimClient.LOGGER.error("Failed to create debug file: " + filename, e);
        }
    }
}
