package me.supcheg.advancedmanhunt.action;

import lombok.Data;

@Data
public class ActionThrowable {
    private final ExecutableAction action;
    private final Throwable throwable;
}
