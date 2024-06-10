package me.supcheg.advancedmanhunt.command.exception;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomExceptions {
    public static final SimpleCommandExceptionType ACCESS_DENIED = new SimpleCommandExceptionType(new LiteralMessage("Access denied"));
}
