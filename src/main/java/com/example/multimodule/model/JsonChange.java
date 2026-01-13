package com.example.multimodule.model;

import com.fasterxml.jackson.databind.JsonNode;

public record JsonChange(
        String path,
        ChangeType type,
        JsonNode oldValue,
        JsonNode newValue
) {}