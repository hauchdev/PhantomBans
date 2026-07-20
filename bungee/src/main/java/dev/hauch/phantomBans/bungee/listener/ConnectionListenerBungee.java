package dev.hauch.phantomBans.bungee.listener;

import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.bungee.PhantomBans;
import dev.hauch.phantomBans.manager.PunishmentManager;
import dev.hauch.phantomBans.manager.UUIDManager;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Listens for player connections on BungeeCord.
 */
public class ConnectionListenerBungee implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onConnection(LoginEvent event) {
        if (event.isCancelled()) return;

        UUIDManager.get().supplyInternUUID(event.getConnection().getName(), event.getConnection().getUniqueId());
        event.registerIntent(PhantomBans.get());

        Universal.get().getMethods().runAsync(() -> {
            String result = Universal.get().callConnection(
                    event.getConnection().getName(),
                    event.getConnection().getAddress().getAddress().getHostAddress());
            if (result != null) {
                event.setCancelled(true);
                event.setCancelReason(result);
            }
            event.completeIntent(PhantomBans.get());
        });
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        Universal.get().getMethods().runAsync(() -> {
            if (event.getPlayer() != null) {
                PunishmentManager.get().discard(event.getPlayer().getName());
            }
        });
    }
}
