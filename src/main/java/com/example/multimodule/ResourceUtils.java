package com.example.multimodule;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class ResourceUtils {

    private ResourceUtils() {}

    public static String readResource(String path) {
        try (InputStream is = ResourceUtils.class
                .getClassLoader()
                .getResourceAsStream(path)) {

            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + path);
            }

            return new String(is.readAllBytes(), StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Failed to read resource: " + path, e);
        }
    }
}
