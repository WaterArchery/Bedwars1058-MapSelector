package me.leoo.bedwars.mapselector;

import lombok.Getter;
import me.leoo.bedwars.mapselector.commands.FirstGuiCommand;
import me.leoo.bedwars.mapselector.commands.MainCommand;
import me.leoo.bedwars.mapselector.commands.SecondGuiCommand;
import me.leoo.bedwars.mapselector.configuration.CacheConfig;
import me.leoo.bedwars.mapselector.configuration.MainConfig;
import me.leoo.bedwars.mapselector.database.DatabaseManager;
import me.leoo.bedwars.mapselector.hook.PlaceholderAPI;
import me.leoo.bedwars.mapselector.listeners.JoinListener;
import me.leoo.bedwars.mapselector.utils.BedwarsMode;
import me.leoo.utils.bukkit.Utils;
import me.leoo.utils.bukkit.commands.CommandManager;
import me.leoo.utils.bukkit.config.ConfigManager;
import me.leoo.utils.bukkit.events.Events;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getPluginManager;

@Getter
public class MapSelector extends JavaPlugin {

    @Getter
    private static MapSelector plugin;

    private ConfigManager mainConfig;
    private ConfigManager cacheConfig;
    private DatabaseManager databaseManager;
    private BedwarsMode bedwarsMode;

    @Override
    public void onEnable() {
        plugin = this;

        Utils.initialize(this);

        for (BedwarsMode mode : BedwarsMode.values()) {
            if (getPluginManager().isPluginEnabled(mode.getName())) {
                bedwarsMode = mode;

                getLogger().info("Hooked into " + mode.getName());
            }
        }

        if (bedwarsMode == null) {
            getLogger().info("Bedwars1058/BedwarsProxy not found. Disabling...");

            getPluginManager().disablePlugin(this);

            return;
        }

        String directory = "plugins/" + bedwarsMode.getName() + "/Addons/MapSelector";
        mainConfig = new MainConfig("config", directory);
        cacheConfig = new CacheConfig("cache", directory);

        databaseManager = new DatabaseManager();

        if (getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPI().register();
        }

        Events.register(new JoinListener());
        CommandManager.register(new FirstGuiCommand(), new SecondGuiCommand(), new MainCommand());

        getLogger().info(getDescription().getName() + " plugin by itz_leoo has been successfully enabled.");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) databaseManager.close();

        getLogger().info(getDescription().getName() + " plugin by itz_leoo has been successfully disabled.");
    }

    public void debug(String string) {
        if (mainConfig.getBoolean("map-selector.debug")) {
            getLogger().info(string);
        }
    }

    public static MapSelector get() {
        return plugin;
    }
}
