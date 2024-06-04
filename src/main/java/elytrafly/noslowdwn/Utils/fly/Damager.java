package elytrafly.noslowdwn.Utils.fly;

import elytrafly.noslowdwn.Utils.Config;
import elytrafly.noslowdwn.Utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import static elytrafly.noslowdwn.ElytraFly.instance;

public class Damager extends BukkitRunnable {

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ItemStack chestplate = p.getInventory().getChestplate();
            if (instance.getConfig().getBoolean("Utils.fly-command.damage.enable", false)
                    && !p.hasPermission("elytrafly.bypass.damage")
                    && p.isFlying()
                    && p.getGameMode() != GameMode.CREATIVE
                    && p.getGameMode() != GameMode.SPECTATOR
                    && chestplate != null && chestplate.getType() == Material.ELYTRA) {
                if (chestplate.getDurability() < chestplate.getType().getMaxDurability()
                        && (chestplate.getType().getMaxDurability() - chestplate.getDurability()) + 1 > instance.getConfig().getInt("Utils.fly-command.damage.power", 1)) {
                    chestplate.setDurability((short) (chestplate.getDurability() + instance.getConfig().getInt("Utils.fly-command.damage.power", 1)));
                } else {
                    p.sendMessage(Color.of(Config.getMessagesConfig().getString("Messages.elytra-require.was-broken")));
                    chestplate.setDurability(chestplate.getType().getMaxDurability());
                    if (instance.getConfig().getBoolean("Utils.fly-command.disable-if-broken", true))
                        Bukkit.getScheduler().runTask(instance, () -> p.setAllowFlight(false));
                    if (instance.getConfig().getBoolean("Utils.fly-command.delete-if-broken", false))
                        chestplate.setItemMeta(null);
                }
            }
        }
    }
}