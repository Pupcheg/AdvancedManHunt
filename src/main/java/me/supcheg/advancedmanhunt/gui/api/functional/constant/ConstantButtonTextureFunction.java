package me.supcheg.advancedmanhunt.gui.api.functional.constant;

import lombok.Data;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import org.jetbrains.annotations.NotNull;

@Data
public class ConstantButtonTextureFunction implements ButtonTextureFunction {
    private final String path;

    /**
     * @deprecated use {@link #getPath()}
     */
    @Deprecated
    @NotNull
    @Override
    public String getTexture(@NotNull ButtonResourceGetContext ctx) {
        return path;
    }
}
