package me.supcheg.advancedmanhunt.gui.json.functional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.functional.AdvancedButtonConfigurer;
import me.supcheg.advancedmanhunt.gui.api.tick.ButtonTicker;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class DefaultButtonConfigurer implements AdvancedButtonConfigurer {
    private final List<ButtonClickAction> clickActions;
    private final List<ButtonTicker> tickers;

    @Override
    public void configure(@NotNull AdvancedButtonBuilder builder) {
        builder.getClickActions().addAll(clickActions);
        builder.getTickers().addAll(tickers);
    }
}
