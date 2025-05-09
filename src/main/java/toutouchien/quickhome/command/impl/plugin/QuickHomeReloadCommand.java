package toutouchien.quickhome.command.impl.plugin;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import toutouchien.quickhome.QuickHome;
import toutouchien.quickhome.command.CommandData;
import toutouchien.quickhome.command.SubCommand;
import toutouchien.quickhome.lang.Lang;

public class QuickHomeReloadCommand extends SubCommand {
    private final QuickHome quickHome;

    protected QuickHomeReloadCommand(@NotNull QuickHome quickHome) {
        super(new CommandData("reload")
                .description(Lang.getString("quickhome_reload_description")));

        this.quickHome = quickHome;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args, @NotNull String label) {
        long startMillis = System.currentTimeMillis();
        this.quickHome.reload();
        long timeTaken = System.currentTimeMillis() - startMillis;

        Lang.sendMessage(sender, "quickhome_reload_success", timeTaken);
    }
}
