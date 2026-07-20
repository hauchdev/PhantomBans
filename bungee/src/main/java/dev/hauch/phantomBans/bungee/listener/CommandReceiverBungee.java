package dev.hauch.phantomBans.bungee.listener;

import dev.hauch.phantomBans.bungee.PhantomBans;
import dev.hauch.phantomBans.manager.CommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 * Receives BungeeCord commands and forwards them to CommandManager.
 */
public class CommandReceiverBungee extends Command {

    public CommandReceiverBungee(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length > 0) {
            args[0] = (PhantomBans.get().getProxy().getPlayer(args[0]) != null
                    ? PhantomBans.get().getProxy().getPlayer(args[0]).getName()
                    : args[0]);
        }
        CommandManager.get().onCommand(sender, this.getName(), args);
    }
}
