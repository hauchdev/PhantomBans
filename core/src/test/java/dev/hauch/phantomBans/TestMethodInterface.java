package dev.hauch.phantomBans;

import dev.hauch.phantomBans.utils.Permissionable;
import dev.hauch.phantomBans.utils.Punishment;
import dev.hauch.phantomBans.utils.tabcompletion.TabCompleter;

import java.io.File;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Test implementation of MethodInterface for unit testing.
 */
public class TestMethodInterface implements MethodInterface {

    private final Map<String, Object> config = new HashMap<>();
    private final Map<String, Object> messages = new HashMap<>();
    private final Map<String, Object> layouts = new HashMap<>();
    private final Map<String, Object> mysql = new HashMap<>();

    public TestMethodInterface() {
        // Default config values
        config.put("UseMySQL", false);
        config.put("Debug", false);
        config.put("DetailedEnableMessage", false);
        config.put("DetailedDisableMessage", false);
        config.put("Disable Prefix", false);
        config.put("DefaultReason", "Misconduct");
        config.put("DateFormat", "dd.MM.yyyy-HH:mm");
        config.put("LockdownOnError", false);
        config.put("EnableAllPermissionNodes", false);
        config.put("TimeDiff", 0);
        config.put("Log Purge Days", 30);
        config.put("MuteCommands", Arrays.asList("/msg", "/tell", "/w", "/reply", "/r", "/me", "/action"));
        config.put("ExemptPlayers", Collections.emptyList());
        config.put("UUID-Fetcher.Dynamic", false);
        config.put("UUID-Fetcher.Enabled", false);
        config.put("UUID-Fetcher.Intern", false);
        config.put("UUID-Fetcher.REST-API.URL", "");
        config.put("UUID-Fetcher.REST-API.Key", "id");
        config.put("UUID-Fetcher.BackUp-API.URL", "");
        config.put("UUID-Fetcher.BackUp-API.Key", "uuid");

        // Default message values
        messages.put("General.Prefix", "&8[&cPhantomBans&8]");
        messages.put("General.NoPerms", "&cYou don't have permission to use this command!");
        messages.put("General.FailedFetch", "&cFailed to fetch %NAME%'s UUID!");
        messages.put("General.LayoutNotFound", "&cLayout %NAME% not found!");
        messages.put("General.TimeLayoutD", "%DD% days %HH% hours %MM% minutes %SS% seconds");
        messages.put("General.TimeLayoutH", "%HH% hours %MM% minutes %SS% seconds");
        messages.put("General.TimeLayoutM", "%MM% minutes %SS% seconds");
        messages.put("General.TimeLayoutS", "%SS% seconds");
        messages.put("Ban.Usage", "&cUsage &8» &7&o/ban [Name] [Reason]");
        messages.put("Ban.Done", "&c&o%NAME% &7was successfully banned!");
        messages.put("Tempban.Usage", "&cUsage &8» &7&o/tempban [Name] [Duration] [Reason]");
        messages.put("Tempban.Done", "&c&o%NAME% &7was successfully temp-banned!");
        messages.put("Mute.Usage", "&cUsage &8» &7&o/mute [Name] [Reason]");
        messages.put("Mute.Done", "&c&o%NAME% &7was successfully muted!");
        messages.put("Ipban.Usage", "&cUsage &8» &7&o/ipban [Name/IP] [Reason]");
        messages.put("Note.Usage", "&cUsage &8» &7&o/note [Name] [Note]");
        messages.put("Kick.Usage", "&cUsage &8» &7&o/kick [Name] [Reason]");
        messages.put("Warn.Usage", "&cUsage &8» &7&o/warn [Name] [Reason]");
        messages.put("Check.Usage", "&cUsage &8» &7&o/check [Name]");
        messages.put("Check.Cached", "&a&lCACHED");
        messages.put("Check.NotCached", "&c&lNOT CACHED");
        messages.put("History.Usage", "&cUsage &8» &7&o/history [Name] <Page>");
        messages.put("Banlist.Usage", "&cUsage &8» &7&o/banlist <Page>");
        messages.put("UnBan.Usage", "&cUsage &8» &7&o/unban [Name]");
        messages.put("UnBan.Done", "&c&o%NAME% &7was successfully unbanned!");
        messages.put("UnBan.NotFound", "&c%NAME% is not banned!");

        // Default layout values
        layouts.put("Time.example", Arrays.asList("1d", "7d", "30d"));
    }

    @Override
    public void loadFiles() {}

    @Override
    public String getFromUrlJson(String url, String key) { return null; }

    @Override
    public String getVersion() { return "1.0-TEST"; }

    @Override
    public String[] getKeys(Object file, String path) { return new String[0]; }

    @Override
    public Object getConfig() { return config; }

    @Override
    public Object getMessages() { return messages; }

    @Override
    public Object getLayouts() { return layouts; }

    @Override
    public void setupMetrics() {}

    @Override
    public boolean isProxy() { return false; }

    @Override
    public String clearFormatting(String text) { return text.replaceAll("§[0-9a-fk-or]", ""); }

    @Override
    public Object getPlugin() { return null; }

    @Override
    public File getDataFolder() { return new File("target/test-data"); }

    @Override
    public void setCommandExecutor(String cmd, String permission, TabCompleter tabCompleter) {}

    @Override
    public void sendMessage(Object player, String msg) {}

    @Override
    public String getName(Object player) { return player != null ? player.toString() : "Console"; }

    @Override
    public String getName(String uuid) { return uuid; }

    @Override
    public String getIP(Object player) { return "127.0.0.1"; }

    @Override
    public String getInternUUID(Object player) { return "00000000000000000000000000000000"; }

    @Override
    public String getInternUUID(String player) { return "00000000000000000000000000000000"; }

    @Override
    public boolean hasPerms(Object player, String perms) { return true; }

    @Override
    public Permissionable getOfflinePermissionPlayer(String name) { return perm -> true; }

    @Override
    public boolean isOnline(String name) { return false; }

    @Override
    public Object getPlayer(String name) { return null; }

    @Override
    public void kickPlayer(String player, String reason) {}

    @Override
    public Object[] getOnlinePlayers() { return new Object[0]; }

    @Override
    public void scheduleAsyncRep(Runnable rn, long l1, long l2) { rn.run(); }

    @Override
    public void scheduleAsync(Runnable rn, long l1) { rn.run(); }

    @Override
    public void runAsync(Runnable rn) { rn.run(); }

    @Override
    public void runSync(Runnable rn) { rn.run(); }

    @Override
    public void executeCommand(String cmd) {}

    @Override
    public boolean callChat(Object player) { return false; }

    @Override
    public boolean callCMD(Object player, String cmd) { return false; }

    @Override
    public Object getMySQLFile() { return mysql; }

    @Override
    public String parseJSON(InputStreamReader json, String key) { return null; }

    @Override
    public String parseJSON(String json, String key) { return null; }

    // Config getters using the maps
    @Override
    public Boolean getBoolean(Object file, String path) {
        Object val = ((Map<String, Object>) file).get(path);
        return val instanceof Boolean ? (Boolean) val : false;
    }

    @Override
    public String getString(Object file, String path) {
        Object val = ((Map<String, Object>) file).get(path);
        return val instanceof String ? (String) val : null;
    }

    @Override
    public Long getLong(Object file, String path) {
        Object val = ((Map<String, Object>) file).get(path);
        return val instanceof Number ? ((Number) val).longValue() : 0L;
    }

    @Override
    public Integer getInteger(Object file, String path) {
        Object val = ((Map<String, Object>) file).get(path);
        return val instanceof Number ? ((Number) val).intValue() : 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getStringList(Object file, String path) {
        Object val = ((Map<String, Object>) file).get(path);
        return val instanceof List ? (List<String>) val : Collections.emptyList();
    }

    @Override
    public boolean getBoolean(Object file, String path, boolean def) {
        Object val = ((Map<String, Object>) file).get(path);
        return val instanceof Boolean ? (Boolean) val : def;
    }

    @Override
    public String getString(Object file, String path, String def) {
        Object val = ((Map<String, Object>) file).get(path);
        return val instanceof String ? (String) val : def;
    }

    @Override
    public long getLong(Object file, String path, long def) {
        Object val = ((Map<String, Object>) file).get(path);
        return val instanceof Number ? ((Number) val).longValue() : def;
    }

    @Override
    public int getInteger(Object file, String path, int def) {
        Object val = ((Map<String, Object>) file).get(path);
        return val instanceof Number ? ((Number) val).intValue() : def;
    }

    @Override
    public boolean contains(Object file, String path) {
        return ((Map<String, Object>) file).containsKey(path);
    }

    @Override
    public String getFileName(Object file) { return "test-config"; }

    @Override
    public void callPunishmentEvent(Punishment punishment) {}

    @Override
    public void callRevokePunishmentEvent(Punishment punishment, boolean massClear) {}

    @Override
    public boolean isOnlineMode() { return true; }

    @Override
    public void notify(String perm, List<String> notification) {}

    @Override
    public void log(String msg) {}

    @Override
    public boolean isUnitTesting() { return true; }
}
