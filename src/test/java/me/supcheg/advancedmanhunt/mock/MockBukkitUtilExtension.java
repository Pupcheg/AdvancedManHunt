package me.supcheg.advancedmanhunt.mock;

import me.supcheg.advancedmanhunt.paper.BukkitUtilMock;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class MockBukkitUtilExtension implements BeforeEachCallback, AfterEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) {
        BukkitUtilMock.mock();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        BukkitUtilMock.unmock();
    }
}
