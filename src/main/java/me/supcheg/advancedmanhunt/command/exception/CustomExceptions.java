package me.supcheg.advancedmanhunt.command.exception;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class CustomExceptions {
    public static final SimpleCommandExceptionType INVALID_PATH = new SimpleCommandExceptionType(new LiteralMessage("Invalid path"));
    public static final DynamicCommandExceptionType NO_DIRECTORY = new DynamicCommandExceptionType(a -> new LiteralMessage("No directory at " + a));
}
