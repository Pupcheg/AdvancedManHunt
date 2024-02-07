package me.supcheg.advancedmanhunt.gui.api.functional.action;

import lombok.Data;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickActionConsumer;
import org.jetbrains.annotations.NotNull;

@Data
public class OpenGuiButtonClickActionConsumer implements ButtonClickActionConsumer {
    private final String key;

    @Override
    public void accept(@NotNull ButtonClickContext ctx) {
        ctx.getGui().getController().getGuiOrThrow(key).open(ctx.getPlayer());
    }
}
