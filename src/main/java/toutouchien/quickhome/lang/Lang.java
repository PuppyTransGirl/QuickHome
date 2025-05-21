package toutouchien.quickhome.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import toutouchien.quickhome.QuickHome;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Lang {
    private static final Map<Locale, Map<String, String>> MESSAGES = new ConcurrentHashMap<>();
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final String DEFAULT_LANG = "en_US";
    private static final String[] DEFAULT_MESSAGES_FILES = {
            "en_US.yml",
            "fr_FR.yml"
    };

    private static Locale defaultLocale = Locale.US;
    private static boolean usePlayerLocale;

    public static void load(@NotNull QuickHome plugin) {
        MESSAGES.clear();

        saveDefaultMessages(plugin);
        loadConfig(plugin);
        loadMessages(plugin);
    }

    private static void loadConfig(@NotNull QuickHome plugin) {
        FileConfiguration config = plugin.getConfig();
        String langCode = config.getString("lang", DEFAULT_LANG);
        defaultLocale = Locale.forLanguageTag(langCode.replace('_', '-'));
        usePlayerLocale = config.getBoolean("use_player_locale", false);
    }

    private static void loadMessages(@NotNull QuickHome plugin) {
        if (!usePlayerLocale) {
            loadLocaleFile(plugin, defaultLocale);
        } else {
            for (Locale locale : Locale.getAvailableLocales())
                loadLocaleFile(plugin, locale);
        }
    }

    private static void loadLocaleFile(@NotNull QuickHome plugin, @NotNull Locale locale) {
        String fileName = "lang/%s.yml".formatted(locale.toLanguageTag().replace('-', '_'));
        File langFile = new File(plugin.getDataFolder(), fileName);
        if (!langFile.exists())
            return;

        Map<String, String> messages = new HashMap<>();
        FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);

        for (String key : langConfig.getKeys(false)) {
            String value = langConfig.getString(key);
            if (value != null)
                messages.put(key, value);
        }

        if (!messages.isEmpty())
            MESSAGES.put(locale, messages);
    }

    private static void saveDefaultMessages(@NotNull QuickHome plugin) {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists() && !langFolder.mkdirs()) {
            plugin.getLogger().warning("Failed to create language folder");
            return;
        }

        for (String lang : DEFAULT_MESSAGES_FILES) {
            File langFile = new File(langFolder, lang);
            if (!langFile.exists())
                plugin.saveResource("lang/%s".formatted(lang), false);
        }
    }

    @NotNull
    private static String getStringInternal(@Nullable CommandSender sender, @NotNull String key, Object @NotNull ... args) {
        Locale locale = defaultLocale;
        if (usePlayerLocale && sender instanceof Player player)
            locale = player.locale();

        String message = MESSAGES.getOrDefault(locale, Collections.emptyMap()).getOrDefault(key, key);
        return args.length > 0 ? message.formatted(args) : message;
    }

    @NotNull
    public static String getString(@NotNull String key) {
        return getStringInternal(null, key);
    }

    @NotNull
    public static String getString(@NotNull String key, @NotNull Object @NotNull ... args) {
        return getStringInternal(null, key, args);
    }

    @NotNull
    public static String getString(@NotNull CommandSender sender, @NotNull String key) {
        return getStringInternal(sender, key);
    }

    @NotNull
    public static String getString(@NotNull CommandSender sender, @NotNull String key, @NotNull Object @NotNull ... args) {
        return getStringInternal(sender, key, args);
    }

    @NotNull
    public static Component get(@NotNull String key) {
        return MM.deserialize(getString(key));
    }

    @NotNull
    public static Component get(@NotNull String key, @NotNull Object @NotNull ... args) {
        return MM.deserialize(getString(key, args));
    }

    @NotNull
    public static Component get(@NotNull CommandSender sender, @NotNull String key) {
        return MM.deserialize(getString(sender, key));
    }

    @NotNull
    public static Component get(@NotNull CommandSender sender, @NotNull String key, @NotNull Object @NotNull ... args) {
        return MM.deserialize(getString(sender, key, args));
    }

    public static void sendMessage(@NotNull CommandSender sender, @NotNull String key) {
        Component message = get(sender, key);
        if (!message.equals(Component.empty()))
            sender.sendMessage(message);
    }

    public static void sendMessage(@NotNull CommandSender sender, @NotNull String key, @NotNull Object @NotNull ... args) {
        Component message = get(sender, key, args);
        if (!message.equals(Component.empty()))
            sender.sendMessage(message);
    }

    public static void reload(@NotNull QuickHome plugin) {
        load(plugin);
    }
}