package elytrafly.noslowdwn.Utils.fly;

import elytrafly.noslowdwn.Utils.Config;
import elytrafly.noslowdwn.Utils.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static elytrafly.noslowdwn.ElytraFly.instance;

public class UnequipChecker implements Listener {

    @EventHandler
    public void clickEvent(InventoryClickEvent event) {
        Player ePlayer = (Player)event.getWhoClicked();
        if (instance.getConfig().getBoolean("Utils.fly-command.disable-if-unequiped")
                && !ePlayer.hasPermission("elytrafly.bypass")
                && ePlayer.getAllowFlight()
                && event.getRawSlot() == 6) {
            Material currentItemMaterial = event.getCurrentItem().getType();
            if (currentItemMaterial == Material.ELYTRA) {
                ePlayer.setAllowFlight(false);
                ePlayer.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.elytra-require.unequipped")));
            }
        }
    }
}