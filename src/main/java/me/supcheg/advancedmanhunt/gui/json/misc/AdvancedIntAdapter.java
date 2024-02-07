package me.supcheg.advancedmanhunt.gui.json.misc;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.gui.json.BadPropertyException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdvancedIntAdapter extends TypeAdapter<IntStream> {
    public static final AdvancedIntAdapter INSTANCE = new AdvancedIntAdapter();

    private static final Map<String, ExpressionType> EXPRESSION_TYPES = Map.of(
            "range", new RangeExpressionType(),
            "every", new EveryExpressionType()
    );

    @Override
    public void write(@NotNull JsonWriter out, @NotNull IntStream value) throws IOException {
        int[] array = value.distinct().toArray();

        if (array.length == 1) {
            out.value(array[0]);
            return;
        }

        out.beginArray();
        for (int i : array) {
            out.value(i);
        }
        out.endArray();
    }

    @NotNull
    @Override
    public IntStream read(@NotNull JsonReader in) throws IOException {
        JsonToken peek = in.peek();

        if (peek == JsonToken.NUMBER) {
            return IntStream.of(in.nextInt());
        }

        if (peek == JsonToken.BEGIN_ARRAY) {
            IntStream.Builder builder = IntStream.builder();

            in.beginArray();
            while (in.hasNext()) {
                builder.add(in.nextInt());
            }
            in.endArray();

            return builder.build();
        }

        String raw = in.nextString().trim();

        if (raw.isEmpty()) {
            throw new BadPropertyException();
        }

        if (raw.charAt(0) != '#') {
            return IntStream.of(Integer.parseInt(raw));
        }

        int spaceIndex = raw.indexOf(' ');

        String rawType = raw.substring(1, spaceIndex).toLowerCase();

        ExpressionType expressionType = EXPRESSION_TYPES.get(rawType);
        if (expressionType == null) {
            throw new BadPropertyException("Unsupported expression type: '%s'".formatted(rawType));
        }

        String rawExpression = raw.substring(spaceIndex + 1);
        if (!expressionType.supports(rawExpression)) {
            throw new BadPropertyException("Invalid expression '%s' for '%s' expression type".formatted(rawExpression, rawType));
        }

        return expressionType.parse(rawExpression);
    }

    public interface ExpressionType {
        boolean supports(@NotNull String raw);

        @NotNull
        IntStream parse(@NotNull String raw);
    }

    public static class RangeExpressionType implements ExpressionType {
        private final Pattern validPattern = Pattern.compile("^[0-9]+(?:-[0-9]+)?(?:,[0-9]+(?:-[0-9]+)?)*$");
        private final Pattern nextValuePattern = Pattern.compile("([0-9]+)(?:-([0-9]+))?(?:,|$)");

        @Override
        public boolean supports(@NotNull String raw) {
            return validPattern.matcher(raw).matches();
        }

        @NotNull
        @Override
        public IntStream parse(@NotNull String raw) {
            Matcher matcher = nextValuePattern.matcher(raw);
            IntStream stream = IntStream.empty();
            while (matcher.find()) {
                String rawStart = matcher.group(1);
                int start = Integer.parseInt(rawStart);

                String rawEnd = matcher.group(2);
                if (rawEnd == null) {
                    stream = IntStream.concat(stream, IntStream.of(start));
                } else {
                    int end = Integer.parseInt(rawEnd);

                    if (end < start) {
                        throw new IllegalArgumentException("End: %d is less than start: %d".formatted(end, start));
                    }
                    stream = IntStream.concat(stream, IntStream.rangeClosed(start, end));
                }
            }

            return stream;
        }
    }

    public static class EveryExpressionType implements ExpressionType {
        private final Pattern validPattern = Pattern.compile("\\d+(?:, *\\d+)*");
        private final Pattern splitPattern = Pattern.compile(", ");

        @Override
        public boolean supports(@NotNull String raw) {
            return validPattern.matcher(raw).matches();
        }

        @NotNull
        @Override
        public IntStream parse(@NotNull String raw) {
            int[] ints = Arrays.stream(splitPattern.split(raw))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            return IntStream.range(0, 54).filter(i -> Arrays.stream(ints).anyMatch(del -> i % del == 0));
        }
    }
}
