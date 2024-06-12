package noslowdwn.flightsystem.handlers;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import noslowdwn.flightsystem.utils.Parser;
import noslowdwn.flightsystem.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static noslowdwn.flightsystem.FlightSystem.instance;

public class Prevention implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {

            World bworld;

            if (p.isGliding() && instance.getConfig().getBoolean("utils.elytra-glide.enable", false)
                    && !p.hasPermission("flightsystem.bypass.prevention.elytra")) {

                for (String bWorld : instance.getConfig().getStringList("utils.elytra-glide.disabled-worlds")) {
                    if ((bworld = Bukkit.getWorld(bWorld)) != null && event.getPlayer().getWorld().equals(bworld)) {

                        p.setGliding(false);

                        if (instance.getConfig().getBoolean("utils.elytra-glide.send-title-and-subtitle", true))
                            p.sendTitle(Parser.hex(p, Config.getMessage().getString("messages.elytra-glide.title")),
                                    Parser.hex(p, Config.getMessage().getString("messages.elytra-glide.subtitle")), 0, 40, 20);

                        if (instance.getConfig().getBoolean("utils.elytra-glide.send-actionbar", true)) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                                    Parser.hex(p, Config.getMessage().getString("messages.elytra-glide.actionbar-message"))));
                        }

                        if (instance.getConfig().getBoolean("utils.elytra-glide.send-chat-message", false)) {
                            p.sendMessage(Parser.hex(p, Config.getMessage().getString("messages.elytra-glide.chat-message")));
                        }

                        break;
                    }
                }
            }

            if (p.isFlying() && instance.getConfig().getBoolean("utils.flight-prevent.enable", false)
                    && !p.hasPermission("flightsystem.bypass.prevention.fly")) {
                for (String bWorld : instance.getConfig().getStringList("utils.flight-prevent.disabled-worlds")) {
                    if ((bworld = Bukkit.getWorld(bWorld)) != null && event.getPlayer().getWorld().equals(bworld)) {

                        p.setAllowFlight(false);

                        if (instance.getConfig().getBoolean("utils.flight-prevent.send-title-and-subtitle", true)) {
                            p.sendTitle(Parser.hex(p, Config.getMessage().getString("messages.flight-prevent.title")),
                                    Parser.hex(p, Config.getMessage().getString("messages.flight-prevent.subtitle")), 0, 40, 20);
                        }

                        if (instance.getConfig().getBoolean("utils.flight-prevent.send-actionbar", true)) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                                    Parser.hex(p, Config.getMessage().getString("messages.flight-prevent.actionbar-message"))));
                        }

                        if (instance.getConfig().getBoolean("utils.flight-prevent.send-chat-message", false)) {
                            p.sendMessage(Parser.hex(p, Config.getMessage().getString("messages.flight-prevent.chat-message")));
                        }

                        break;
                    }
                }
            }
        }
    }
}