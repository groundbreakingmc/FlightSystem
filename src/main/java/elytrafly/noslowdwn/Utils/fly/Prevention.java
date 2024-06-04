package elytrafly.noslowdwn.Utils.fly;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import elytrafly.noslowdwn.Utils.Color;
import elytrafly.noslowdwn.Utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static elytrafly.noslowdwn.ElytraFly.instance;

public class Prevention implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
            if (player.isGliding() && instance.getConfig().getBoolean("Utils.elytra-prevent.enable", false)
                    && !player.hasPermission("elytrafly.bypass.prevention.elytra")) {

                for (String bWorld : instance.getConfig().getStringList("Utils.elytra-prevent.disabled-worlds")) {
                    World bworld = Bukkit.getWorld(bWorld);
                    if (event.getPlayer().getWorld().equals(bworld)) {
                        player.setGliding(false);

                        if (instance.getConfig().getBoolean("Utils.elytra-prevent.send-title-and-subtitle", true))
                            player.sendTitle(Color.of(Config.getMessagesConfig().getString("Messages.elytra-prevent.title")),
                                    Color.of(Config.getMessagesConfig().getString("Messages.elytra-prevent.subtitle")), 0, 40, 20);

                        if (instance.getConfig().getBoolean("Utils.elytra-prevent.send-actionbar", true))
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                                    Color.of(Config.getMessagesConfig().getString("Messages.elytra-prevent.actionbar-message"))));

                        if (instance.getConfig().getBoolean("Utils.elytra-prevent.send-chat-message", false))
                            player.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.elytra-prevent.chat-message")));
                        break;
                    }
                }
            }

            if (player.isFlying() && instance.getConfig().getBoolean("Utils.flight-prevent.enable", false)
                    && !player.hasPermission("elytrafly.bypass.prevention.fly")) {
                for (String bWorld : instance.getConfig().getStringList("Utils.flight-prevent.disabled-worlds")) {
                    World bworld = Bukkit.getWorld(bWorld);
                    if (event.getPlayer().getWorld().equals(bworld)) {
                        player.setAllowFlight(false);
                        if (instance.getConfig().getBoolean("Utils.flight-prevent.send-title-and-subtitle", true))
                            player.sendTitle(Color.of(Config.getMessagesConfig().getString("Messages.flight-prevent.title")),
                                    Color.of(Config.getMessagesConfig().getString("Messages.flight-prevent.subtitle")), 0, 40, 20);
                        if (instance.getConfig().getBoolean("Utils.flight-prevent.send-actionbar", true))
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                                    Color.of(Config.getMessagesConfig().getString("Messages.flight-prevent.actionbar-message"))));
                        if (instance.getConfig().getBoolean("Utils.flight-prevent.send-chat-message", false))
                            player.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.flight-prevent.chat-message")));
                        break;
                    }
                }
            }
        }
    }
}