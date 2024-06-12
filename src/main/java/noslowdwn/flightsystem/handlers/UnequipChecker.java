package noslowdwn.flightsystem.handlers;

import noslowdwn.flightsystem.utils.Config;
import noslowdwn.flightsystem.utils.Parser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static noslowdwn.flightsystem.FlightSystem.instance;

public class UnequipChecker implements Listener {

    @EventHandler
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!p.hasPermission("flightsystem.bypass.unequip")
                && p.getAllowFlight()
                && e.getRawSlot() == 6) {
            if (p.isGliding() && instance.getConfig().getBoolean("utils.elytra-glide.disable-if-unequiped")) {
                e.setCancelled(true);
                p.sendMessage(Parser.hex(p, Config.getMessage().getString("messages.elytra-glide.unequip-not-allowed")));
            } else if (p.isFlying() && instance.getConfig().getBoolean("utils.flight.disable-if-unequiped")) {
                e.setCancelled(true);
                p.sendMessage(Parser.hex(p, Config.getMessage().getString("messages.other.unequip-not-allowed")));
            } else if (instance.getConfig().getBoolean("utils.flight.disable-if-unequiped")) {
                Material currentItemMaterial = e.getCurrentItem().getType();
                if (currentItemMaterial == Material.ELYTRA) {
                    p.setAllowFlight(false);
                    p.sendMessage(Parser.hex(p, Config.getMessage().getString("messages.elytra-glide.unequipped")));
                }
            }
        }
    }
}