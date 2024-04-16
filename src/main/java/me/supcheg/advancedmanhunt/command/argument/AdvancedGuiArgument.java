package me.supcheg.advancedmanhunt.command.argument;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.google.common.collect.Collections2;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.papermc.paper.brigadier.PaperBrigadier;
import me.supcheg.advancedmanhunt.bridge.command.KeyArgument;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.suggestIfStartsWith;

public class AdvancedGuiArgument {
    private static final SimpleCommandExceptionType NO_GUI = new SimpleCommandExceptionType(
            PaperBrigadier.message(Component.translatable("advancedmanhunt.exception.no_gui"))
    );

    private final KeyArgument keyArgument;
    private final AdvancedGuiController controller;
    private final SuggestionProvider<BukkitBrigadierCommandSource> guiKeysProvider;

    @Inject
    public AdvancedGuiArgument(@NotNull KeyArgument keyArgument, @NotNull AdvancedGuiController controller) {
        this.keyArgument = keyArgument;
        this.controller = controller;
        this.guiKeysProvider = suggestIfStartsWith(
                Collections2.transform(controller.getRegisteredKeys(), Key::asString)
        );
    }

    @NotNull
    public RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> gui(@NotNull String name) {
        return keyArgument.key(name).suggests(guiKeysProvider);
    }

    @NotNull
    public AdvancedGui getGui(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name)
            throws CommandSyntaxException {
        AdvancedGui gui = controller.getGui(keyArgument.getKey(ctx, name));
        if (gui == null) {
            throw NO_GUI.create();
        }
        return gui;
    }
}
