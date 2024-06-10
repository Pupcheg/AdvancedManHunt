package me.supcheg.advancedmanhunt.action;

import lombok.Data;

@Data
public final class ActionThrowable {
    private final ExecutableAction action;
    private final Throwable throwable;
}
