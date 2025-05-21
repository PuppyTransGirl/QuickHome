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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
            deleteHome(player, player.getUniqueId(), arg);
            return;
        }

        String[] splitText = arg.split(":");
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(splitText[0]);
        if (!targetPlayer.hasPlayedBefore()) {
            Lang.sendMessage(player, "deletehome_player_not_found", splitText[0]);
            return;
        }

        UUID targetUUID = targetPlayer.getUniqueId();
        String homeName = splitText[1];
        deleteHome(player, targetUUID, homeName);
    }

    private void deleteHome(@NotNull Player player, @NotNull UUID targetUUID, @NotNull String homeName) {
        Home home = this.homeManager.homeByName(targetUUID, homeName);
        if (home == null) {
            Lang.sendMessage(player, "deletehome_home_not_found", homeName);
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
