package me.supcheg.advancedmanhunt.math.builder;

import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import io.papermc.paper.math.Position;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.math.ImmutableLocation;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static me.supcheg.advancedmanhunt.math.builder.PositionBuilderApplicable.builderApplicable;

@NoArgsConstructor
final class DefaultPositionBuilder implements PositionBuilder, Cloneable {
    private WorldReference world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    @NotNull
    @Override
    public PositionBuilder world(@Nullable WorldReference world) {
        this.world = world;
        return this;
    }

    @NotNull
    @Override
    public PositionBuilder xyz(@NotNull Position pos) {
        assertFinite(pos);
        this.x = pos.x();
        this.y = pos.y();
        this.z = pos.z();
        return this;
    }

    @NotNull
    @Override
    public PositionBuilder offset(@NotNull Position pos) {
        assertFinite(pos);
        this.x += pos.x();
        this.y += pos.y();
        this.z += pos.z();
        return this;
    }

    @NotNull
    @Override
    public PositionBuilder xyz(double x, double y, double z) {
        assertFinite(x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @NotNull
    @Override
    public PositionBuilder offset(double x, double y, double z) {
        assertFinite(x, y, z);
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @NotNull
    @Override
    public PositionBuilder negateXYZ() {
        this.x = -x;
        this.y = -y;
        this.z = -z;
        return this;
    }

    @NotNull
    @Override
    public PositionBuilder negateYawPitch() {
        this.yaw = -yaw;
        this.pitch = -pitch;
        return this;
    }

    @Override
    public WorldReference world() {
        return world;
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

    @Override
    public float yaw() {
        return yaw;
    }

    @Override
    public float pitch() {
        return pitch;
    }

    @NotNull
    @Override
    public PositionBuilder x(double x) {
        assertFinite(x);
        this.x = x;
        return this;
    }

    @NotNull
    @Override
    public PositionBuilder y(double y) {
        assertFinite(y);
        this.y = y;
        return this;
    }

    @NotNull
    @Override
    public PositionBuilder z(double z) {
        assertFinite(z);
        this.z = z;
        return this;
    }

    @NotNull
    @Override
    public PositionBuilder yaw(float yaw) {
        assertFinite(yaw);
        this.yaw = yaw;
        return this;
    }

    @NotNull
    @Override
    public PositionBuilder pitch(float pitch) {
        assertFinite(pitch);
        this.pitch = pitch;
        return this;
    }

    @NotNull
    @Override
    public PositionBuilder yaw(double yaw) {
        return yaw((float) yaw);
    }

    @NotNull
    @Override
    public PositionBuilder pitch(double pitch) {
        return pitch((float) pitch);
    }

    @NotNull
    @Override
    public PositionBuilder apply(@NotNull Position position) {
        builderApplicable(position).applyTo(this);
        return this;
    }

    @NotNull
    @Override
    public ImmutableLocation immutableLocation() {
        return new ImmutableLocation(world, x, y, z, yaw, pitch);
    }

    @NotNull
    @Override
    public Location bukkitLocation() {
        return new Location(world == null ? null : world.getWorld(), x, y, z, yaw, pitch);
    }

    @NotNull
    @Override
    public FinePosition finePosition() {
        return Position.fine(x, y, z);
    }

    @NotNull
    @Override
    public BlockPosition blockPosition() {
        return Position.block((int) x, (int) y, (int) z);
    }

    @Override
    public void applyTo(@NotNull PositionBuilder builder) {
        if (builder.getClass() == DefaultPositionBuilder.class) {
            DefaultPositionBuilder other = (DefaultPositionBuilder) builder;
            other.world = this.world;
            other.x = this.x;
            other.y = this.y;
            other.z = this.z;
            other.yaw = this.yaw;
            other.pitch = this.pitch;
            return;
        }
        builder.world(world)
                .xyz(x, y, z)
                .yaw(yaw)
                .pitch(pitch);
    }

    @SneakyThrows
    @NotNull
    @Override
    public PositionBuilder clone() {
        DefaultPositionBuilder clone = (DefaultPositionBuilder) super.clone();
        applyTo(clone);
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DefaultPositionBuilder that)) {
            return false;
        }

        return Double.compare(that.x, x) == 0
               && Double.compare(that.y, y) == 0
               && Double.compare(that.z, z) == 0
               && Float.compare(that.yaw, yaw) == 0
               && Float.compare(that.pitch, pitch) == 0
               && Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                world,
                x, y, z,
                yaw, pitch
        );
    }

    private static void assertFinite(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Value is not finite: " + value);
        }
    }

    private static void assertFinite(double x, double y, double z) {
        assertFinite(x);
        assertFinite(y);
        assertFinite(z);
    }

    private static void assertFinite(@NotNull Position pos) {
        Objects.requireNonNull(pos, "pos");
        assertFinite(pos.x());
        assertFinite(pos.y());
        assertFinite(pos.z());
    }

    private static void assertFinite(float value) {
        if (!Float.isFinite(value)) {
            throw new IllegalArgumentException("Value is not finite: " + value);
        }
    }
}
