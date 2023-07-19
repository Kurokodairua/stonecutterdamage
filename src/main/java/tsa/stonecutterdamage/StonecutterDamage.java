package tsa.stonecutterdamage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StonecutterDamage extends JavaPlugin implements Listener {

    private int damageInterval = 10; // Schaden alle 10 Tick (0.5 Sekunde)
    private Map<UUID, BukkitRunnable> damageTasks = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("StonecutterDamage activated");
    }

    @Override
    public void onDisable() {
        for (BukkitRunnable damageTask : damageTasks.values()) {
            damageTask.cancel();
        }
        damageTasks.clear();
        getLogger().info("StonecutterDamage deactivated");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Material blockType = player.getLocation().getBlock().getType();
        if (blockType == Material.STONECUTTER) {
            startDamageTask(player);
        } else {
            stopDamageTask(player);
        }
    }

    private void startDamageTask(Player player) {
        if (damageTasks.containsKey(player.getUniqueId())) {
            // Schadensvorgang läuft bereits
            return;
        }
        getLogger().info(player.getName()+ " stepped on a stonecutter");

        BukkitRunnable damageTask = new BukkitRunnable() {
            @Override
            public void run() {
                player.damage(1); // Hier kannst du den gewünschten Schaden einstellen
            }
        };
        damageTask.runTaskTimer(this, damageInterval, damageInterval);
        damageTasks.put(player.getUniqueId(), damageTask);
    }

    private void stopDamageTask(Player player) {
        if (damageTasks.containsKey(player.getUniqueId())) {
            BukkitRunnable damageTask = damageTasks.remove(player.getUniqueId());
            damageTask.cancel();
            getLogger().info(player.getName()+ " stepped off a stonecutter");
        }
    }
}