package elytrafly.noslowdwn;

import elytrafly.noslowdwn.Commands.Completer;
import elytrafly.noslowdwn.Commands.Manager;
import elytrafly.noslowdwn.Utils.Config;
import elytrafly.noslowdwn.Utils.fly.Damager;
import elytrafly.noslowdwn.Utils.fly.Prevention;
import elytrafly.noslowdwn.Utils.fly.UnequipChecker;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

// Ilu if u read that (kiss)

public final class ElytraFly extends JavaPlugin {

    public static ElytraFly instance;
    public BukkitTask dmgTask;

    @Override
    public void onEnable() {
        instance = this;
        new Config().load("config.yml");
        new Config().load("messages.yml");
        getServer().getPluginCommand("elytrafly").setExecutor(new Manager());
        getServer().getPluginCommand("elytrafly").setTabCompleter(new Completer());
        this.getServer().getPluginManager().registerEvents(new Prevention(), this);
        this.getServer().getPluginManager().registerEvents(new UnequipChecker(), this);

        if (getConfig().getBoolean("Utils.fly-command.damage.enable", false))
            dmgTask = new Damager().runTaskTimerAsynchronously(this, 0L,
                    getConfig().getInt("Utils.fly-command.damage.interval", 3) * 20L);
    }
}
