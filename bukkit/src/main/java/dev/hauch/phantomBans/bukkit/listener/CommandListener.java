package dev.hauch.phantomBans.bukkit.listener;

import dev.hauch.phantomBans.Universal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Listens for commands to block muted players from using certain commands.
 */
public class CommandListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!event.isCancelled() && Universal.get().getMethods().callCMD(event.getPlayer(), event.getMessage())) {
            event.setCancelled(true);
        }
    }
}
