package me.supcheg.advancedmanhunt.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class AdvancedGuiArgumentType implements CustomArgumentType<AdvancedGui, Key> {
    private static final SimpleCommandExceptionType NO_GUI = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(Component.translatable("advancedmanhunt.exception.no_gui"))
    );

    private final AdvancedGuiController controller;
    private final ArgumentType<Key> keyArgumentType;

    private AdvancedGuiArgumentType(@NotNull AdvancedGuiController controller) {
        this.controller = controller;
        this.keyArgumentType = ArgumentTypes.key();
    }

    @NotNull
    public static ArgumentType<AdvancedGui> advancedGui(@NotNull AdvancedGuiController controller) {
        return new AdvancedGuiArgumentType(controller);
    }

    @NotNull
    public static AdvancedGui getAdvancedGui(@NotNull CommandContext<CommandSourceStack> ctx, @NotNull String name) {
        return ctx.getArgument(name, AdvancedGui.class);
    }

    @NotNull
    @Override
    public AdvancedGui parse(@NotNull StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        Key key = keyArgumentType.parse(reader);

        AdvancedGui gui = controller.getGui(key);
        if (gui == null) {
            reader.setCursor(cursor);
            throw NO_GUI.create();
        }

        return gui;
    }

    @NotNull
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context,
                                                              @NotNull SuggestionsBuilder builder) {
        for (Key key : controller.getRegisteredKeys()) {
            builder.suggest(key.asString());
        }
        return builder.buildFuture();
    }

    @NotNull
    @Override
    public ArgumentType<Key> getNativeType() {
        return keyArgumentType;
    }
}
