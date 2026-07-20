package dev.hauch.phantomBans.manager;

import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.utils.Command;

/**
 * Handles command execution.
 */
public class CommandManager {

    private static CommandManager instance = null;

    public static synchronized CommandManager get() {
        if (instance == null) instance = new CommandManager();
        return instance;
    }

    public void onCommand(final Object sender, final String cmd, final String[] args) {
        Universal.get().getMethods().runAsync(() -> {
            Command command = Command.getByName(cmd);
            if (command == null) return;
            String permission = command.getPermission();
            if (permission != null && !Universal.get().hasPerms(sender, permission)) {
                MessageManager.sendMessage(sender, "General.NoPerms", true);
                return;
            }
            if (!command.validateArguments(args)) {
                MessageManager.sendMessage(sender, command.getUsagePath(), true);
                return;
            }
            command.execute(sender, args);
        });
    }
}
