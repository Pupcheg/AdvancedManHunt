package me.supcheg.advancedmanhunt.gui.api.functional.action;

import lombok.Data;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickActionConsumer;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

@Data
public class PlaySoundButtonClickActionConsumer implements ButtonClickActionConsumer {
    private final Sound sound;

    @Override
    public void accept(@NotNull ButtonClickContext ctx) {
        ctx.getPlayer().playSound(sound);
    }
}
