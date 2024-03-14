package me.supcheg.advancedmanhunt.action;

import lombok.Data;

import java.util.List;

@Data
final class PlainJoinedAction implements JoinedAction {
    private final List<? extends Action> actions;
}
