package me.supcheg.advancedmanhunt.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import static me.supcheg.advancedmanhunt.action.Action.anyThread;
import static me.supcheg.advancedmanhunt.action.Action.join;
import static me.supcheg.advancedmanhunt.action.Action.mainThread;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SyncActionTest {

    AtomicInteger mainThreadExecutesCount;
    ActionExecutor executor;

    @BeforeEach
    void setup() {
        Executor defaultExecutor = Runnable::run;

        mainThreadExecutesCount = new AtomicInteger();
        Executor mainThreadExecutor = command -> {
            mainThreadExecutesCount.incrementAndGet();
            command.run();
        };

        executor = new DefaultActionExecutor(mainThreadExecutor, defaultExecutor);
    }

    @Test
    void executeOnMainThreadTest() {
        executor.execute(join(
                anyThread("any-thread-task-1"),
                mainThread("main-thread-task"),
                anyThread("any-thread-task-2")
        )).join();

        assertEquals(1, mainThreadExecutesCount.get());
    }
}
