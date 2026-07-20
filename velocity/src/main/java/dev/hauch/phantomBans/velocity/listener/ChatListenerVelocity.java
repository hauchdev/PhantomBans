package dev.hauch.phantomBans.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import dev.hauch.phantomBans.Universal;

/**
 * Listens for chat and commands on Velocity to block muted players.
 */
public class ChatListenerVelocity {

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        if (Universal.get().getMethods().callChat(event.getPlayer())) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
        }
    }

    @Subscribe
    public void onCommandExecute(CommandExecuteEvent event) {
        if (!(event.getCommandSource() instanceof com.velocitypowered.api.proxy.Player)) return;

        if (Universal.get().getMethods().callCMD(event.getCommandSource(), "/" + event.getCommand())) {
            event.setResult(CommandExecuteEvent.CommandResult.denied());
        }
    }
}
