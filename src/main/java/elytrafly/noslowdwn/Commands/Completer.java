package elytrafly.noslowdwn.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static elytrafly.noslowdwn.ElytraFly.instance;

public class Completer implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {

        List<String> completions = new ArrayList<>();
        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;
            if (args.length == 1) {
                if (sender.hasPermission("spacefly.fly.on") || sender.hasPermission("spacefly.fly.on.other"))
                    completions.add("on");

                if (sender.hasPermission("spacefly.fly.off") || sender.hasPermission("spacefly.fly.off.other"))
                    completions.add("off");

                if (sender.hasPermission("spacefly.fly.switch.other"))
                    completions.add("switch");

                if ((sender.hasPermission("spacefly.fly.on.all") || sender.hasPermission("spacefly.fly.off.all"))
                        && !instance.getConfig().getBoolean("settings.turn-all-console-only"))
                    completions.add("all");

                if (sender.hasPermission("spacefly.help.admin") || sender.hasPermission("spacefly.help.staff")
                        || sender.hasPermission("spacefly.help.player"))
                    completions.add("help");

                if (sender.hasPermission("spacefly.reload"))
                    completions.add("reload");
            }
            if (args.length == 2) {
                if ((args[0].equalsIgnoreCase("all") && sender.hasPermission("spacefly.fly.on.all"))
                        && !instance.getConfig().getBoolean("settings.turn-all-console-only"))
                    completions.add("on");
                if ((args[0].equalsIgnoreCase("all") && sender.hasPermission("spacefly.fly.off.all"))
                        && !instance.getConfig().getBoolean("settings.turn-all-console-only"))
                    completions.add("off");
                if (args[0].equalsIgnoreCase("all") && sender.hasPermission("spacefly.fly.switch.all"))
                    completions.add("switch");

                if ((sender.hasPermission("spacefly.fly.on.other")
                        || sender.hasPermission("spacefly.fly.off.other")
                        || sender.hasPermission("spacefly.fly.switch.other"))
                        && (args[0].equalsIgnoreCase("on")
                        || args[0].equalsIgnoreCase("off")
                        || args[0].equalsIgnoreCase("switch")))
                    return null;
                else {
                    if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))
                        return Collections.singletonList(sender.getName());
                }
            }
        } else {
            if (args.length == 1) {
                completions.add("on");
                completions.add("off");
                completions.add("switch");
                completions.add("all");
                completions.add("help");
                completions.add("reload");
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("all")) {
                    completions.add("on");
                    completions.add("off");
                    completions.add("switch");
                }
                if (args[0].equalsIgnoreCase("on")
                        || args[0].equalsIgnoreCase("off")
                        || args[0].equalsIgnoreCase("switch"))
                    return null;
            }
        }
        return completions;
    }
}