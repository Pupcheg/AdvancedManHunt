package me.supcheg.advancedmanhunt.player.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;

import java.util.UUID;

@Data
@EqualsAndHashCode(of = "uniqueId")
public class DefaultManHuntPlayerView implements ManHuntPlayerView {
    private final UUID uniqueId;
    private ManHuntGame game;
}
