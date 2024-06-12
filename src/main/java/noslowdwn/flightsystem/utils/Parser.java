package noslowdwn.flightsystem.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static String hex(CommandSender sender, String message) {
        if (message == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[FlightSystem] Error message parsing!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[FlightSystem] Please check your messages.yml to find error!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[FlightSystem] You can also check syntax on https://yamlchecker.com/!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[FlightSystem] Or just delete 'messages.yml' and reload plugin!");
            message = "&c[FlightSystem] Error message parsing! Please contact administrator or check console!";
        }

        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        int subVersion = Integer.parseInt(version.replace("1_", "").replaceAll("_R\\d", "").replace("v", ""));
        if (subVersion >= 16) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                StringBuilder replacement = new StringBuilder("x");
                for (char c : color.substring(1).toCharArray())
                    replacement.append('\u00A7').append(c);
                message = message.replace(color, replacement.toString());
                matcher = pattern.matcher(message);
            }
        }

        return papi((Player) sender, ChatColor.translateAlternateColorCodes('&', message));
    }

    public static String papi(Player p, String message) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(p, message);
        } else {
            return message;
        }
    }

}
