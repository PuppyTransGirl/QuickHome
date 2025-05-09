package toutouchien.quickhome.command.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.quickhome.command.Command;
import toutouchien.quickhome.command.CommandData;
import toutouchien.quickhome.config.HomeConfig;
import toutouchien.quickhome.lang.Lang;
import toutouchien.quickhome.managers.HomeManager;

import java.util.UUID;

public class SetHomeCommand extends Command {
    private final HomeManager homeManager;
    private final HomeConfig homeConfig;

    public SetHomeCommand(@NotNull HomeManager homeManager, @NotNull HomeConfig homeConfig) {
        super(new CommandData("sethome")
                .description(Lang.getString("sethome_description"))
                .usage(Lang.getString("sethome_usage"))
                .playerRequired(true));

        this.homeManager = homeManager;
        this.homeConfig = homeConfig;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String @NotNull [] args, @NotNull String label) {
        if (args.length == 0) {
            Lang.sendMessage(player, "sethome_no_name");
            return;
        }

        String homeName = args[0];

        if (homeName.length() < this.homeConfig.minHomeNameLength()) {
            Lang.sendMessage(player, "sethome_name_too_short", homeConfig.minHomeNameLength());
            return;
        }

        if (homeName.length() > this.homeConfig.maxHomeNameLength()) {
            Lang.sendMessage(player, "sethome_name_too_long", homeConfig.maxHomeNameLength());
            return;
        }

        if (!this.homeConfig.homeNameValidationRegex().matcher(homeName).matches()) {
            Lang.sendMessage(player, "sethome_name_invalid", this.homeConfig.homeNameValidationRegex());
            return;
        }

        UUID uuid = player.getUniqueId();
        if (this.homeManager.homeExists(uuid, homeName)) {
            Lang.sendMessage(player, "sethome_home_exists", homeName);
            return;
        }

        if (this.homeManager.hasReachedHomeLimit(player)) {
            Lang.sendMessage(player, "sethome_home_limit_reached");
            return;
        }

        Location location = player.getLocation();
        this.homeManager.createHome(uuid, homeName, location);
        Lang.sendMessage(player, "sethome_home_set", homeName);
    }
}
