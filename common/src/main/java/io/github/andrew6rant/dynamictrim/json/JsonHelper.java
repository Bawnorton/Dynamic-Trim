package io.github.andrew6rant.dynamictrim.json;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.StringReader;

public abstract class JsonHelper {
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    public static <T> T fromJson(BufferedReader reader, Class<T> clazz) {
        return GSON.fromJson(reader, clazz);
    }

    public static String toJsonString(Object object) {
        return GSON.toJson(object);
    }

    public static BufferedReader toJsonReader(Object object) {
        return new BufferedReader(new StringReader(GSON.toJson(object)));
    }
}
