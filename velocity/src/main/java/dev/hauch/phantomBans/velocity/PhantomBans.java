package dev.hauch.phantomBans.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.velocity.listener.ChatListenerVelocity;
import dev.hauch.phantomBans.velocity.listener.ConnectionListenerVelocity;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import java.nio.file.Path;

/**
 * Main Velocity plugin class.
 */
@Plugin(
        id = "phantombans",
        name = "PhantomBans",
        version = "1.0-SNAPSHOT",
        description = "A multi-platform punishment system",
        authors = {"Hauchdev"}
)
public class PhantomBans {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final Metrics.Factory metricsFactory;

    @Inject
    public PhantomBans(ProxyServer server, Logger logger,
                       @DataDirectory Path dataDirectory,
                       Metrics.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Register events first
        server.getEventManager().register(this, new ConnectionListenerVelocity(server));
        server.getEventManager().register(this, new ChatListenerVelocity());

        // Setup the core
        Universal.get().setup(new VelocityMethods(dataDirectory, server, this));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        Universal.get().shutdown();
    }

    public ProxyServer getServer() { return server; }
    public Logger getLogger() { return logger; }
    public Path getDataDirectory() { return dataDirectory; }
    public Metrics.Factory getMetricsFactory() { return metricsFactory; }
    public String getVersion() { return "1.0-SNAPSHOT"; }
}
