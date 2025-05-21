package toutouchien.quickhome.command.impl;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.quickhome.command.Command;
import toutouchien.quickhome.command.CommandData;
import toutouchien.quickhome.lang.Lang;
import toutouchien.quickhome.managers.HomeManager;
import toutouchien.quickhome.models.Home;
import toutouchien.quickhome.utils.PlayerUtils;

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

        String arg = args[0];

        if (!arg.contains(":") || !player.hasPermission("quickhome.command.home.other")) {
            teleportToHome(player, player, arg, false);
            return;
        }

        String[] splitText = arg.split(":");
        String targetPlayerName = splitText[0];
        if (!PlayerUtils.isValidPlayerName(targetPlayerName)) {
            Lang.sendMessage(player, "home_player_not_found_other", targetPlayerName);
            return;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);
        if (!targetPlayer.hasPlayedBefore()) {
            Lang.sendMessage(player, "home_player_not_found_other", targetPlayerName);
            return;
        }

        String homeName = splitText[1];
        teleportToHome(player, targetPlayer, homeName, true);
    }

    private void teleportToHome(@NotNull Player player, @NotNull OfflinePlayer target, @NotNull String homeName, boolean admin) {
        Home home = this.homeManager.homeByName(target.getUniqueId(), homeName);
        if (!admin && home == null) {
            Lang.sendMessage(player, "home_home_not_found", homeName);
            return;
        } else if (home == null) {
            String name = target.getName();
            Lang.sendMessage(player, "home_home_not_found_other", name == null ? "null" : name, homeName);
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
        if (!currentArg.contains(":")) {
            return this.homeManager.homes(player.getUniqueId()).stream()
                    .map(Home::name)
                    .filter(homeName -> homeName.startsWith(currentArg))
                    .toList();
        }

        if (!player.hasPermission("quickhome.command.home.other"))
            return Collections.emptyList();

        String[] splitText = currentArg.split(":");
        String targetPlayerName = splitText[0];
        if (!PlayerUtils.isValidPlayerName(targetPlayerName))
            return Collections.emptyList();

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);
        if (!targetPlayer.hasPlayedBefore())
            return Collections.emptyList();

        List<String> homesNames = this.homeManager.homes(targetPlayer.getUniqueId()).stream()
                .map(home -> targetPlayerName + ":" + home.name())
                .toList();

        if (splitText.length == 1)
            return homesNames;

        return homesNames.stream()
                .filter(homeName -> (targetPlayerName + ":" + homeName).toLowerCase().startsWith(currentArg.toLowerCase()))
                .toList();
    }
}
