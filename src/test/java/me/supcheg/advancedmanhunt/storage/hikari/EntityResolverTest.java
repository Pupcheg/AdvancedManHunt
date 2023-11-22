package me.supcheg.advancedmanhunt.storage.hikari;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityResolverTest {

    @Test
    void test() {
        List<String> resolvedFieldsNames = new EntityResolver(TestEntity.class).resolveAllFields()
                .stream()
                .map(ResolvedField::getFieldName)
                .toList();
        assertEquals(resolvedFieldsNames, List.of("string_1", "string_2_abc"));
    }

    public static class TestEntity {
        private final String string_1 = "value";
        @DatabaseField("string_2_abc")
        private final String string_2 = "value";
    }
}
