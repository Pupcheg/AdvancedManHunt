package me.supcheg.advancedmanhunt.gui.api.functional.constant;

import lombok.Data;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
public class ConstantButtonLoreFunction implements ButtonLoreFunction {
    private final List<Component> lore;

    public ConstantButtonLoreFunction(@NotNull List<Component> lore) {
        this.lore = ComponentUtil.copyAndRemoveItalic(lore);
    }

    /**
     * @deprecated use {@link #getLore()}
     */
    @Deprecated
    @NotNull
    @Override
    public List<Component> getLore(@NotNull ButtonResourceGetContext ctx) {
        return lore;
    }

    /**
     * @deprecated use {@link #getLore()}
     */
    @Deprecated
    @NotNull
    @Override
    public List<Component> getLoreWithoutItalic(@NotNull ButtonResourceGetContext ctx) {
        return lore;
    }
}
