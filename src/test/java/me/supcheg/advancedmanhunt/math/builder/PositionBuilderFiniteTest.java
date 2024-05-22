package me.supcheg.advancedmanhunt.math.builder;

import io.papermc.paper.math.Position;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static io.papermc.paper.math.Position.fine;
import static me.supcheg.advancedmanhunt.math.builder.PositionBuilder.position;

class PositionBuilderFiniteTest {

    PositionBuilder builder;

    @BeforeEach
    void setup() {
        builder = PositionBuilder.position();
    }

    @Test
    void xyzNaNThrowTest() {
        assertThrows(() -> builder.xyz(Double.NaN, 0, 0));
        assertThrows(() -> builder.xyz(0, Double.NaN, 0));
        assertThrows(() -> builder.xyz(0, 0, Double.NaN));
    }

    @Test
    void xyzNaNPositionThrowTest() {
        Position xNaN = fine(Double.NaN, 0, 0);
        Position yNaN = fine(0, Double.NaN, 0);
        Position zNaN = fine(0, 0, Double.NaN);

        assertThrows(() -> builder.xyz(xNaN));
        assertThrows(() -> builder.xyz(yNaN));
        assertThrows(() -> builder.xyz(zNaN));
    }

    @Test
    void xNaNThrowTest() {
        assertThrows(() -> builder.x(Double.NaN));
    }

    @Test
    void yNaNThrowTest() {
        assertThrows(() -> builder.y(Double.NaN));
    }

    @Test
    void zNaNThrowTest() {
        assertThrows(() -> builder.z(Double.NaN));
    }

    @Test
    void offsetNaNThrowTest() {
        assertThrows(() -> builder.offset(Double.NaN, 0, 0));
        assertThrows(() -> builder.offset(0, Double.NaN, 0));
        assertThrows(() -> builder.offset(0, 0, Double.NaN));
    }

    @Test
    void offsetNaNPositionThrowTest() {
        Position xNaN = fine(Double.NaN, 0, 0);
        Position yNaN = fine(0, Double.NaN, 0);
        Position zNaN = fine(0, 0, Double.NaN);

        assertThrows(() -> builder.offset(xNaN));
        assertThrows(() -> builder.offset(yNaN));
        assertThrows(() -> builder.offset(zNaN));
    }

    @Test
    void yawNaNThrowTest() {
        assertThrows(() -> builder.yaw(Float.NaN));
        assertThrows(() -> builder.yaw(Double.NaN));
    }

    @Test
    void pitchNaNThrowTest() {
        assertThrows(() -> builder.pitch(Float.NaN));
        assertThrows(() -> builder.pitch(Double.NaN));
    }

    private void assertThrows(@NotNull Executable executable) {
        Assertions.assertThrows(Throwable.class, executable);
    }
}
