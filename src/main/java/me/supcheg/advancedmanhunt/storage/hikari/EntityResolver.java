package me.supcheg.advancedmanhunt.storage.hikari;

import com.google.gson.FieldNamingPolicy;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class EntityResolver {
    private final Class<?> entityClass;

    @Unmodifiable
    @NotNull
    public List<ResolvedField> resolveAllFields() {
        Field[] classFields = entityClass.getDeclaredFields();

        List<ResolvedField> resolvedFields = new ArrayList<>(classFields.length);

        for (Field classField : classFields) {
            ResolvedField resolvedField = resolveField(classField, classField.getAnnotation(DatabaseField.class));
            resolvedFields.add(resolvedField);
        }

        return resolvedFields;
    }

    @NotNull
    @Contract("_, _ -> new")
    private ResolvedField resolveField(@NotNull Field field, @Nullable DatabaseField annotation) {
        ResolvedAnnotation resolvedAnnotation = resolveAnnotation(annotation);

        String name = resolvedAnnotation.getName().equals(DatabaseField.DEFAULT_NAME_RESOLVE) ?
                resolveName(field) : resolvedAnnotation.getName();
        boolean isPrimary = resolvedAnnotation.isPrimary();

        return new ResolvedField(name, isPrimary);
    }

    @NotNull
    private static String resolveName(@NotNull Field field) {
        return FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(field);
    }

    @NotNull
    private static ResolvedAnnotation resolveAnnotation(@Nullable DatabaseField annotation) {
        return annotation == null ? ResolvedAnnotation.DEFAULT :
                new ResolvedAnnotation(annotation.value(), annotation.primary());
    }

    @Data
    private static final class ResolvedAnnotation {
        private static final ResolvedAnnotation DEFAULT
                = new ResolvedAnnotation(DatabaseField.DEFAULT_NAME_RESOLVE, false);

        private final String name;
        private final boolean primary;
    }
}
