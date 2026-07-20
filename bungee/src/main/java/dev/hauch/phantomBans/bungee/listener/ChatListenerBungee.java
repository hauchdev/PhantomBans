package dev.hauch.phantomBans.bungee.listener;

import dev.hauch.phantomBans.Universal;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Listens for chat/commands on BungeeCord to block muted players.
 */
public class ChatListenerBungee implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent event) {
        if (event.isCancelled()) return;

        if (event.getMessage().startsWith("/")) {
            if (Universal.get().getMethods().callCMD(event.getSender(), event.getMessage())) {
                event.setCancelled(true);
            }
        } else {
            if (Universal.get().getMethods().callChat(event.getSender())) {
                event.setCancelled(true);
            }
        }
    }
}
