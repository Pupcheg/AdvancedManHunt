package me.supcheg.advancedmanhunt.gui.api.functional.constant;

import lombok.Data;
import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import org.jetbrains.annotations.NotNull;

@Data
public class ConstantGuiBackgroundFunction implements GuiBackgroundFunction {
    private final String path;

    /**
     * @deprecated use {@link #getPath()}
     */
    @Deprecated
    @NotNull
    @Override
    public String getBackground(@NotNull GuiResourceGetContext ctx) {
        ctx.getGui().setBackground(this);
        return path;
    }
}
