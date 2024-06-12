package noslowdwn.flightsystem.handlers;

import noslowdwn.flightsystem.utils.Config;
import noslowdwn.flightsystem.utils.Parser;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import static noslowdwn.flightsystem.FlightSystem.instance;

public class Damager extends BukkitRunnable {

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ItemStack chestplate = p.getInventory().getChestplate();
            if (instance.getConfig().getBoolean("utils.flight.damage.enable", false)
                    && !p.hasPermission("flightsystem.bypass.damage")
                    && p.isFlying()
                    && p.getGameMode() != GameMode.CREATIVE
                    && p.getGameMode() != GameMode.SPECTATOR
                    && chestplate != null && chestplate.getType() == Material.ELYTRA) {
                if (chestplate.getDurability() < chestplate.getType().getMaxDurability()
                        && (chestplate.getType().getMaxDurability() - chestplate.getDurability()) + 1
                                        > instance.getConfig().getInt("utils.flight.damage.power", 1)) {
                    chestplate.setDurability((short) (chestplate.getDurability() + instance.getConfig().getInt("utils.flight.damage.power", 1)));
                } else {
                    p.sendMessage(Parser.hex(p, Config.getMessage().getString("messages.elytra-glide.was-broken")));
                    chestplate.setDurability(chestplate.getType().getMaxDurability());

                    if (instance.getConfig().getBoolean("utils.flight.disable-if-broken", true)) {
                        Bukkit.getScheduler().runTask(instance, () -> p.setAllowFlight(false));
                    }

                    if (instance.getConfig().getBoolean("utils.flight.delete-if-broken", false)) {
                        chestplate.setItemMeta(null);
                    }
                }
            }
        }
    }
}