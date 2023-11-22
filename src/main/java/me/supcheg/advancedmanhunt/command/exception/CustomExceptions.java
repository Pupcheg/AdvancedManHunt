package me.supcheg.advancedmanhunt.command.exception;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class CustomExceptions {
    public static final SimpleCommandExceptionType INVALID_PATH = new SimpleCommandExceptionType(new LiteralMessage("Invalid path"));
    public static final DynamicCommandExceptionType NO_FILE = new DynamicCommandExceptionType(a -> new LiteralMessage("No file at " + a));
    public static final DynamicCommandExceptionType NO_DIRECTORY = new DynamicCommandExceptionType(a -> new LiteralMessage("No directory at " + a));
    public static final SimpleCommandExceptionType INVALID_UNIQUE_ID = new SimpleCommandExceptionType(new LiteralMessage("Invalid unique id"));
    public static final SimpleCommandExceptionType ACCESS_DENIED = new SimpleCommandExceptionType(new LiteralMessage("Access denied"));
    public static final DynamicCommandExceptionType NULL = new DynamicCommandExceptionType(a -> new LiteralMessage(a + " is null"));
}
