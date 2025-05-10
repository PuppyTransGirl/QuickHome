package toutouchien.quickhome;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import toutouchien.quickhome.command.Command;
import toutouchien.quickhome.command.CommandManager;
import toutouchien.quickhome.command.impl.DeleteHomeCommand;
import toutouchien.quickhome.command.impl.HomeCommand;
import toutouchien.quickhome.command.impl.SetHomeCommand;
import toutouchien.quickhome.command.impl.plugin.QuickHomeCommand;
import toutouchien.quickhome.config.HomeConfig;
import toutouchien.quickhome.lang.Lang;
import toutouchien.quickhome.managers.HomeManager;

import java.util.Arrays;
import java.util.List;

public final class QuickHome extends JavaPlugin {
    private CommandManager commandManager;
    private HomeManager homeManager;

    private HomeConfig homeConfig;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        Lang.load(this);

        this.commandManager = new CommandManager();
        this.homeManager = new HomeManager(this);

        (this.homeConfig = new HomeConfig(this)).initialize();

        // Register commands
        this.registerCommands();
    }

    public void registerCommands() {
       List<Command> commands = Arrays.asList(
                new QuickHomeCommand(this),

                new DeleteHomeCommand(this.homeManager),
                new SetHomeCommand(this.homeManager, this.homeConfig),
                new HomeCommand(this.homeManager)
        );

       this.commandManager.registerCommands(commands);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void reload() {
        this.reloadConfig();
        Lang.reload(this);

        this.homeConfig.reload();
    }

    @NotNull
    public HomeConfig homeConfig() {
        return homeConfig;
    }
}
