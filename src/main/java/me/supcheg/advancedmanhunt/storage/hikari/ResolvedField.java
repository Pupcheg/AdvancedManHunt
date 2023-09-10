package me.supcheg.advancedmanhunt.storage.hikari;

import lombok.Data;

@Data
public class ResolvedField {
    private final String fieldName;
    private final boolean primary;
}
