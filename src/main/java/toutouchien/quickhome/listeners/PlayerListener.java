package toutouchien.quickhome.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import toutouchien.quickhome.QuickHome;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {
    private final QuickHome plugin;
    private final Map<UUID, Integer> runnables;

    public PlayerListener(@NotNull QuickHome plugin) {
        this.plugin = plugin;
        this.runnables = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (!this.runnables.containsKey(uuid))
            return;

        Bukkit.getScheduler().cancelTask(this.runnables.get(uuid));
        this.runnables.remove(uuid);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        this.runnables.put(uuid, Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            this.plugin.homeManager().unloadHomes(uuid);
        }, 3L * 60L * 20L).getTaskId());
    }
}
