package toutouchien.quickhome.config;

import org.bukkit.configuration.file.FileConfiguration;
import toutouchien.quickhome.QuickHome;

import java.util.regex.Pattern;

public class HomeConfig {
    private final QuickHome plugin;

    private int minHomeNameLength;
    private int maxHomeNameLength;

    // Compiled Regex
    private Pattern homeNameValidationRegex;

    public HomeConfig(QuickHome plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        FileConfiguration config = this.plugin.getConfig();

        this.minHomeNameLength = config.getInt("min_home_name_length", 3);
        this.maxHomeNameLength = config.getInt("max_home_name_length", 20);

        String regex = config.getString("home_name_validation_regex", "^[a-zA-Z0-9_-]+$");
        this.homeNameValidationRegex = Pattern.compile(regex);
    }

    public void reload() {
        this.initialize();
    }

    public int minHomeNameLength() {
        return minHomeNameLength;
    }

    public int maxHomeNameLength() {
        return maxHomeNameLength;
    }

    public Pattern homeNameValidationRegex() {
        return homeNameValidationRegex;
    }
}
