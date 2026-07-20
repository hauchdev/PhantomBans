package dev.hauch.phantomBans.bukkit;

import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.bukkit.listener.ChatListener;
import dev.hauch.phantomBans.bukkit.listener.CommandListener;
import dev.hauch.phantomBans.bukkit.listener.ConnectionListener;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main Bukkit/Spigot/Paper plugin class.
 */
public class PhantomBans extends JavaPlugin {

    private static PhantomBans instance;

    public static PhantomBans get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        Universal.get().setup(new BukkitMethods());

        ConnectionListener connListener = new ConnectionListener();
        getServer().getPluginManager().registerEvents(connListener, this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new CommandListener(), this);

        // Re-check online players (e.g. after /reload)
        Bukkit.getOnlinePlayers().forEach(player -> {
            AsyncPlayerPreLoginEvent apple = new AsyncPlayerPreLoginEvent(
                    player.getName(), player.getAddress().getAddress(), player.getUniqueId());
            connListener.onConnect(apple);
            if (apple.getLoginResult() == AsyncPlayerPreLoginEvent.Result.KICK_BANNED) {
                player.kickPlayer(apple.getKickMessage());
            }
        });
    }

    @Override
    public void onDisable() {
        Universal.get().shutdown();
    }
}
