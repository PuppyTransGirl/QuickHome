package toutouchien.quickhome.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.util.List;

public class CommandManager {
    public void registerCommand(Command command) {
        CommandMap commandMap = Bukkit.getCommandMap();
        String fallbackPrefix = command.data().pluginName();

        commandMap.register(fallbackPrefix, command);
    }

    public void registerCommands(List<Command> commands) {
        if (commands.isEmpty())
            return;

        for (Command command : commands) {
            registerCommand(command);
        }
    }
}