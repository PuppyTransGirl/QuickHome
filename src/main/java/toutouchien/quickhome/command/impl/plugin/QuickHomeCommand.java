package toutouchien.quickhome.command.impl.plugin;

import org.jetbrains.annotations.NotNull;
import toutouchien.quickhome.QuickHome;
import toutouchien.quickhome.command.Command;
import toutouchien.quickhome.command.CommandData;
import toutouchien.quickhome.lang.Lang;

public class QuickHomeCommand extends Command {
    public QuickHomeCommand(@NotNull QuickHome plugin) {
        super(new CommandData("quickhome")
                .description(Lang.getString("quickhome_description"))
                .usage(Lang.getString("quickhome_usage"))
                .subCommands(new QuickHomeReloadCommand(plugin)));
    }
}
