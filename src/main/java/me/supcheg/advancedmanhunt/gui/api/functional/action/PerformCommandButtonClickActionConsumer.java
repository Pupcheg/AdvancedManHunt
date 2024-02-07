package me.supcheg.advancedmanhunt.gui.api.functional.action;

import lombok.Data;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickActionConsumer;
import org.jetbrains.annotations.NotNull;

@Data
public class PerformCommandButtonClickActionConsumer implements ButtonClickActionConsumer {
    private final String label;

    @Override
    public void accept(@NotNull ButtonClickContext ctx) {
        ctx.getPlayer().performCommand(label);
    }
}
