package me.supcheg.advancedmanhunt.action;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;
import static me.supcheg.advancedmanhunt.action.Action.anyThread;
import static me.supcheg.advancedmanhunt.action.Action.join;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsyncActionTest {

    ExecutorService asyncExecutor;
    ActionExecutor executor;

    Action action;

    AtomicInteger executeTimes;
    AtomicInteger discardTimes;

    @BeforeEach
    void setup() {
        asyncExecutor = Executors.newSingleThreadExecutor();
        executor = new DefaultActionExecutor(Runnable::run, asyncExecutor);

        executeTimes = new AtomicInteger();
        discardTimes = new AtomicInteger();

        action = join(
                anyThread("long-action")
                        .execute(() -> {
                            sleep(1000);
                            executeTimes.incrementAndGet();
                        })
                        .discard(discardTimes::incrementAndGet),
                anyThread("short-action")
                        .execute(executeTimes::incrementAndGet)
                        .discard(discardTimes::incrementAndGet)
        );
    }

    @AfterEach
    void shutdown() {
        asyncExecutor.shutdownNow();
    }

    @Test
    void interruptAndDiscardTest() throws Throwable {
        RunningAction runningAction = executor.execute(action);
        sleep(10);

        runningAction.interruptAndDiscard();

        runningAction.join();

        assertTrue(runningAction.isInterrupted());
        assertEquals(1, executeTimes.get());
        assertEquals(1, discardTimes.get());
    }

    @Test
    void interruptWithoutDiscardTest() throws Throwable {
        RunningAction runningAction = executor.execute(action);
        sleep(10);

        runningAction.interruptWithoutDiscard();

        runningAction.join();

        assertTrue(runningAction.isInterrupted());
        assertEquals(1, executeTimes.get());
        assertEquals(0, discardTimes.get());
    }
}
