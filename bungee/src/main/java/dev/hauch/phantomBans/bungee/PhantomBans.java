package dev.hauch.phantomBans.bungee;

import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.bungee.listener.ChatListenerBungee;
import dev.hauch.phantomBans.bungee.listener.CommandReceiverBungee;
import dev.hauch.phantomBans.bungee.listener.ConnectionListenerBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Main BungeeCord/Waterfall plugin class.
 */
public class PhantomBans extends Plugin {

    private static PhantomBans instance;

    public static PhantomBans get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        Universal.get().setup(new BungeeMethods());

        ProxyServer.getInstance().getPluginManager().registerListener(this, new ConnectionListenerBungee());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ChatListenerBungee());
        ProxyServer.getInstance().registerChannel("phantombans:main");
    }

    @Override
    public void onDisable() {
        Universal.get().shutdown();
    }
}
