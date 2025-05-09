package toutouchien.quickhome.command.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.quickhome.command.Command;
import toutouchien.quickhome.command.CommandData;
import toutouchien.quickhome.lang.Lang;
import toutouchien.quickhome.managers.HomeManager;
import toutouchien.quickhome.models.Home;

import java.util.Collections;
import java.util.List;

public class HomeCommand extends Command {
    private final HomeManager homeManager;

    public HomeCommand(@NotNull HomeManager homeManager) {
        super(new CommandData("home")
                .description(Lang.getString("home_description"))
                .usage(Lang.getString("home_usage"))
                .playerRequired(true));

        this.homeManager = homeManager;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String @NotNull [] args, @NotNull String label) {
        if (args.length == 0) {
            Lang.sendMessage(player, "home_no_name");
            return;
        }

        String homeName = args[0];

        Home home = this.homeManager.homeByName(player.getUniqueId(), homeName);
        if (home == null) {
            Lang.sendMessage(player, "home_home_not_found", homeName);
            return;
        }

        Lang.sendMessage(player, "home_teleporting", homeName);
        this.homeManager.teleportToHome(player, home);
    }

    @Override
    public List<String> complete(@NotNull Player player, @NotNull String @NotNull [] args, int argIndex) {
        if (argIndex != 0)
            return Collections.emptyList();

        String currentArg = args[argIndex];
        return this.homeManager.homes(player.getUniqueId()).stream()
                .map(Home::name)
                .filter(homeName -> homeName.startsWith(currentArg))
                .toList();
    }
}
