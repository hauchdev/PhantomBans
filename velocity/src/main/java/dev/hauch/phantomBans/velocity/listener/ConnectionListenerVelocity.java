package dev.hauch.phantomBans.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.manager.PunishmentManager;
import dev.hauch.phantomBans.manager.UUIDManager;

/**
 * Listens for player connections and disconnections on Velocity.
 */
public class ConnectionListenerVelocity {

    private final ProxyServer server;

    public ConnectionListenerVelocity(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        UUIDManager.get().supplyInternUUID(
                event.getPlayer().getUsername(),
                event.getPlayer().getUniqueId());

        Universal.get().getMethods().runAsync(() -> {
            String result = Universal.get().callConnection(
                    event.getPlayer().getUsername(),
                    event.getPlayer().getRemoteAddress().getHostString());
            if (result != null) {
                event.setResult(com.velocitypowered.api.event.ResultedEvent.ComponentResult.denied(
                        net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                                .legacySection().deserialize(result)));
            }
        });
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        if (event.getPlayer() != null) {
            PunishmentManager.get().discard(event.getPlayer().getUsername());
        }
    }
}
