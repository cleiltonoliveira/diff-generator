package com.example.multimodule;

import com.example.multimodule.model.ChangeType;
import com.example.multimodule.model.JsonChange;
import com.example.multimodule.service.JsonDiffProcessor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonDiffProcessorTest {

    @Test
    void shouldDetectSimpleValueChange() {
        String oldJson = """
            { "name": "John" }
        """;

        String newJson = """
            { "name": "John Doe" }
        """;

        List<JsonChange> changes =
                JsonDiffProcessor.diffAsJsonFromString(oldJson, newJson, false);

        assertEquals(1, changes.size());

        JsonChange change = changes.get(0);
        assertEquals("name", change.path());
        assertEquals(ChangeType.CHANGE, change.type());
        assertEquals("John", change.oldValue().asText());
        assertEquals("John Doe", change.newValue().asText());
    }

    @Test
    void shouldDetectAddedField() {
        String oldJson = """
            { "name": "John" }
        """;

        String newJson = """
            { "name": "John", "age": 30 }
        """;

        List<JsonChange> changes =
                JsonDiffProcessor.diffAsJsonFromString(oldJson, newJson, false);

        assertEquals(1, changes.size());

        JsonChange change = changes.get(0);
        assertEquals("age", change.path());
        assertEquals(ChangeType.ADD, change.type());
        assertNull(change.oldValue());
        assertEquals(30, change.newValue().asInt());
    }

    @Test
    void shouldDetectRemovedField() {
        String oldJson = """
            { "name": "John", "phone": "123" }
        """;

        String newJson = """
            { "name": "John" }
        """;

        List<JsonChange> changes =
                JsonDiffProcessor.diffAsJsonFromString(oldJson, newJson, false);

        assertEquals(1, changes.size());

        JsonChange change = changes.get(0);
        assertEquals("phone", change.path());
        assertEquals(ChangeType.REMOVE, change.type());
        assertEquals("123", change.oldValue().asText());
        assertNull(change.newValue());
    }

    /* ==========================================================
       ============ OBJETO ANINHADO ===============================
       ========================================================== */
    @Test
    void shouldDetectNestedChangesCorrectly() {
        String oldJson = """
            {
              "customer": {
                "id": 1,
                "name": "John",
                "address": {
                  "zipCode": "100012"
                }
              }
            }
        """;

        String newJson = """
            {
              "customer": {
                "id": 1,
                "name": "John Doe",
                "address": {
                  "zipCode": "10001",
                  "country": "US"
                }
              }
            }
        """;

        List<JsonChange> changes =
                JsonDiffProcessor.diffAsJsonFromString(oldJson, newJson, false);

        assertEquals(3, changes.size());

        assertContainsChange(
                changes,
                "customer.name",
                ChangeType.CHANGE,
                "John",
                "John Doe"
        );

        assertContainsChange(
                changes,
                "customer.address.zipCode",
                ChangeType.CHANGE,
                "100012",
                "10001"
        );

        assertContainsChange(
                changes,
                "customer.address.country",
                ChangeType.ADD,
                null,
                "US"
        );
    }

     /* ==========================================================
       ============ ARRAYS ======================================
       ========================================================== */
    @Test
    void shouldDetectArrayElementChangeByIndex() {
        String oldJson = """
            {
              "items": [
                { "sku": "A" },
                { "sku": "B" }
              ]
            }
        """;

        String newJson = """
            {
              "items": [
                { "sku": "A" },
                { "sku": "C" }
              ]
            }
        """;

        List<JsonChange> changes =
                JsonDiffProcessor.diffAsJsonFromString(oldJson, newJson, false);

        assertEquals(1, changes.size());

        JsonChange change = changes.get(0);
        assertEquals("items[1].sku", change.path());
        assertEquals(ChangeType.CHANGE, change.type());
        assertEquals("B", change.oldValue().asText());
        assertEquals("C", change.newValue().asText());
    }

    @Test
    void shouldIgnoreOrderForSimpleArrays() {
        String oldJson = """
          { "roles": ["ADMIN", "USER"] }
        """;

        String newJson = """
          { "roles": ["USER", "ADMIN"] }
        """;

        List<JsonChange> changes =
                JsonDiffProcessor.diffAsJsonFromString(oldJson, newJson, false);

        assertTrue(changes.isEmpty());
    }

    @Test
    void shouldWorkWithJavaObjects() {
        Person oldPerson = new Person("John", 20);
        Person newPerson = new Person("John Doe", 20);

        List<JsonChange> changes =
                JsonDiffProcessor.diffAsJsonFromObject(oldPerson, newPerson, false);

        assertEquals(1, changes.size());

        JsonChange change = changes.get(0);
        assertEquals("name", change.path());
        assertEquals(ChangeType.CHANGE, change.type());
        assertEquals("John", change.oldValue().asText());
        assertEquals("John Doe", change.newValue().asText());
    }

    /* ==========================================================
       ============ HELPERS =====================================
       ========================================================== */
    private void assertContainsChange(
            List<JsonChange> changes,
            String path,
            ChangeType type,
            String oldValue,
            String newValue
    ) {
        JsonChange change = changes.stream()
                .filter(c -> c.path().equals(path))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Expected change not found: " + path)
                );

        assertEquals(type, change.type());

        if (oldValue == null) {
            assertNull(change.oldValue());
        } else {
            assertEquals(oldValue, change.oldValue().asText());
        }

        if (newValue == null) {
            assertNull(change.newValue());
        } else {
            assertEquals(newValue, change.newValue().asText());
        }
    }
}
