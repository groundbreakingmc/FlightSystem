package noslowdwn.flightsystem.cmds;

import noslowdwn.flightsystem.utils.Config;
import noslowdwn.flightsystem.utils.Parser;
import noslowdwn.flightsystem.handlers.Damager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;

import static noslowdwn.flightsystem.FlightSystem.instance;

public class Manager implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // /fly <on/off/reload/help/switch> <player>

        if (args.length == 0) {
            if (sender instanceof Player) {
                if (!((Player )sender).getAllowFlight()) {
                    on(sender);
                } else {
                    off(sender);
                }
            } else {
                sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.player-only")));
            }
        }

        else if (args.length == 1) {
            switch (args[0]) {
                case "on":
                    on(sender);
                    break;
                case "off":
                    off(sender);
                    break;
                case "reload":
                    reloadPlugin(sender);
                    break;
                case "help":
                    help(sender);
                    break;
                default:
                    if (sender instanceof Player) {
                        if (sender.hasPermission("flightsystem.fly.on")
                                || sender.hasPermission("flightsystem.fly.off")
                                || sender.hasPermission("flightsystem.help.admin")
                                || sender.hasPermission("flightsystem.help.staff")
                                || sender.hasPermission("flightsystem.help.user")
                                || sender.hasPermission("flightsystem.reload")) {
                            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.usage-error")));
                        } else {
                            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.no-permission")));
                        }
                    } else {
                        sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.usage-error")));
                    }
            }
        }

        else if (args.length == 2) {
            switch (args[0]) {
                case "on":
                    if (sender instanceof Player) {
                        if (sender == Bukkit.getPlayer(args[1]))
                            on(sender);
                        else {
                            if (sender.hasPermission("flightsystem.fly.on.other"))
                                on(sender, args[1]);
                            else
                                sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.no-permission")));
                        }
                    } else
                        on(sender, args[1]);
                    break;
                case "off":
                    if (sender instanceof Player) {
                        if (sender == Bukkit.getPlayer(args[1])) {
                            off(sender);
                        } else {
                            if (sender.hasPermission("flightsystem.fly.off.other")) {
                                off(sender, args[1]);
                            } else {
                                sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.no-permission")));
                            }
                        }
                    } else {
                        off(sender, args[1]);
                    }
                    break;
                case "all":
                    if (args[1].equalsIgnoreCase("on")) {
                        onAll(sender);
                    } else if (args[1].equalsIgnoreCase("off")) {
                        offAll(sender);
                    } else if (args[1].equalsIgnoreCase("switch")) {
                        switchAll(sender);
                    }
                    break;
                case "switch":
                    fSwitch(sender, args[1]);
                    break;
                default:
                    sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.usage-error")));
            }
        } else {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.usage-error")));
        }

        return true;
    }

    private void on(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.player-only")));
            return;
        }

        if (!sender.hasPermission("flightsystem.fly.on")) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.no-permission")));
            return;
        }

        if (((Player) sender).getAllowFlight()) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.also-on")));
            return;
        }

        if (!instance.getConfig().getStringList("utils.flight-prevent.disabled-worlds").isEmpty()
                && !sender.hasPermission("flightsystem.bypass.prevention.fly")) {
            for (String blocked : instance.getConfig().getStringList("utils.flight-prevent.disabled-worlds")) {
                if (((Player) sender).getPlayer().getWorld().equals(Bukkit.getWorld(blocked))) {
                    sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-prevent.command-on-self")));
                    return;
                }
            }
        }

        if (instance.getConfig().getBoolean("utils.flight.elytra-only", true)) {
            ItemStack chestplate = ((Player) sender).getInventory().getChestplate();
            if (!sender.hasPermission("flightsystem.bypass.elytra")
                    && chestplate != null && chestplate.getType() == Material.ELYTRA) {
                if (!instance.getConfig().getBoolean("utils.flight.fly-with-broken", false)
                        && !sender.hasPermission("flightsystem.bypass.broken")
                        && chestplate.getDurability() >= chestplate.getData().getItemType().getMaxDurability()) {
                    sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.elytra-glide.broken")));
                    return;
                }
            } else {
                sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.elytra-glide.only")));
                return;
            }
        }

        sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.self-on")));
        ((Player) sender).setAllowFlight(true);
    }

    private void on(CommandSender sender, String arg) {
        Player target = Bukkit.getPlayer(arg);
        if (target == null) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.player-not-found")
                    .replace("%player%", arg)));
            return;
        }

        if (target.getAllowFlight()) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.also-on-other")
                    .replace("%player%", target.getPlayer().getName())));
            return;
        }

        if (!instance.getConfig().getStringList("utils.disabled-worlds").isEmpty()
                && !target.hasPermission("flightsystem.bypass.prevention.fly")) {
            for (String blocked : instance.getConfig().getStringList("utils.disabled-worlds")) {
                if (target.getPlayer().getWorld().equals(Bukkit.getWorld(blocked))) {
                    sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-prevent.command-on-another")
                            .replace("%player%", target.getPlayer().getName())));
                    return;
                }
            }
        }

        target.setAllowFlight(true);
        sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.on-other")
                .replace("%player%", target.getPlayer().getName())));

        if (sender instanceof Player) {
            target.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.on-by-other")
                    .replace("%player%", ((Player) sender).getPlayer().getName())));
        } else {
            target.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.on-by-other")
                    .replace("%player%", Config.getMessage().getString("messages.flight-switch.console"))));
        }
    }

    private void onAll(CommandSender sender) {

        if(sender instanceof Player && instance.getConfig().getBoolean("utils.n-all-console-only")) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.console-only")));
            return;
        }

        if (sender instanceof Player && !sender.hasPermission("flightsystem.fly.on.all")) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.no-permission")));
            return;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getAllowFlight()) {
                continue;
            }

            target.setAllowFlight(true);
            if (sender instanceof Player) {
                target.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.on-by-other")
                        .replace("%player%", ((Player) sender).getPlayer().getName())));
            } else {
                target.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.on-by-other")
                        .replace("%player%", Config.getMessage().getString("messages.flight-switch.console"))));
            }
        }

        sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.on-for-all")));
    }

    private void off(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.player-only")));
            return;
        }

        if (!sender.hasPermission("flightsystem.fly.off")) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.no-permission")));
            return;
        }

        if (!((Player) sender).getAllowFlight()) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.also-off")));
            return;
        }

        sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.self-off")));
        ((Player) sender).setAllowFlight(false);
    }

    private void off(CommandSender sender, String arg) {
        Player target = Bukkit.getPlayer(arg);
        if (target == null) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.player-not-found")
                    .replace("%player%", arg)));
            return;
        }

        if (!target.getAllowFlight()) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.also-off-other")
                    .replace("%player%", target.getPlayer().getName())));
            return;
        }

        sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.off-other")
                .replace("%player%", arg)));
        if (sender instanceof Player) {
            target.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.off-by-other")
                    .replace("%player%", ((Player) sender).getPlayer().getName())));
        } else {
            target.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.off-by-other")
                    .replace("%player%", Config.getMessage().getString("messages.flight-switch.console"))));
        }

        target.setAllowFlight(false);
    }

    private void offAll(CommandSender sender) {

        if(sender instanceof Player && instance.getConfig().getBoolean("utils.turn-all-console-only")) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.console-only")));
            return;
        }

        if (sender instanceof Player && !sender.hasPermission("flightsystem.fly.off.all")) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.no-permission")));
            return;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.getAllowFlight()) {
                continue;
            }

            target.setAllowFlight(false);
            if (sender instanceof Player) {
                target.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.off-by-other")
                        .replace("%player%", ((Player )sender).getPlayer().getName())));
            } else {
                target.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.off-by-other")
                        .replace("%player%", Config.getMessage().getString("messages.flight-switch.console"))));
            }
        }
        sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.off-for-all")));
    }

    private void fSwitch(CommandSender sender, String arg) {
        if (sender instanceof Player && !sender.hasPermission("flightsystem.fly.switch.other")) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.no-permission")));
            return;
        }

        Player target = Bukkit.getPlayer(arg);
        if (target == null) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.player-not-found")
                    .replace("%player%", arg)));
            return;
        }

        if (sender instanceof Player && ((Player) sender).getPlayer() == target) {
            if (((Player) sender).getPlayer().getAllowFlight()) {
                off(((Player) sender).getPlayer());
            } else {
                on(((Player) sender).getPlayer());
            }
        } else {
            if (target.getAllowFlight()) {
                off(sender, arg);
            } else {
                on(sender, arg);
            }
        }
    }

    private void switchAll(CommandSender sender) {
        if (sender instanceof Player && !sender.hasPermission("flightsystem.fly.switch.all")) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.no-permission")));
            return;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.getAllowFlight()){
                target.setAllowFlight(true);
                if (sender instanceof Player) {
                    target.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.on-by-other")
                            .replace("%player%", ((Player) sender).getPlayer().getName())));
                } else {
                    target.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.on-by-other")
                            .replace("%player%", Config.getMessage().getString("messages.flight-switch.console"))));
                }
            } else {
                target.setAllowFlight(false);
                if (sender instanceof Player) {
                    target.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.off-by-other")
                            .replace("%player%", ((Player) sender).getPlayer().getName())));
                } else {
                    target.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.off-by-other")
                            .replace("%player%", Config.getMessage().getString("messages.flight-switch.console"))));
                }
            }
        }
        sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.flight-switch.switch-for-all")));
    }

    private void help(CommandSender sender) {
        if (sender instanceof Player) {
            if (sender.hasPermission("flightsystem.other.help.admin")) {
                for (String line : Config.getMessage().getStringList("messages.other.help.admin")) {
                    sender.sendMessage(Parser.hex(sender, line));
                }
            } else if (sender.hasPermission("flightsystem.other.help.staff")) {
                for (String line : Config.getMessage().getStringList("messages.other.help.staff")) {
                    sender.sendMessage(Parser.hex(sender, line));
                }
            } else if (sender.hasPermission("flightsystem.other.help.user")) {
                for (String line : Config.getMessage().getStringList("messages.other.help.player")) {
                    sender.sendMessage(Parser.hex(sender, line));
                }
            } else {
                sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.no-permission")));
            }
        } else {
            for (String line : Config.getMessage().getStringList("messages.other.help.console")) {
                sender.sendMessage(Parser.hex(sender, line));
            }
        }
    }

    private void reloadPlugin(CommandSender sender) {
        if (sender instanceof Player && !sender.hasPermission("flightsystem.reload")) {
            sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.no-permission")));
            return;
        }

        instance.reloadConfig();

        File messagesFile = new File(instance.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            instance.saveResource("messages.yml", false);
        }

        Config.msgConfig = YamlConfiguration.loadConfiguration(messagesFile);

        if (instance.dmgTask != null) {
            instance.dmgTask.cancel();
        }
        if (instance.getConfig().getBoolean("utils.flight.damage.enable", false)) {
            instance.dmgTask = new Damager().runTaskTimerAsynchronously(instance, 0L,
                    instance.getConfig().getInt("utils.flight.damage.interval", 3) * 20L);
        }

        sender.sendMessage(Parser.hex(sender, Config.getMessage().getString("messages.other.reload")));
    }
}