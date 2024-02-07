package me.supcheg.advancedmanhunt.config;

import lombok.Data;

@Data(staticConstructor = "of")
public class IntLimit {
    private final int minValue;
    private final int maxValue;
}
