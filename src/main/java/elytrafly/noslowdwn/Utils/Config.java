package elytrafly.noslowdwn.Utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static elytrafly.noslowdwn.ElytraFly.instance;

public class Config {

    public static FileConfiguration msgConfig;

    public void load(String name) {
        File file = new File(instance.getDataFolder(), name);

        if (!file.exists())
            instance.saveResource(name,false);

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dmgCheck();
    }

    public static FileConfiguration getMessagesConfig() {
        File file = new File(instance.getDataFolder(), "messages.yml");

        if (!file.exists())
            instance.saveResource("messages.yml",false);

        msgConfig = YamlConfiguration.loadConfiguration(file);

        return msgConfig;
    }

    public boolean dmgRes = false;
    public void dmgCheck() {
        if (instance.getConfig().getBoolean("Utils.fly-command.damage.enable", false)
                && instance.getConfig().getInt("Utils.fly-command.damage.interval", 3) == 0) {
            Bukkit.getConsoleSender().sendMessage(Color.of("&cYou have enabled damage for elytra flying, but you set damage-interval to 0!"));
            dmgRes = true;
        }
        if (instance.getConfig().getBoolean("Utils.fly-command.damage.enable", false)
                && instance.getConfig().getInt("Utils.fly-command.damage.power", 3) == 0) {
            Bukkit.getConsoleSender().sendMessage(Color.of("&cYou have enabled damage for elytra flying, but you set damage-power to 0!"));
            dmgRes = true;
        }
    }
}