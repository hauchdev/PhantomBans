package dev.hauch.phantomBans.bukkit.listener;

import dev.hauch.phantomBans.manager.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Receives and handles Bukkit commands.
 */
public class CommandReceiver implements CommandExecutor {

    private static CommandReceiver instance = null;

    public static CommandReceiver get() {
        if (instance == null) instance = new CommandReceiver();
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length > 0) {
            strings[0] = (Bukkit.getPlayer(strings[0]) != null ? Bukkit.getPlayer(strings[0]).getName() : strings[0]);
        }
        CommandManager.get().onCommand(commandSender, command.getName(), strings);
        return true;
    }
}
