package me.supcheg.advancedmanhunt.command.exception;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class CustomExceptions {
    public static final SimpleCommandExceptionType ACCESS_DENIED = new SimpleCommandExceptionType(new LiteralMessage("Access denied"));
}
