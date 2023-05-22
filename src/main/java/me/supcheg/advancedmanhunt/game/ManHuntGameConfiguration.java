package me.supcheg.advancedmanhunt.game;

import lombok.Builder;
import lombok.Data;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import me.supcheg.advancedmanhunt.template.Template;
import org.jetbrains.annotations.NotNull;

@Builder
@Data
public class ManHuntGameConfiguration {
    private final boolean randomizeRolesOnStart;
    @NotNull
    private final Template overworldTemplate;
    @NotNull
    private final Template netherTemplate;
    @NotNull
    private final Template endTemplate;
    @NotNull
    private final SpawnLocationFinder spawnLocationFinder;
}
