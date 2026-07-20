package dev.hauch.phantomBans.bukkit;

import dev.hauch.phantomBans.MethodInterface;
import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.bukkit.event.PunishmentEvent;
import dev.hauch.phantomBans.bukkit.event.RevokePunishmentEvent;
import dev.hauch.phantomBans.bukkit.listener.CommandReceiver;
import dev.hauch.phantomBans.manager.DatabaseManager;
import dev.hauch.phantomBans.manager.PunishmentManager;
import dev.hauch.phantomBans.manager.UUIDManager;
import dev.hauch.phantomBans.utils.Permissionable;
import dev.hauch.phantomBans.utils.Punishment;
import dev.hauch.phantomBans.utils.tabcompletion.TabCompleter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Bukkit implementation of MethodInterface.
 */
public class BukkitMethods implements MethodInterface {

    private final File configFile = new File(getDataFolder(), "config.yml");
    private final File messageFile = new File(getDataFolder(), "Messages.yml");
    private final File layoutFile = new File(getDataFolder(), "Layouts.yml");
    private final File mysqlFile = new File(getDataFolder(), "MySQL.yml");
    private YamlConfiguration config;
    private YamlConfiguration messages;
    private YamlConfiguration layouts;
    private YamlConfiguration mysql;

    @Override
    public void loadFiles() {
        if (!configFile.exists()) {
            getPlugin().saveResource("config.yml", true);
        }
        if (!messageFile.exists()) {
            getPlugin().saveResource("Messages.yml", true);
        }
        if (!layoutFile.exists()) {
            getPlugin().saveResource("Layouts.yml", true);
        }
        try {
            config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));
            messages = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(messageFile), StandardCharsets.UTF_8));
            layouts = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(layoutFile), StandardCharsets.UTF_8));
            if (mysqlFile.exists()) {
                mysql = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(mysqlFile), StandardCharsets.UTF_8));
            } else {
                mysql = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));
            }
        } catch (FileNotFoundException exc) {
            Universal.get().debugException(exc);
        }
    }

    @Override
    public String getFromUrlJson(String url, String key) {
        try {
            HttpURLConnection request = (HttpURLConnection) new URL(url).openConnection();
            request.connect();
            JSONParser jp = new JSONParser();
            JSONObject json = (JSONObject) jp.parse(new InputStreamReader(request.getInputStream()));
            String[] keys = key.split("\\|");
            for (int i = 0; i < keys.length - 1; i++) {
                json = (JSONObject) json.get(keys[i]);
            }
            return json.get(keys[keys.length - 1]).toString();
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
        return ((YamlConfiguration) file).getConfigurationSection(path).getKeys(false).toArray(new String[0]);
    }

    @Override
    public YamlConfiguration getConfig() { return config; }

    @Override
    public YamlConfiguration getMessages() { return messages; }

    @Override
    public YamlConfiguration getLayouts() { return layouts; }

    @Override
    public void setupMetrics() {
        Metrics metrics = new Metrics(getPlugin(), 0); // Replace 0 with actual bStats plugin ID
        metrics.addCustomChart(new SimplePie("MySQL",
                () -> DatabaseManager.get().isUseMySQL() ? "yes" : "no"));
    }

    @Override
    public boolean isProxy() { return false; }

    @Override
    public String clearFormatting(String text) {
        return ChatColor.stripColor(text);
    }

    @Override
    public PhantomBans getPlugin() {
        return PhantomBans.get();
    }

    @Override
    public File getDataFolder() {
        return getPlugin().getDataFolder();
    }

    @Override
    public void setCommandExecutor(String cmd, String permission, TabCompleter tabCompleter) {
        PluginCommand command = getPlugin().getCommand(cmd);
        if (command != null) {
            command.setExecutor(CommandReceiver.get());
            if (tabCompleter != null) {
                command.setTabCompleter((commandSender, c, s, args) -> {
                    if (permission != null && !hasPerms(commandSender, permission))
                        return Collections.emptyList();
                    return tabCompleter.onTabComplete(commandSender, args);
                });
            }
        } else {
            System.out.println("PhantomBans >> Failed to register command " + cmd);
        }
    }

    @Override
    public void sendMessage(Object player, String msg) {
        ((CommandSender) player).sendMessage(msg);
    }

    @Override
    public boolean hasPerms(Object player, String perms) {
        return ((CommandSender) player).hasPermission(perms);
    }

    @Override
    public Permissionable getOfflinePermissionPlayer(String name) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if (player == null || !player.hasPlayedBefore())
            return permission -> false;
        return permission -> player.isOp();
    }

    @Override
    public boolean isOnline(String name) {
        return Bukkit.getOfflinePlayer(name).isOnline();
    }

    @Override
    public Player getPlayer(String name) {
        return Bukkit.getPlayer(name);
    }

    @Override
    public void kickPlayer(String player, String reason) {
        Player p = getPlayer(player);
        if (p != null && p.isOnline()) {
            p.kickPlayer(reason);
        }
    }

    @Override
    public Player[] getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().toArray(new Player[0]);
    }

    @Override
    public void scheduleAsyncRep(Runnable rn, long l1, long l2) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), rn, l1, l2);
    }

    @Override
    public void scheduleAsync(Runnable rn, long l1) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), rn, l1);
    }

    @Override
    public void runAsync(Runnable rn) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), rn);
    }

    @Override
    public void runSync(Runnable rn) {
        Bukkit.getScheduler().runTask(getPlugin(), rn);
    }

    @Override
    public void executeCommand(String cmd) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    @Override
    public String getName(Object player) {
        return ((CommandSender) player).getName();
    }

    @Override
    public String getName(String uuid) {
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
    }

    @Override
    public String getIP(Object player) {
        return ((Player) player).getAddress().getHostName();
    }

    @Override
    public String getInternUUID(Object player) {
        return player instanceof OfflinePlayer ? ((OfflinePlayer) player).getUniqueId().toString().replaceAll("-", "") : "none";
    }

    @Override
    public String getInternUUID(String player) {
        return Bukkit.getOfflinePlayer(player).getUniqueId().toString().replaceAll("-", "");
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
    public YamlConfiguration getMySQLFile() { return mysql; }

    @Override
    public String parseJSON(InputStreamReader json, String key) {
        try {
            return ((JSONObject) new JSONParser().parse(json)).get(key).toString();
        } catch (ParseException | IOException e) {
            return null;
        }
    }

    @Override
    public String parseJSON(String json, String key) {
        try {
            return ((JSONObject) new JSONParser().parse(json)).get(key).toString();
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public Boolean getBoolean(Object file, String path) { return ((YamlConfiguration) file).getBoolean(path); }

    @Override
    public String getString(Object file, String path) { return ((YamlConfiguration) file).getString(path); }

    @Override
    public Long getLong(Object file, String path) { return ((YamlConfiguration) file).getLong(path); }

    @Override
    public Integer getInteger(Object file, String path) { return ((YamlConfiguration) file).getInt(path); }

    @Override
    public List<String> getStringList(Object file, String path) { return ((YamlConfiguration) file).getStringList(path); }

    @Override
    public boolean getBoolean(Object file, String path, boolean def) { return ((YamlConfiguration) file).getBoolean(path, def); }

    @Override
    public String getString(Object file, String path, String def) { return ((YamlConfiguration) file).getString(path, def); }

    @Override
    public long getLong(Object file, String path, long def) { return ((YamlConfiguration) file).getLong(path, def); }

    @Override
    public int getInteger(Object file, String path, int def) { return ((YamlConfiguration) file).getInt(path, def); }

    @Override
    public boolean contains(Object file, String path) { return ((YamlConfiguration) file).contains(path); }

    @Override
    public String getFileName(Object file) { return ((YamlConfiguration) file).getName(); }

    @Override
    public void callPunishmentEvent(Punishment punishment) {
        runSync(() -> Bukkit.getPluginManager().callEvent(new PunishmentEvent(punishment)));
    }

    @Override
    public void callRevokePunishmentEvent(Punishment punishment, boolean massClear) {
        runSync(() -> Bukkit.getPluginManager().callEvent(new RevokePunishmentEvent(punishment, massClear)));
    }

    @Override
    public boolean isOnlineMode() { return Bukkit.getOnlineMode(); }

    @Override
    public void notify(String perm, List<String> notification) {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> hasPerms(player, perm))
                .forEach(player -> notification.forEach(str -> sendMessage(player, str)));
    }

    @Override
    public void log(String msg) {
        Bukkit.getServer().getConsoleSender().sendMessage(msg.replaceAll("&", "§"));
    }

    @Override
    public boolean isUnitTesting() { return false; }
}
