package me.supcheg.advancedmanhunt.coord;

import io.papermc.paper.math.FinePosition;
import io.papermc.paper.math.Position;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("UnstableApiUsage")
public class ImmutableLocation implements FinePosition {
    private final WorldReference worldReference;
    private final double x;
    private final double y;
    private final double z;

    private final float yaw;
    private final float pitch;

    @NotNull
    @Contract(value = "-> new", pure = true)
    public static ImmutableLocation.Builder immutableLocation() {
        return new Builder();
    }

    @Nullable
    @Contract(value = "null -> null; !null -> !null", pure = true)
    public static ImmutableLocation immutableCopy(@Nullable Location location) {
        return location == null ? null : new ImmutableLocation(
                WorldReference.ofNullable(location.getWorld()),
                location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch()
        );
    }

    @Nullable
    @Contract(value = "null -> null; !null -> !null", pure = true)
    public static Location mutableCopy(@Nullable ImmutableLocation immutableLocation) {
        return immutableLocation == null ? null : immutableLocation.asMutable();
    }

    @Nullable
    public World getWorld() {
        return worldReference == null ? null : worldReference.getWorld();
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    public Location asMutable() {
        return new Location(getWorld(), x, y, z, yaw, pitch);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public ImmutableLocation copyWith(@NotNull Consumer<Builder> consumer) {
        Objects.requireNonNull(consumer, "consumer");
        Builder builder = toBuilder();
        consumer.accept(builder);
        return builder.build();
    }

    @NotNull
    public ImmutableLocation.Builder toBuilder() {
        return new Builder(this);
    }

    private ImmutableLocation(@NotNull ImmutableLocation.Builder builder) {
        this.worldReference = builder.worldReference;
        this.x = builder.x;
        this.y = builder.y;
        this.z = builder.z;
        this.yaw = builder.yaw;
        this.pitch = builder.pitch;
    }

    public static class Builder {
        private WorldReference worldReference;
        private double x;
        private double y;
        private double z;
        private float yaw;
        private float pitch;

        private Builder() {
        }

        private Builder(@NotNull ImmutableLocation location) {
            this.worldReference = location.worldReference;
            this.x = location.x;
            this.y = location.y;
            this.z = location.z;
            this.yaw = location.yaw;
            this.pitch = location.pitch;
        }

        @NotNull
        public ImmutableLocation.Builder world(@Nullable World world) {
            this.worldReference = WorldReference.ofNullable(world);
            return this;
        }

        @NotNull
        public ImmutableLocation.Builder world(@Nullable WorldReference worldReference) {
            this.worldReference = worldReference;
            return this;
        }

        @NotNull
        public ImmutableLocation.Builder xyz(@NotNull Position pos) {
            Objects.requireNonNull(pos, "pos");
            this.x = pos.x();
            this.y = pos.y();
            this.z = pos.z();
            return this;
        }

        @NotNull
        public ImmutableLocation.Builder offset(@NotNull Position pos) {
            Objects.requireNonNull(pos, "pos");
            this.x += pos.x();
            this.y += pos.y();
            this.z += pos.z();
            return this;
        }

        @NotNull
        public ImmutableLocation.Builder xyz(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        @NotNull
        public ImmutableLocation.Builder offset(double x, double y, double z) {
            this.x += x;
            this.y += y;
            this.z += z;
            return this;
        }

        @NotNull
        public ImmutableLocation.Builder x(double x) {
            this.x = x;
            return this;
        }

        @NotNull
        public ImmutableLocation.Builder y(double y) {
            this.y = y;
            return this;
        }

        @NotNull
        public ImmutableLocation.Builder z(double z) {
            this.z = z;
            return this;
        }

        @NotNull
        public ImmutableLocation.Builder yaw(float yaw) {
            this.yaw = yaw;
            return this;
        }

        @NotNull
        public ImmutableLocation.Builder pitch(float pitch) {
            this.pitch = pitch;
            return this;
        }

        @NotNull
        public ImmutableLocation build() {
            return new ImmutableLocation(this);
        }
    }
}
