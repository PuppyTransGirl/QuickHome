package toutouchien.quickhome.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import toutouchien.quickhome.QuickHome;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Lang {
    private static final Map<String, String> MESSAGES = new ConcurrentHashMap<>();
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final String DEFAULT_LANG = "en_US";

    private static final String[] DEFAULT_MESSAGES_FILES = {
            "en_US.yml",
            "fr_FR.yml"
    };

    public static void load(@NotNull QuickHome plugin) {
        MESSAGES.clear();

        saveDefaultMessages(plugin);

        String langCode = plugin.getConfig().getString("lang", DEFAULT_LANG);
        File langFile = new File(plugin.getDataFolder(), "lang/%s.yml".formatted(langCode));
        if (!langFile.exists())
            return;

        FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
        for (String key : langConfig.getKeys(false)) {
            String value = langConfig.getString(key);
            if (value == null)
                continue;

            MESSAGES.put(key, value);
        }
    }

    public static void reload(@NotNull QuickHome plugin) {
        load(plugin);
    }

    private static void saveDefaultMessages(@NotNull QuickHome plugin) {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists())
            langFolder.mkdirs();

        for (String lang : DEFAULT_MESSAGES_FILES) {
            File langFile = new File(langFolder, lang);
            if (langFile.exists())
                continue;

            plugin.saveResource("lang/%s".formatted(lang), false);
        }
    }

    @NotNull
    public static String getString(@NotNull String key) {
        return MESSAGES.getOrDefault(key, key);
    }

    @NotNull
    public static String getString(@NotNull String key, @NotNull Object @NotNull ... args) {
        return MESSAGES.getOrDefault(key, key).formatted(args);
    }

    @NotNull
    public static Component get(@NotNull String key) {
        return MM.deserialize(getString(key));
    }

    @NotNull
    public static Component get(@NotNull String key, @NotNull Object @NotNull ... args) {
        return MM.deserialize(getString(key, args));
    }

    public static void sendMessage(@NotNull CommandSender sender, @NotNull String key) {
        sender.sendMessage(get(key));
    }

    public static void sendMessage(@NotNull CommandSender sender, @NotNull String key, @NotNull Object @NotNull ... args) {
        sender.sendMessage(get(key, args));
    }
}
