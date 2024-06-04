package elytrafly.noslowdwn.Commands;

import elytrafly.noslowdwn.Utils.Color;
import elytrafly.noslowdwn.Utils.Config;
import elytrafly.noslowdwn.Utils.fly.Damager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;

import static elytrafly.noslowdwn.ElytraFly.instance;

public class Manager implements CommandExecutor {

    private Player sender;

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {

        // /fly <on/off/reload/help/switch> <player>
        if (commandSender instanceof Player)
            sender = (Player) commandSender;

        if (args.length == 0) {
            if (commandSender instanceof Player) {
                if (!sender.getAllowFlight())
                    on(sender);
                else
                    off(sender);
            }
            else
                commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.player-only")));
        }

        else if (args.length == 1) {
            switch (args[0]) {
                case "on":
                    on(commandSender);
                    break;
                case "off":
                    off(commandSender);
                    break;
                case "reload":
                    reloadPlugin(commandSender);
                    break;
                case "help":
                    help(commandSender);
                    break;
                default:
                    if (commandSender instanceof Player) {
                        if (sender.hasPermission("elytrafly.fly.on")
                                || sender.hasPermission("elytrafly.fly.off")
                                || sender.hasPermission("elytrafly.help.admin")
                                || sender.hasPermission("elytrafly.help.staff")
                                || sender.hasPermission("elytrafly.help.user")
                                || sender.hasPermission("elytrafly.reload"))
                            sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.usage-error")));
                        else
                            sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.no-permission")));
                    } else
                        commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.usage-error")));
            }
        }

        else if (args.length == 2) {
            switch (args[0]) {
                case "on":
                    if (commandSender instanceof Player) {
                        if (sender == Bukkit.getPlayer(args[1]))
                            on(sender);
                        else {
                            if (sender.hasPermission("elytrafly.fly.on.other"))
                                on(commandSender, args[1]);
                            else
                                sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.no-permission")));
                        }
                    } else
                        on(commandSender, args[1]);
                    break;
                case "off":
                    if (commandSender instanceof Player) {
                        if (sender == Bukkit.getPlayer(args[1]))
                            off(sender);
                        else {
                            if (sender.hasPermission("elytrafly.fly.off.other"))
                                off(commandSender, args[1]);
                            else
                                sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.no-permission")));
                        }
                    } else
                        off(commandSender, args[1]);
                    break;
                case "all":
                    if (args[1].equalsIgnoreCase("on"))
                        onAll(commandSender);
                    if (args[1].equalsIgnoreCase("off"))
                        offAll(commandSender);
                    if (args[1].equalsIgnoreCase("switch"))
                        switchAll(commandSender);
                    break;
                case "switch":
                    fSwitch(commandSender, args[1]);
                    break;
                default:
                    commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.usage-error")));
            }
        } else
            commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.usage-error")));

        return true;
    }

    private void on(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.player-only")));
            return;
        }

        if (!sender.hasPermission("elytrafly.fly.on")) {
            sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.no-permission")));
            return;
        }

        if (sender.getAllowFlight()) {
            sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.also-on")));
            return;
        }

        if (!instance.getConfig().getStringList("Utils.flight-prevent.disabled-worlds").isEmpty()
                && !sender.hasPermission("elytrafly.bypass.prevention.fly")) {
            for (String blocked : instance.getConfig().getStringList("Utils.flight-prevent.disabled-worlds")) {
                if (sender.getPlayer().getWorld().equals(Bukkit.getWorld(blocked))) {
                    sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-prevent.command-on-self")));
                    return;
                }
            }
        }

        if (instance.getConfig().getBoolean("Utils.fly-command.elytra-only", true)) {
            ItemStack chestplate = sender.getInventory().getChestplate();
            if (!sender.hasPermission("elytrafly.bypass.elytra")
                    && chestplate != null && chestplate.getType() == Material.ELYTRA) {
                if (!instance.getConfig().getBoolean("Utils.fly-command.fly-with-broken", false)
                        && !sender.hasPermission("elytrafly.bypass.broken")
                        && chestplate.getDurability() >= chestplate.getData().getItemType().getMaxDurability()) {
                    sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.elytra-require.broken")));
                    return;
                }
            } else {
                sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.elytra-require.only")));
                return;
            }
        }

        sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.self-on")));
        sender.setAllowFlight(true);
    }

    private void on(CommandSender commandSender, String arg) {
        Player target = Bukkit.getPlayer(arg);
        if (target == null) {
            commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.player-not-found")
                    .replace("%player%", arg)));
            return;
        }

        if (target.getAllowFlight()) {
            commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.also-on-other")
                    .replace("%player%", target.getPlayer().getName())));
            return;
        }

        if (!instance.getConfig().getStringList("settings.disabled-worlds").isEmpty()
                && !target.hasPermission("elytrafly.bypass.prevention.fly")) {
            for (String blocked : instance.getConfig().getStringList("settings.disabled-worlds")) {
                if (target.getPlayer().getWorld().equals(Bukkit.getWorld(blocked))) {
                    commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-prevent.command-on-another")
                            .replace("%player%", target.getPlayer().getName())));
                    return;
                }
            }
        }

        target.setAllowFlight(true);
        commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.on-other")
                .replace("%player%", target.getPlayer().getName())));

        if (commandSender instanceof Player)
            target.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.on-by-other")
                    .replace("%player%", ((Player) commandSender).getPlayer().getName())));
        else
            target.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.on-by-other")
                    .replace("%player%", Config.getMessagesConfig().getString("Messages.flight-switch.console"))));
    }

    private void onAll(CommandSender commandSender) {

        if(commandSender instanceof Player && instance.getConfig().getBoolean("settings.turn-all-console-only")) {
            sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.console-only")));
            return;
        }

        if (commandSender instanceof Player && !sender.hasPermission("elytrafly.fly.on.all")) {
            sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.no-permission")));
            return;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getAllowFlight())
                continue;

            target.setAllowFlight(true);
            if (commandSender instanceof Player)
                target.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.on-by-other")
                        .replace("%player%", sender.getPlayer().getName())));
            else
                target.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.on-by-other")
                        .replace("%player%", Config.getMessagesConfig().getString("Messages.flight-switch.console"))));
        }

        commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.on-for-all")));
    }

    private void off(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.player-only")));
            return;
        }

        if (!sender.hasPermission("elytrafly.fly.off")) {
            sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.no-permission")));
            return;
        }

        if (!sender.getAllowFlight()) {
            sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.also-off")));
            return;
        }

        sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.self-off")));
        sender.setAllowFlight(false);
    }

    private void off(CommandSender commandSender, String arg) {
        Player target = Bukkit.getPlayer(arg);
        if (target == null) {
            commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.player-not-found")
                    .replace("%player%", arg)));
            return;
        }

        if (!target.getAllowFlight()) {
            commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.also-off-other")
                    .replace("%player%", target.getPlayer().getName())));
            return;
        }

        commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.off-other")
                .replace("%player%", arg)));
        if (commandSender instanceof Player)
            target.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.off-by-other")
                    .replace("%player%", sender.getPlayer().getName())));
        else
            target.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.off-by-other")
                    .replace("%player%", Config.getMessagesConfig().getString("Messages.flight-switch.console"))));

        target.setAllowFlight(false);
    }

    private void offAll(CommandSender commandSender) {

        if(commandSender instanceof Player && instance.getConfig().getBoolean("settings.turn-all-console-only")) {
            sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.console-only")));
            return;
        }

        if (commandSender instanceof Player && !sender.hasPermission("elytrafly.fly.off.all")) {
            sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.no-permission")));
            return;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.getAllowFlight())
                continue;

            target.setAllowFlight(false);
            if (commandSender instanceof Player)
                target.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.off-by-other")
                        .replace("%player%", sender.getPlayer().getName())));
            else
                target.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.off-by-other")
                        .replace("%player%", Config.getMessagesConfig().getString("Messages.flight-switch.console"))));
        }
        commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.off-for-all")));
    }

    private void fSwitch(CommandSender commandSender, String arg) {
        if (commandSender instanceof Player && !sender.hasPermission("elytrafly.fly.switch.other")) {
            sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.no-permission")));
            return;
        }

        Player target = Bukkit.getPlayer(arg);
        if (target == null) {
            commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.player-not-found")
                    .replace("%player%", arg)));
            return;
        }

        if (commandSender instanceof Player && ((Player) commandSender).getPlayer() == target) {
            if (((Player) commandSender).getPlayer().getAllowFlight())
                off(((Player) commandSender).getPlayer());
            else
                on(((Player) commandSender).getPlayer());
        } else {
            if (target.getAllowFlight())
                off(commandSender, arg);
            else
                on(commandSender, arg);
        }
    }

    private void switchAll(CommandSender commandSender) {
        if (commandSender instanceof Player && !sender.hasPermission("elytrafly.fly.switch.all")) {
            sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.no-permission")));
            return;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.getAllowFlight()){
                target.setAllowFlight(true);
                if (commandSender instanceof Player)
                    target.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.on-by-other")
                            .replace("%player%", sender.getPlayer().getName())));
                else
                    target.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.on-by-other")
                            .replace("%player%", Config.getMessagesConfig().getString("Messages.flight-switch.console"))));
            } else {
                target.setAllowFlight(false);
                if (commandSender instanceof Player)
                    target.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.off-by-other")
                            .replace("%player%", sender.getPlayer().getName())));
                else
                    target.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.off-by-other")
                            .replace("%player%", Config.getMessagesConfig().getString("Messages.flight-switch.console"))));
            }
        }
        commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-switch.switch-for-all")));
    }

    private void help(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            if (sender.hasPermission("elytrafly.other.help.admin")) {
                for (String line : Config.getMessagesConfig().getStringList("Messages.other.help.admin"))
                    sender.sendMessage(Color.of(line));
            }
            else if (sender.hasPermission("elytrafly.other.help.staff")) {
                for (String line : Config.getMessagesConfig().getStringList("Messages.other.help.staff"))
                    sender.sendMessage(Color.of(line));
            }
            else if (sender.hasPermission("elytrafly.other.help.user")) {
                for (String line : Config.getMessagesConfig().getStringList("Messages.other.help.player"))
                    sender.sendMessage(Color.of(line));
            }
            else
                sender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.no-permission")));
        } else {
            for (String line : Config.getMessagesConfig().getStringList("Messages.other.help.console"))
                commandSender.sendMessage(Color.of(line));
        }
    }

    private void reloadPlugin(CommandSender commandSender) {
        if (commandSender instanceof Player && !commandSender.hasPermission("elytrafly.reload")) {
            commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.no-permission")));
            return;
        }

        instance.reloadConfig();

        File messagesFile = new File(instance.getDataFolder(), "messages.yml");
        if (!messagesFile.exists())
            instance.saveResource("messages.yml", false);

        Config.msgConfig = YamlConfiguration.loadConfiguration(messagesFile);

        if (instance.dmgTask != null)
            instance.dmgTask.cancel();
        if (instance.getConfig().getBoolean("Utils.fly-command.damage.enable", false))
            instance.dmgTask = new Damager().runTaskTimerAsynchronously(instance, 0L,
                    instance.getConfig().getInt("Utils.fly-command.damage.interval", 3) * 20L);

        commandSender.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.other.reload")));
    }
}