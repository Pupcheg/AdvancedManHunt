package me.supcheg.advancedmanhunt.action;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.IntStream.rangeClosed;
import static me.supcheg.advancedmanhunt.action.Action.anyThread;
import static me.supcheg.advancedmanhunt.action.Action.join;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ActionTest {

    ActionExecutor executor;
    List<Integer> out;

    @BeforeEach
    void setup() {
        executor = new DefaultActionExecutor(Runnable::run, Runnable::run);
        out = new ArrayList<>();
    }

    @Test
    void sequenceTest() {
        Action action = join(
                actionWithId(1),
                actionWithId(2),
                join(
                        actionWithId(3),
                        actionWithId(4)
                ),
                actionWithId(5),
                join(
                        actionWithId(6)
                ),
                join(
                        actionWithId(7),
                        join(
                                actionWithId(8),
                                actionWithId(9)
                        )
                ),
                actionWithId(10)
        );
        executor.execute(action);

        assertEquals(
                rangeClosed(1, 10).boxed().toList(),
                out.stream().sorted().toList()
        );
    }

    @Test
    void singleExecuteTest() {
        Action action = actionWithId(0);
        executor.execute(action);
    }

    @Test
    void throwTest() {
        Action action = join(
                actionWithId(1),
                actionWithId(2),
                throwingActionWithId(3),
                actionWithId(4)
        );
        RunningAction future = executor.execute(action);
        future.join();

        assertEquals(List.of(), out);
        assertEquals(1, future.listThrowables().size());
    }

    @Test
    void noExecutableTest() {
        Action action = join();
        assertThrows(Throwable.class, () -> executor.execute(action));
    }

    @NotNull
    Action throwingActionWithId(int id) {
        return anyThread("action-" + id)
                .execute(() -> {
                    out.add(id);
                    throw new Exception();
                })
                .discard(() -> out.remove((Object) id))
                .build();
    }

    @NotNull
    Action actionWithId(int id) {
        return anyThread("action-" + id)
                .execute(() -> out.add(id))
                .discard(() -> out.remove((Object) id))
                .build();
    }
}
