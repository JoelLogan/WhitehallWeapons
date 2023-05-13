package com.whitehallplugins.whitehallweapons;

import com.whitehallplugins.whitehallweapons.Commands.WWCommands;
import com.whitehallplugins.whitehallweapons.Commands.WWTabCompleter;
import com.whitehallplugins.whitehallweapons.Events.WWEventListener;
import com.whitehallplugins.whitehallweapons.Items.ItemManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Plugin Starting");
        Objects.requireNonNull(getCommand("weapons")).setExecutor(new WWCommands());
        Objects.requireNonNull(getCommand("weapons")).setTabCompleter(new WWTabCompleter());
        getServer().getPluginManager().registerEvents(new WWEventListener(this), this);
        ItemManager.init();
        WWEventListener.init();
        saveDefaultConfig();
        getLogger().info("Plugin Started");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin Stopping");
        getLogger().info("Plugin Stopped");
    }
}
