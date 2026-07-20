package dev.hauch.phantomBans.velocity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.hauch.phantomBans.MethodInterface;
import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.manager.DatabaseManager;
import dev.hauch.phantomBans.manager.PunishmentManager;
import dev.hauch.phantomBans.manager.UUIDManager;
import dev.hauch.phantomBans.utils.Permissionable;
import dev.hauch.phantomBans.utils.Punishment;
import dev.hauch.phantomBans.utils.tabcompletion.TabCompleter;
import dev.hauch.phantomBans.velocity.event.PunishmentEvent;
import dev.hauch.phantomBans.velocity.event.RevokePunishmentEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bstats.velocity.Metrics;
import org.bstats.charts.SimplePie;

import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Velocity implementation of MethodInterface.
 */
public class VelocityMethods implements MethodInterface {

    private final Path dataDirectory;
    private final ProxyServer server;
    private final PhantomBans plugin;
    private final File configFile;
    private final File messageFile;
    private final File layoutFile;
    private final File mysqlFile;

    // Simple properties-based config (Velocity doesn't have built-in YAML)
    private Properties config;
    private Properties messages;
    private Properties layouts;
    private Properties mysql;

    public VelocityMethods(Path dataDirectory, ProxyServer server, PhantomBans plugin) {
        this.dataDirectory = dataDirectory;
        this.server = server;
        this.plugin = plugin;
        this.configFile = new File(dataDirectory.toFile(), "config.properties");
        this.messageFile = new File(dataDirectory.toFile(), "messages.properties");
        this.layoutFile = new File(dataDirectory.toFile(), "layouts.properties");
        this.mysqlFile = new File(dataDirectory.toFile(), "mysql.properties");
    }

    @Override
    public void loadFiles() {
        try {
            if (!dataDirectory.toFile().exists()) {
                dataDirectory.toFile().mkdirs();
            }
            config = loadProperties(configFile, "config.properties");
            messages = loadProperties(messageFile, "messages.properties");
            layouts = loadProperties(layoutFile, "layouts.properties");
            mysql = loadProperties(mysqlFile, "mysql.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Properties loadProperties(File file, String resourceName) {
        Properties props = new Properties();
        try {
            if (!file.exists()) {
                // Save default from resources
                var in = getClass().getClassLoader().getResourceAsStream(resourceName);
                if (in != null) {
                    props.load(in);
                    in.close();
                    // Save to file
                    var out = new java.io.FileOutputStream(file);
                    props.store(out, "PhantomBans " + resourceName);
                    out.close();
                }
            } else {
                var in = new java.io.FileInputStream(file);
                props.load(in);
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props;
    }

    @Override
    public String getFromUrlJson(String url, String key) {
        try {
            HttpURLConnection request = (HttpURLConnection) new URL(url).openConnection();
            request.connect();
            JsonParser jp = new JsonParser();
            JsonObject json = jp.parse(new InputStreamReader(request.getInputStream())).getAsJsonObject();
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
        return plugin.getVersion();
    }

    @Override
    public String[] getKeys(Object file, String path) {
        return ((Properties) file).stringPropertyNames().stream()
                .filter(k -> k.startsWith(path + "."))
                .map(k -> k.substring(path.length() + 1))
                .toArray(String[]::new);
    }

    @Override
    public Properties getConfig() { return config; }

    @Override
    public Properties getMessages() { return messages; }

    @Override
    public Properties getLayouts() { return layouts; }

    @Override
    public void setupMetrics() {
        Metrics.Factory factory = plugin.getMetricsFactory();
        if (factory != null) {
            Metrics metrics = factory.make(plugin, 0); // Replace with actual bStats plugin ID
            metrics.addCustomChart(new SimplePie("MySQL",
                    () -> DatabaseManager.get().isUseMySQL() ? "yes" : "no"));
        }
    }

    @Override
    public boolean isProxy() { return true; }

    @Override
    public String clearFormatting(String text) {
        return text.replaceAll("§[0-9a-fk-or]", "");
    }

    @Override
    public PhantomBans getPlugin() { return plugin; }

    @Override
    public File getDataFolder() {
        return dataDirectory.toFile();
    }

    @Override
    public void setCommandExecutor(String cmd, String permission, TabCompleter tabCompleter) {
        SimpleCommand command = invocation -> {
            CommandSource source = invocation.source();
            String[] args = invocation.arguments();

            // Resolve player names
            if (args.length > 0) {
                Optional<Player> targetPlayer = server.getPlayer(args[0]);
                if (targetPlayer.isPresent()) {
                    args[0] = targetPlayer.get().getUsername();
                }
            }

            dev.hauch.phantomBans.manager.CommandManager.get().onCommand(source, cmd, args);
        };

        SimpleCommand tabCompleteCommand = new SimpleCommand() {
            @Override
            public void execute(Invocation invocation) {
                command.execute(invocation);
            }

            @Override
            public List<String> suggest(Invocation invocation) {
                if (tabCompleter != null) {
                    String[] args = invocation.arguments();
                    if (permission != null && !hasPerms(invocation.source(), permission)) {
                        return Collections.emptyList();
                    }
                    return tabCompleter.onTabComplete(invocation.source(), args);
                }
                return Collections.emptyList();
            }

            @Override
            public boolean hasPermission(Invocation invocation) {
                return permission == null || invocation.source().hasPermission(permission);
            }
        };

        server.getCommandManager().register(cmd, tabCompleteCommand);
    }

    @Override
    public void sendMessage(Object player, String msg) {
        if (player instanceof CommandSource) {
            ((CommandSource) player).sendMessage(LegacyComponentSerializer.legacySection().deserialize(msg));
        }
    }

    @Override
    public boolean hasPerms(Object player, String perms) {
        return player instanceof CommandSource && ((CommandSource) player).hasPermission(perms);
    }

    @Override
    public Permissionable getOfflinePermissionPlayer(String name) {
        return permission -> false;
    }

    @Override
    public boolean isOnline(String name) {
        return server.getPlayer(name).isPresent();
    }

    @Override
    public Player getPlayer(String name) {
        return server.getPlayer(name).orElse(null);
    }

    @Override
    public void kickPlayer(String player, String reason) {
        server.getPlayer(player).ifPresent(p ->
                p.disconnect(LegacyComponentSerializer.legacySection().deserialize(reason)));
    }

    @Override
    public Player[] getOnlinePlayers() {
        return server.getAllPlayers().toArray(new Player[0]);
    }

    @Override
    public void scheduleAsyncRep(Runnable rn, long l1, long l2) {
        server.getScheduler().buildTask(plugin, rn).delay(l1, TimeUnit.MILLISECONDS)
                .repeat(l2, TimeUnit.MILLISECONDS).schedule();
    }

    @Override
    public void scheduleAsync(Runnable rn, long l1) {
        server.getScheduler().buildTask(plugin, rn).delay(l1, TimeUnit.MILLISECONDS).schedule();
    }

    @Override
    public void runAsync(Runnable rn) {
        server.getScheduler().buildTask(plugin, rn).schedule();
    }

    @Override
    public void runSync(Runnable rn) {
        rn.run(); // Velocity is already async-safe
    }

    @Override
    public void executeCommand(String cmd) {
        server.getCommandManager().executeAsync(server.getConsoleCommandSource(), cmd);
    }

    @Override
    public String getName(Object player) {
        if (player instanceof Player) {
            return ((Player) player).getUsername();
        }
        return "CONSOLE";
    }

    @Override
    public String getName(String uuid) {
        return server.getPlayer(UUID.fromString(uuid)).map(Player::getUsername).orElse(null);
    }

    @Override
    public String getIP(Object player) {
        if (player instanceof Player) {
            return ((Player) player).getRemoteAddress().getHostName();
        }
        return "127.0.0.1";
    }

    @Override
    public String getInternUUID(Object player) {
        if (player instanceof Player) {
            return ((Player) player).getUniqueId().toString().replaceAll("-", "");
        }
        return "none";
    }

    @Override
    public String getInternUUID(String player) {
        return server.getPlayer(player)
                .map(p -> p.getUniqueId().toString().replaceAll("-", ""))
                .orElse(null);
    }

    @Override
    public boolean callChat(Object player) {
        if (player instanceof Player) {
            String uuid = UUIDManager.get().getUUID(getName(player));
            Punishment pnt = PunishmentManager.get().getMute(uuid);
            if (pnt != null) {
                pnt.getLayout().forEach(str -> sendMessage(player, str));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean callCMD(Object player, String cmd) {
        if (player instanceof Player) {
            Punishment pnt;
            if (Universal.get().isMuteCommand(cmd.substring(1))
                    && (pnt = PunishmentManager.get().getMute(
                            UUIDManager.get().getUUID(getName(player)))) != null) {
                pnt.getLayout().forEach(str -> sendMessage(player, str));
                return true;
            }
        }
        return false;
    }

    @Override
    public Properties getMySQLFile() { return mysql; }

    @Override
    public String parseJSON(InputStreamReader json, String key) {
        JsonElement element = JsonParser.parseReader(json);
        if (element instanceof JsonNull) return null;
        JsonElement obj = element.getAsJsonObject().get(key);
        return obj != null ? obj.toString().replaceAll("\"", "") : null;
    }

    @Override
    public String parseJSON(String json, String key) {
        JsonElement element = JsonParser.parseString(json);
        if (element instanceof JsonNull) return null;
        JsonElement obj = element.getAsJsonObject().get(key);
        return obj != null ? obj.toString().replaceAll("\"", "") : null;
    }

    // Properties-based config getters
    @Override
    public Boolean getBoolean(Object file, String path) {
        String val = ((Properties) file).getProperty(path);
        return val != null && Boolean.parseBoolean(val);
    }

    @Override
    public String getString(Object file, String path) {
        return ((Properties) file).getProperty(path);
    }

    @Override
    public Long getLong(Object file, String path) {
        String val = ((Properties) file).getProperty(path);
        return val != null ? Long.parseLong(val) : 0L;
    }

    @Override
    public Integer getInteger(Object file, String path) {
        String val = ((Properties) file).getProperty(path);
        return val != null ? Integer.parseInt(val) : 0;
    }

    @Override
    public List<String> getStringList(Object file, String path) {
        String val = ((Properties) file).getProperty(path);
        if (val == null) return Collections.emptyList();
        return Arrays.asList(val.split(","));
    }

    @Override
    public boolean getBoolean(Object file, String path, boolean def) {
        String val = ((Properties) file).getProperty(path);
        return val != null ? Boolean.parseBoolean(val) : def;
    }

    @Override
    public String getString(Object file, String path, String def) {
        return ((Properties) file).getProperty(path, def);
    }

    @Override
    public long getLong(Object file, String path, long def) {
        String val = ((Properties) file).getProperty(path);
        return val != null ? Long.parseLong(val) : def;
    }

    @Override
    public int getInteger(Object file, String path, int def) {
        String val = ((Properties) file).getProperty(path);
        return val != null ? Integer.parseInt(val) : def;
    }

    @Override
    public boolean contains(Object file, String path) {
        return ((Properties) file).containsKey(path);
    }

    @Override
    public String getFileName(Object file) {
        return "[Properties config]";
    }

    @Override
    public void callPunishmentEvent(Punishment punishment) {
        server.getEventManager().fireAndForget(new PunishmentEvent(punishment));
    }

    @Override
    public void callRevokePunishmentEvent(Punishment punishment, boolean massClear) {
        server.getEventManager().fireAndForget(new RevokePunishmentEvent(punishment, massClear));
    }

    @Override
    public boolean isOnlineMode() {
        return server.getConfiguration().isOnlineMode();
    }

    @Override
    public void notify(String perm, List<String> notification) {
        server.getAllPlayers().stream()
                .filter(p -> hasPerms(p, perm))
                .forEach(p -> notification.forEach(str -> sendMessage(p, str)));
    }

    @Override
    public void log(String msg) {
        plugin.getLogger().info(msg.replaceAll("&", ""));
    }

    @Override
    public boolean isUnitTesting() { return false; }
}
