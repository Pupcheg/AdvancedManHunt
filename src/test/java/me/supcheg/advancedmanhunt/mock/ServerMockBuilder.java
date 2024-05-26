package me.supcheg.advancedmanhunt.mock;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;

import java.io.File;
import java.util.function.Consumer;

import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.doReturn;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServerMockBuilder {
    @NotNull
    @SafeVarargs
    public static ServerMock buildServerMock(@NotNull Consumer<ServerMock> @NotNull ... configurators) {
        ServerMock mock = Mockito.mock(ServerMock.class, delegatesTo(new ServerMock()));
        for (Consumer<ServerMock> configurator : configurators) {
            configurator.accept(mock);
        }
        MockBukkit.mock(mock);
        return mock;
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    public static Consumer<ServerMock> getWorldContainerEmptyFile() {
        return mock -> doReturn(new File("")).when(mock).getWorldContainer();
    }
}
