package noslowdwn.flightsystem.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static noslowdwn.flightsystem.FlightSystem.instance;

public class UpdateChecker implements Listener {
    
    private static final String VERSION_URL = "https://raw.githubusercontent.com/noslowdwn/FlightSystem/master/version";
    private static Boolean new_version = false;
    private static String latestVersion, downloadLink;

    public static void checkVersion() {
        if(instance.getConfig().getBoolean("check-updates", false)) {
            Bukkit.getConsoleSender().sendMessage(Parser.hex(null, "&6[FlightSystem] Checking for updates..."));

            try {
                URL url = new URL(VERSION_URL);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    content.append(line);
                }
                in.close();
                connection.disconnect();

                String[] versionInfo = content.toString().split("->");
                if (versionInfo.length != 2) {
                    Bukkit.getConsoleSender().sendMessage(Parser.hex(null, "&6[FlightSystem] Got version from server is invalid!"));
                    return;
                }

                latestVersion = versionInfo[0].trim();
                downloadLink = versionInfo[1].trim();

                if (instance.getDescription().getVersion().equals(latestVersion)) {
                    Bukkit.getConsoleSender().sendMessage(Parser.hex(null, "&c[FlightSystem] No updates were found!"));
                } else {
                    new_version = true;
                    Bukkit.getConsoleSender().sendMessage(Parser.hex(null, "&f=========== FlightSystem ==========="));
                    Bukkit.getConsoleSender().sendMessage(Parser.hex(null, "&fCurrent version: &7" + instance.getDescription().getVersion()));
                    Bukkit.getConsoleSender().sendMessage(Parser.hex(null, "&fNew version: &a" + latestVersion));
                    Bukkit.getConsoleSender().sendMessage(Parser.hex(null, "&fDownload link: &7" + downloadLink));
                    Bukkit.getConsoleSender().sendMessage(Parser.hex(null, "&f===================================="));
                }
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(Parser.hex(null, "&c[FlightSystem] Failed to check for updates: " + e.getMessage()));
            }
        }
    }

    @EventHandler
    private void onJoinNotification(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> {
            if ((p.isOp() || p.hasPermission("flightsystem.updates")) && new_version) {
                p.sendMessage("");
                p.sendMessage(Parser.hex(null, "&6[FlightSystem] &aWas found an update!"));
                p.sendMessage(Parser.hex(null, "&fCurrent version: &7" + instance.getDescription().getVersion()));
                p.sendMessage(Parser.hex(null, "&fNew version: &a" + latestVersion));
                p.sendMessage(Parser.hex(null, "&fDownload link: &7" + downloadLink));
                p.sendMessage("");
            }
        }, 100L);
    }
}
