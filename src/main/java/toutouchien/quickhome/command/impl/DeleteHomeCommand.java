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

public class DeleteHomeCommand extends Command {
    private final HomeManager homeManager;

    public DeleteHomeCommand(@NotNull HomeManager homeManager) {
        super(new CommandData("deletehome")
                .aliases("removehome", "delhome")
                .description(Lang.getString("deletehome_description"))
                .usage(Lang.getString("deletehome_usage"))
                .playerRequired(true));

        this.homeManager = homeManager;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String @NotNull [] args, @NotNull String label) {
        if (args.length == 0) {
            Lang.sendMessage(player, "deletehome_no_name");
            return;
        }

        String arg = args[0];

        if (!arg.contains(":") || !player.hasPermission("quickhome.command.deletehome.other")) {
            deleteHome(player, player, arg, false);
            return;
        }

        String[] splitText = arg.split(":");
        String targetPlayerName = splitText[0];
        if (!PlayerUtils.isValidPlayerName(targetPlayerName)) {
            Lang.sendMessage(player, "deletehome_player_not_found_other", targetPlayerName);
            return;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);
        if (!targetPlayer.hasPlayedBefore()) {
            Lang.sendMessage(player, "deletehome_player_not_found_other", targetPlayerName);
            return;
        }

        String homeName = splitText[1];
        deleteHome(player, targetPlayer, homeName, true);
    }

    private void deleteHome(@NotNull Player player, @NotNull OfflinePlayer target, @NotNull String homeName, boolean admin) {
        Home home = this.homeManager.homeByName(target.getUniqueId(), homeName);
        if (!admin && home == null) {
            Lang.sendMessage(player, "deletehome_home_not_found", homeName);
            return;
        } else if (home == null) {
            String name = target.getName();
            Lang.sendMessage(player, "deletehome_home_not_found_other", name == null ? "null" : name, homeName);
            return;
        }

        this.homeManager.deleteHome(player.getUniqueId(), home);
        Lang.sendMessage(player, "deletehome_home_deleted", homeName);
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

        if (!player.hasPermission("quickhome.command.deletehome.other"))
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
