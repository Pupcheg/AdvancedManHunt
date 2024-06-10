package me.supcheg.advancedmanhunt.game.impl;

import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.storage.InMemoryEntityRepository;

import javax.inject.Inject;
import java.util.UUID;

public final class InMemoryManHuntGameRepository extends InMemoryEntityRepository<ManHuntGame, UUID> implements ManHuntGameRepository {
    @Inject
    public InMemoryManHuntGameRepository() {
        super(ManHuntGame::getUniqueId);
    }
}
