package toutouchien.quickhome.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import toutouchien.quickhome.lang.Lang;
import toutouchien.quickhome.models.Home;

import java.util.*;

public class HomeManager {
    private final Map<UUID, List<Home>> homes;

    public HomeManager() {
        this.homes = new HashMap<>();
    }

    public void createHome(@NotNull UUID uuid, @NotNull String homeName, @NotNull Location location) {
        Home home = new Home(homeName, location);

        List<Home> newHomes = new ArrayList<>(homes(uuid));
        newHomes.add(home);

        this.homes.put(uuid, newHomes);
    }

    public void deleteHome(@NotNull UUID uuid, @NotNull Home home) {
        this.homes.computeIfPresent(uuid, (k, v) -> {
            List<Home> newHomes = new ArrayList<>(v);
            newHomes.remove(home);
            return Collections.unmodifiableList(newHomes);
        });
    }

    public boolean hasReachedHomeLimit(@NotNull Player player) {
        return homes(player.getUniqueId()).size() >= maxAllowedHomes(player);
    }

    public int maxAllowedHomes(@NotNull Player player) {
        return player.getEffectivePermissions().stream()
                .map(PermissionAttachmentInfo::getPermission)
                .filter(p -> p.startsWith("quickhome.maxhomes."))
                .map(p -> p.split("\\."))
                .filter(parts -> parts.length == 3)
                .mapToInt(parts -> {
                    try {
                        return Integer.parseInt(parts[2]);
                    } catch (NumberFormatException e) {
                        return 5;
                    }
                })
                .max()
                .orElse(5);
    }

    public boolean homeExists(@NotNull UUID uuid, @NotNull String homeName) {
        return homes(uuid).stream()
                .anyMatch(home -> home.name().equalsIgnoreCase(homeName));
    }

    @Nullable
    public Home homeByName(@NotNull UUID uuid, @NotNull String homeName) {
        return homes(uuid).stream()
                .filter(home -> home.name().equalsIgnoreCase(homeName))
                .findFirst()
                .orElse(null);
    }

    @NotNull
    public List<Home> homes(@NotNull UUID uuid) {
        return this.homes.computeIfAbsent(uuid, k -> Collections.emptyList());
    }

    public void teleportToHome(@NotNull Player player, @NotNull Home home) {
        player.teleportAsync(home.location()).thenAccept(v -> {
            Lang.sendMessage(player, "home_teleport_success", home.name());
        });
    }
}
