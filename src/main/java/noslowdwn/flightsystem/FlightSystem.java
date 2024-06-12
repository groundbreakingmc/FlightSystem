package noslowdwn.flightsystem;

import noslowdwn.flightsystem.cmds.*;
import noslowdwn.flightsystem.utils.Config;
import noslowdwn.flightsystem.handlers.*;
import noslowdwn.flightsystem.utils.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class FlightSystem extends JavaPlugin {

    public static FlightSystem instance;
    public BukkitTask dmgTask;

    @Override
    public void onEnable() {
        instance = this;

        Config.load("config.yml");
        Config.load("messages.yml");
        Config.checkVersion("config.yml", 1.0);
        Config.checkVersion("messages.yml", 1.0);

        getServer().getPluginCommand("flightsystem").setExecutor(new Manager());
        getServer().getPluginCommand("flightsystem").setTabCompleter(new Completer());

        this.getServer().getPluginManager().registerEvents(new Prevention(), this);
        this.getServer().getPluginManager().registerEvents(new UnequipChecker(), this);

        if (getConfig().getBoolean("utils.fly-command.damage.enable", false)) {
            dmgTask = new Damager().runTaskTimerAsynchronously(this, 0L,
                    getConfig().getInt("utils.fly-command.damage.interval", 3) * 20L);
        }

        UpdateChecker.checkVersion();
    }
}
