package me.supcheg.advancedmanhunt.gui.api.functional.constant;

import lombok.Data;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@Data
public class ConstantButtonNameFunction implements ButtonNameFunction {
    private final Component name;

    public ConstantButtonNameFunction(@NotNull Component name) {
        this.name = ComponentUtil.removeItalic(name);
    }

    /**
     * @deprecated use {@link #getName()}
     */
    @Deprecated
    @NotNull
    @Override
    public Component getName(@NotNull ButtonResourceGetContext ctx) {
        return name;
    }

    /**
     * @deprecated use {@link #getName()}
     */
    @Deprecated
    @NotNull
    @Override
    public Component getNameWithoutItalic(@NotNull ButtonResourceGetContext ctx) {
        return name;
    }
}
