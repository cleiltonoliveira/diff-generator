package com.example.multimodule.service;

import com.example.multimodule.model.ChangeType;
import com.example.multimodule.model.JsonChange;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.*;

public final class JsonDiffProcessor {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new JavaTimeModule());

    private JsonDiffProcessor() {
    }

    public static List<JsonChange>  diffAsJsonFromObject(
            Object oldObj,
            Object newObj,
            boolean logDiff
    ) {
        try {

            if (logDiff) {
                JsonDiffPresentation.diffFromObject(oldObj, newObj);
            }

            JsonNode oldNode = MAPPER.valueToTree(oldObj);
            JsonNode newNode = MAPPER.valueToTree(newObj);

            return buildStructuredDiff(oldNode, newNode);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao gerar diff estruturado", e);
        }
    }

    public static List<JsonChange> diffAsJsonFromString(
            String oldJson,
            String newJson,
            boolean logDiff
    ) {
        try {

            if (logDiff) {
                JsonDiffPresentation.diffFromString(oldJson, newJson);
            }

            JsonNode oldNode = MAPPER.readTree(oldJson);
            JsonNode newNode = MAPPER.readTree(newJson);

            return buildStructuredDiff(oldNode, newNode);

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao gerar diff estruturado", e);
        }
    }

    private static List<JsonChange> buildStructuredDiff(
            JsonNode oldNode,
            JsonNode newNode
    ) {

        return diffAsJson(oldNode, newNode, "");

//        ObjectNode result = MAPPER.createObjectNode();
//        result.set("changes", MAPPER.valueToTree(changes));

//        return MAPPER.writerWithDefaultPrettyPrinter()
//                .writeValueAsString(result);
    }

    private static List<JsonChange> diffAsJson(
            JsonNode oldNode,
            JsonNode newNode,
            String path
    ) {
        List<JsonChange> changes = new ArrayList<>();

        // ADD
        if (oldNode == null && newNode != null) {
            changes.add(new JsonChange(path, ChangeType.ADD, null, newNode));
            return changes;
        }

        // REMOVE
        if (oldNode != null && newNode == null) {
            changes.add(new JsonChange(path, ChangeType.REMOVE, oldNode, null));
            return changes;
        }

        // TYPE CHANGE
        if (!oldNode.getNodeType().equals(newNode.getNodeType())) {
            changes.add(new JsonChange(path, ChangeType.CHANGE, oldNode, newNode));
            return changes;
        }

        // VALUE CHANGE
        if (oldNode.isValueNode()) {
            if (!oldNode.equals(newNode)) {
                changes.add(new JsonChange(path, ChangeType.CHANGE, oldNode, newNode));
            }
            return changes;
        }

        // OBJECT
        if (oldNode.isObject()) {
            Set<String> fields = new HashSet<>();
            oldNode.fieldNames().forEachRemaining(fields::add);
            newNode.fieldNames().forEachRemaining(fields::add);

            for (String field : fields) {
                changes.addAll(
                        diffAsJson(
                                oldNode.get(field),
                                newNode.get(field),
                                path.isEmpty() ? field : path + "." + field
                        )
                );
            }
        }

        // ARRAY (por índice)
        if (oldNode.isArray()) {
            int max = Math.max(oldNode.size(), newNode.size());
            for (int i = 0; i < max; i++) {
                changes.addAll(
                        diffAsJson(
                                oldNode.path(i),
                                newNode.path(i),
                                path + "[" + i + "]"
                        )
                );
            }
        }

        return changes;
    }

}

