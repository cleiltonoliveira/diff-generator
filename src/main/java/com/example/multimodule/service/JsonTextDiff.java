package com.example.multimodule.service;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import com.github.difflib.UnifiedDiffUtils;
import java.util.*;

public final class JsonTextDiff {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new JavaTimeModule());

    private JsonTextDiff() {}

    public static String diffFromObject(Object oldJson, Object newJson) {

        try{
            String oldJsonValue = MAPPER.writeValueAsString(oldJson);
            String newJsonValue = MAPPER.writeValueAsString(newJson);
            return diffFromString(oldJsonValue, newJsonValue);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao gerar diff JSON", e);
        }
    }

    public static String diffFromString(String oldJson, String newJson) {
        try {
            String oldNormalized = normalize(oldJson);
            String newNormalized = normalize(newJson);

            List<String> oldLines = Arrays.asList(oldNormalized.split("\n"));
            List<String> newLines = Arrays.asList(newNormalized.split("\n"));

            Patch<String> patch = DiffUtils.diff(oldLines, newLines);

            if (patch.getDeltas().isEmpty()) {
                return "Nenhuma diferença detectada";
            }

            return renderColored(oldLines, patch);

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao gerar diff JSON", e);
        }
    }

    private static String normalize(String json) throws Exception {
        JsonNode node = MAPPER.readTree(json);
        return MAPPER.writerWithDefaultPrettyPrinter()
                .writeValueAsString(node);
    }

    private static String renderColored(
            List<String> oldLines,
            Patch<String> patch
    ) {
        List<String> unified =
                UnifiedDiffUtils.generateUnifiedDiff(
                        "old",
                        "new",
                        oldLines,
                        patch,
                        2 // linhas de contexto
                );

        StringBuilder sb = new StringBuilder();

        for (String line : unified) {
            if (line.startsWith("+") && !line.startsWith("+++")) {
                sb.append(Ansi.GREEN).append(line).append(Ansi.RESET);
            } else if (line.startsWith("-") && !line.startsWith("---")) {
                sb.append(Ansi.RED).append(line).append(Ansi.RESET);
            } else {
                sb.append(line);
            }
            sb.append("\n");
        }

        return sb.toString();
    }

}

