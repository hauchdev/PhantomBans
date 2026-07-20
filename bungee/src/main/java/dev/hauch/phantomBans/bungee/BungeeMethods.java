package dev.hauch.phantomBans.bungee;

import dev.hauch.phantomBans.MethodInterface;
import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.bungee.event.PunishmentEvent;
import dev.hauch.phantomBans.bungee.event.RevokePunishmentEvent;
import dev.hauch.phantomBans.bungee.listener.CommandReceiverBungee;
import dev.hauch.phantomBans.manager.DatabaseManager;
import dev.hauch.phantomBans.manager.PunishmentManager;
import dev.hauch.phantomBans.manager.UUIDManager;
import dev.hauch.phantomBans.utils.Permissionable;
import dev.hauch.phantomBans.utils.Punishment;
import dev.hauch.phantomBans.utils.tabcompletion.TabCompleter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.bstats.bungeecord.Metrics;
import org.bstats.charts.SimplePie;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * BungeeCord implementation of MethodInterface.
 */
public class BungeeMethods implements MethodInterface {

    private final File configFile = new File(getDataFolder(), "config.yml");
    private final File messageFile = new File(getDataFolder(), "Messages.yml");
    private final File layoutFile = new File(getDataFolder(), "Layouts.yml");
    private final File mysqlFile = new File(getDataFolder(), "MySQL.yml");
    private Configuration config;
    private Configuration messages;
    private Configuration layouts;
    private Configuration mysql;

    @Override
    public void loadFiles() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            if (!configFile.exists()) {
                Files.copy(getPlugin().getResourceAsStream("config.yml"), configFile.toPath());
            }
            if (!messageFile.exists()) {
                Files.copy(getPlugin().getResourceAsStream("Messages.yml"), messageFile.toPath());
            }
            if (!layoutFile.exists()) {
                Files.copy(getPlugin().getResourceAsStream("Layouts.yml"), layoutFile.toPath());
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            messages = ConfigurationProvider.getProvider(YamlConfiguration.class).load(messageFile);
            layouts = ConfigurationProvider.getProvider(YamlConfiguration.class).load(layoutFile);
            if (mysqlFile.exists()) {
                mysql = ConfigurationProvider.getProvider(YamlConfiguration.class).load(mysqlFile);
            } else {
                mysql = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFromUrlJson(String url, String key) {
        try {
            HttpURLConnection request = (HttpURLConnection) new URL(url).openConnection();
            request.connect();
            com.google.gson.JsonParser jp = new com.google.gson.JsonParser();
            com.google.gson.JsonObject json = jp.parse(new InputStreamReader(request.getInputStream())).getAsJsonObject();
            String[] keys = key.split("\\|");
            for (int i = 0; i < keys.length - 1; i++) {
                json = json.getAsJsonObject(keys[i]);
            }
            return json.get(keys[keys.length - 1]).toString().replaceAll("\"", "");
        } catch (Exception exc) {
            return null;
        }
    }

    @Override
    public String getVersion() {
        return getPlugin().getDescription().getVersion();
    }

    @Override
    public String[] getKeys(Object file, String path) {
        return ((Configuration) file).getSection(path).getKeys().toArray(new String[0]);
    }

    @Override
    public Configuration getConfig() { return config; }

    @Override
    public Configuration getMessages() { return messages; }

    @Override
    public Configuration getLayouts() { return layouts; }

    @Override
    public void setupMetrics() {
        Metrics metrics = new Metrics(getPlugin(), 0); // Replace 0 with actual bStats plugin ID
        metrics.addCustomChart(new SimplePie("MySQL",
                () -> DatabaseManager.get().isUseMySQL() ? "yes" : "no"));
    }

    @Override
    public boolean isProxy() { return true; }

    @Override
    public String clearFormatting(String text) {
        return ChatColor.stripColor(text);
    }

    @Override
    public Plugin getPlugin() {
        return PhantomBans.get();
    }

    @Override
    public File getDataFolder() {
        return getPlugin().getDataFolder();
    }

    @Override
    public void setCommandExecutor(String cmd, String permission, TabCompleter tabCompleter) {
        ProxyServer.getInstance().getPluginManager().registerCommand(getPlugin(), new CommandReceiverBungee(cmd, permission));
    }

    @Override
    public void sendMessage(Object player, String msg) {
        ((CommandSender) player).sendMessage(msg);
    }

    @Override
    public boolean hasPerms(Object player, String perms) {
        return player != null && ((CommandSender) player).hasPermission(perms);
    }

    @Override
    public Permissionable getOfflinePermissionPlayer(String name) {
        return permission -> false;
    }

    @Override
    public boolean isOnline(String name) {
        try {
            return getPlayer(name) != null;
        } catch (NullPointerException exc) {
            return false;
        }
    }

    @Override
    public ProxiedPlayer getPlayer(String name) {
        return ProxyServer.getInstance().getPlayer(name);
    }

    @Override
    public void kickPlayer(String player, String reason) {
        ProxiedPlayer p = getPlayer(player);
        if (p != null) {
            p.disconnect(TextComponent.fromLegacyText(reason));
        }
    }

    @Override
    public ProxiedPlayer[] getOnlinePlayers() {
        return ProxyServer.getInstance().getPlayers().toArray(new ProxiedPlayer[0]);
    }

    @Override
    public void scheduleAsyncRep(Runnable rn, long l1, long l2) {
        ProxyServer.getInstance().getScheduler().schedule(getPlugin(), rn, l1 * 50, l2 * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void scheduleAsync(Runnable rn, long l1) {
        ProxyServer.getInstance().getScheduler().schedule(getPlugin(), rn, l1 * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runAsync(Runnable rn) {
        ProxyServer.getInstance().getScheduler().runAsync(getPlugin(), rn);
    }

    @Override
    public void runSync(Runnable rn) {
        rn.run();
    }

    @Override
    public void executeCommand(String cmd) {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), cmd);
    }

    @Override
    public String getName(Object player) {
        return ((CommandSender) player).getName();
    }

    @Override
    public String getName(String uuid) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
        return player != null ? player.getName() : null;
    }

    @Override
    public String getIP(Object player) {
        return ((ProxiedPlayer) player).getAddress().getHostName();
    }

    @Override
    public String getInternUUID(Object player) {
        return player instanceof ProxiedPlayer ? ((ProxiedPlayer) player).getUniqueId().toString().replaceAll("-", "") : "none";
    }

    @Override
    public String getInternUUID(String player) {
        ProxiedPlayer proxiedPlayer = getPlayer(player);
        if (proxiedPlayer == null) return null;
        return proxiedPlayer.getUniqueId().toString().replaceAll("-", "");
    }

    @Override
    public boolean callChat(Object player) {
        Punishment pnt = PunishmentManager.get().getMute(UUIDManager.get().getUUID(getName(player)));
        if (pnt != null) {
            pnt.getLayout().forEach(str -> sendMessage(player, str));
            return true;
        }
        return false;
    }

    @Override
    public boolean callCMD(Object player, String cmd) {
        Punishment pnt;
        if (Universal.get().isMuteCommand(cmd.substring(1))
                && (pnt = PunishmentManager.get().getMute(UUIDManager.get().getUUID(getName(player)))) != null) {
            pnt.getLayout().forEach(str -> sendMessage(player, str));
            return true;
        }
        return false;
    }

    @Override
    public Configuration getMySQLFile() { return mysql; }

    @Override
    public String parseJSON(InputStreamReader json, String key) {
        com.google.gson.JsonElement element = new com.google.gson.JsonParser().parse(json);
        if (element instanceof com.google.gson.JsonNull) return null;
        com.google.gson.JsonElement obj = element.getAsJsonObject().get(key);
        return obj != null ? obj.toString().replaceAll("\"", "") : null;
    }

    @Override
    public String parseJSON(String json, String key) {
        com.google.gson.JsonElement element = new com.google.gson.JsonParser().parse(json);
        if (element instanceof com.google.gson.JsonNull) return null;
        com.google.gson.JsonElement obj = element.getAsJsonObject().get(key);
        return obj != null ? obj.toString().replaceAll("\"", "") : null;
    }

    @Override
    public Boolean getBoolean(Object file, String path) { return ((Configuration) file).getBoolean(path); }

    @Override
    public String getString(Object file, String path) { return ((Configuration) file).getString(path); }

    @Override
    public Long getLong(Object file, String path) { return ((Configuration) file).getLong(path); }

    @Override
    public Integer getInteger(Object file, String path) { return ((Configuration) file).getInt(path); }

    @Override
    public List<String> getStringList(Object file, String path) { return ((Configuration) file).getStringList(path); }

    @Override
    public boolean getBoolean(Object file, String path, boolean def) { return ((Configuration) file).getBoolean(path, def); }

    @Override
    public String getString(Object file, String path, String def) { return ((Configuration) file).getString(path, def); }

    @Override
    public long getLong(Object file, String path, long def) { return ((Configuration) file).getLong(path, def); }

    @Override
    public int getInteger(Object file, String path, int def) { return ((Configuration) file).getInt(path, def); }

    @Override
    public boolean contains(Object file, String path) { return ((Configuration) file).get(path) != null; }

    @Override
    public String getFileName(Object file) { return "[Only available on Bukkit-Version!]"; }

    @Override
    public void callPunishmentEvent(Punishment punishment) {
        getPlugin().getProxy().getPluginManager().callEvent(new PunishmentEvent(punishment));
    }

    @Override
    public void callRevokePunishmentEvent(Punishment punishment, boolean massClear) {
        getPlugin().getProxy().getPluginManager().callEvent(new RevokePunishmentEvent(punishment, massClear));
    }

    @Override
    public boolean isOnlineMode() {
        return ProxyServer.getInstance().getConfig().isOnlineMode();
    }

    @Override
    public void notify(String perm, List<String> notification) {
        ProxyServer.getInstance().getPlayers().stream()
                .filter(pp -> Universal.get().hasPerms(pp, perm))
                .forEachOrdered(pp -> notification.forEach(str -> sendMessage(pp, str)));
    }

    @Override
    public void log(String msg) {
        ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(msg.replaceAll("&", "§")));
    }

    @Override
    public boolean isUnitTesting() { return false; }
}
