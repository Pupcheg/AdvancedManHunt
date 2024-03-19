package me.supcheg.advancedmanhunt.paper;

import be.seeseemelk.mockbukkit.MockBukkit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitUtilMock {
    public static void setPlugin(@Nullable JavaPlugin plugin) {
        BukkitUtil.PLUGIN = plugin;
    }

    public static void mock() {
        MockBukkit.ensureMocking();
        setPlugin(MockBukkit.createMockPlugin());
    }

    public static void unmock() {
        setPlugin(null);
    }
}
