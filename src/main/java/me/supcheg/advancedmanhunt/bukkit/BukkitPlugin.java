package me.supcheg.advancedmanhunt.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().warning("This plugin requires Paper.");
        getLogger().warning("You can download it from the following link: https://papermc.io.");
        Bukkit.getPluginManager().disablePlugin(this);
    }
}
