package noslowdwn.flightsystem.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static noslowdwn.flightsystem.FlightSystem.instance;

public class Config {

    public static FileConfiguration msgConfig;

    public static void load(String name) {
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

    public static FileConfiguration getMessage() {
        File file = new File(instance.getDataFolder(), "messages.yml");

        if (!file.exists())
            instance.saveResource("messages.yml",false);

        msgConfig = YamlConfiguration.loadConfiguration(file);

        return msgConfig;
    }

    public static boolean dmgRes = false;
    public static void dmgCheck() {
        if (instance.getConfig().getBoolean("utils.flight.damage.enable", false)
                && instance.getConfig().getInt("utils.flight.damage.interval", 3) == 0) {
            Bukkit.getConsoleSender().sendMessage(Parser.hex(null ,"&c[FlightSystem] You have enabled damage for elytra flying, but damage-interval is 0!"));
            dmgRes = true;
        }
        if (instance.getConfig().getBoolean("utils.flight.damage.enable", false)
                && instance.getConfig().getInt("utils.flight.damage.power", 3) == 0) {
            Bukkit.getConsoleSender().sendMessage(Parser.hex(null, "&c[FlightSystem] You have enabled damage for elytra flying, but damage-power is 0!"));
            dmgRes = true;
        }
    }


    public static void checkVersion(String name, double version) {

        File file = new File(instance.getDataFolder(), name);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String[] fname = name.split("\\.", 2);

        if (config.getDouble("config-version") != version || !config.getKeys(false).contains("config-version")) {

            File backupFolder = new File(instance.getDataFolder(), fname[0] + "-backups");
            if (!backupFolder.exists() && !backupFolder.mkdirs()) {
                Bukkit.getConsoleSender().sendMessage(Parser.hex(null, "&c[VoidFall] Error! Failed to create " + fname[0] + "-backups folder!"));
                return;
            }

            int backupNumber = backupFolder.listFiles().length;
            File backupFile = new File(backupFolder, (fname[0] + "_backup_" + backupNumber + fname[1]));
            if (file.renameTo(backupFile)) {
                instance.saveResource(name, true);
            } else {
                Bukkit.getConsoleSender().sendMessage(Parser.hex(null, "&cYour configuration file is old, but &ncreate new is not possible&c."));
            }

            load(name);
        }
    }
}