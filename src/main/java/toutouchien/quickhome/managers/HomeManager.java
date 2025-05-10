package toutouchien.quickhome.managers;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import toutouchien.quickhome.QuickHome;
import toutouchien.quickhome.lang.Lang;
import toutouchien.quickhome.models.Home;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class HomeManager {
    private final QuickHome plugin;
    private final File homesFolder;

    private final Map<UUID, List<Home>> homes;

    public HomeManager(@NotNull QuickHome plugin) {
        this.plugin = plugin;

        this.homesFolder = new File(plugin.getDataFolder(), "homes");
        if (!this.homesFolder.exists())
            this.homesFolder.mkdirs();

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
        return this.homes.computeIfAbsent(uuid, this::loadHomes);
    }

    public void teleportToHome(@NotNull Player player, @NotNull Home home) {
        player.teleportAsync(home.location()).thenAccept(v -> {
            Lang.sendMessage(player, "home_teleport_success", home.name());
        });
    }

    public List<Home> loadHomes(@NotNull UUID uuid) {
        List<Home> loadedHomes = new ArrayList<>();

        File file = new File(homesFolder, uuid + ".yml");
        if (!file.exists())
            return Collections.emptyList();

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            String worldName = config.getString(key + ".world");
            double x = config.getDouble(key + ".x");
            double y = config.getDouble(key + ".y");
            double z = config.getDouble(key + ".z");
            float yaw = (float) config.getDouble(key + ".yaw");
            float pitch = (float) config.getDouble(key + ".pitch");

            Location location = new Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);
            loadedHomes.add(new Home(key, location));
        }

        return Collections.unmodifiableList(loadedHomes);
    }

    public void saveHomes() {
        Set<Map.Entry<UUID, List<Home>>> entries = this.homes.entrySet();
        for (Map.Entry<UUID, List<Home>> entry : entries) {
            UUID uuid = entry.getKey();
            List<Home> cachedHomes = entry.getValue();

            File file = new File(homesFolder, uuid + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            Set<String> homesKeys = config.getKeys(false);
            Set<String> homesNames = cachedHomes.stream()
                    .map(home -> home.name().toLowerCase(Locale.ROOT))
                    .collect(Collectors.toSet());

            // Delete all homes that are not in the cache but that are in the home file
            for (String key : homesKeys) {
                if (homesNames.contains(key))
                    continue;

                config.set("homes." + key, null);
            }

            // Save all homes in the cache
            for (Home home : cachedHomes) {
                String key = home.name().toLowerCase(Locale.ROOT);
                Location location = home.location();

                config.set(key + ".world", location.getWorld().getName());
                config.set(key + ".x", location.getX());
                config.set(key + ".y", location.getY());
                config.set(key + ".z", location.getZ());
                config.set(key + ".yaw", location.getYaw());
                config.set(key + ".pitch", location.getPitch());
            }

            try {
                config.save(file);
            } catch (Exception e) {
                plugin.getSLF4JLogger().warn("Failed to save homes for " + uuid, e);
            }
        }
    }
}
