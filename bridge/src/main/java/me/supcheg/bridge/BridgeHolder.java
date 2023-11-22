package me.supcheg.bridge;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BridgeHolder {
    @Setter
    private static Bridge instance;

    public static boolean hasInstance() {
        return instance != null;
    }

    @NotNull
    public static Bridge getInstance() {
        return Objects.requireNonNull(instance, "Bridge is not set");
    }
}
