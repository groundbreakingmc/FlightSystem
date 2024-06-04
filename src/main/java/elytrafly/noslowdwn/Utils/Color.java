package elytrafly.noslowdwn.Utils;

import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Color {

    public static String of(String message) {
        if (message == null) {
            System.out.println(ChatColor.RED + "[ElytraFly] Error message parsing!");
            System.out.println(ChatColor.RED + "[ElytraFly] Please check your messages.yml to find error!");
            System.out.println(ChatColor.RED + "[ElytraFly] You can also check syntax on https://yamlchecker.com/!");
            System.out.println(ChatColor.RED + "[ElytraFly] Or just delete 'messages.yml' and reload plugin!");
            return ChatColor.translateAlternateColorCodes('&',
                    "&c[ElytraFly] Error message parsing! Please contact administrator or check console!");
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

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
