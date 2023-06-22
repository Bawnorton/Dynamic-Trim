package io.github.andrew6rant.dynamictrim.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;

public abstract class JsonHelper {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T fromJson(BufferedReader reader, Class<T> clazz) {
        return GSON.fromJson(reader, clazz);
    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }
}
