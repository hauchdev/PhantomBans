package dev.hauch.phantomBans;

import dev.hauch.phantomBans.utils.Permissionable;
import dev.hauch.phantomBans.utils.Punishment;
import dev.hauch.phantomBans.utils.tabcompletion.TabCompleter;

import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

/**
 * The Method Interface is used to define universal actions which are dependent on the server software used.
 */
public interface MethodInterface {

    /**
     * Creates and load the different configuration files.
     */
    void loadFiles();

    /**
     * Request JSON from the <code>url</code> and extract the <code>key</code>.
     *
     * @param url the url
     * @param key the key
     * @return the value corresponding to the key
     */
    String getFromUrlJson(String url, String key);

    /**
     * Get the PhantomBans version.
     *
     * @return the version
     */
    String getVersion();

    /**
     * Get key from a config file at a <code>path</code>.
     *
     * @param file the file
     * @param path the path
     * @return the string array
     */
    String[] getKeys(Object file, String path);

    /**
     * Get the config.yml file.
     *
     * @return the config
     */
    Object getConfig();

    /**
     * Get the messages.yml file.
     *
     * @return the messages
     */
    Object getMessages();

    /**
     * Get the layouts.yml file.
     *
     * @return the layouts
     */
    Object getLayouts();

    /**
     * Set up metrics.
     */
    void setupMetrics();

    /**
     * Check if this is running on a proxy (BungeeCord/Velocity).
     *
     * @return true if running on a proxy
     */
    boolean isProxy();

    /**
     * Clear formatting codes from text.
     *
     * @param text the text
     * @return the cleared text
     */
    String clearFormatting(String text);

    /**
     * Get the plugin instance.
     *
     * @return the plugin
     */
    Object getPlugin();

    /**
     * Get the data folder.
     *
     * @return the data folder
     */
    File getDataFolder();

    /**
     * Register a command.
     *
     * @param cmd          the command name
     * @param permission   the permission required
     * @param tabCompleter the tab completer
     */
    void setCommandExecutor(String cmd, String permission, TabCompleter tabCompleter);

    /**
     * Send a message to a player.
     *
     * @param player the player
     * @param msg    the message
     */
    void sendMessage(Object player, String msg);

    /**
     * Get a player's name.
     *
     * @param player the player
     * @return the name
     */
    String getName(Object player);

    /**
     * Get a player's name from uuid.
     *
     * @param uuid the uuid
     * @return the name
     */
    String getName(String uuid);

    /**
     * Get a player's IP.
     *
     * @param player the player
     * @return the ip
     */
    String getIP(Object player);

    /**
     * Get a player's internal UUID.
     *
     * @param player the player
     * @return the uuid
     */
    String getInternUUID(Object player);

    /**
     * Get a player's internal UUID by name.
     *
     * @param player the player name
     * @return the uuid
     */
    String getInternUUID(String player);

    /**
     * Check if a player has a permission.
     *
     * @param player the player
     * @param perms  the permission
     * @return true if has permission
     */
    boolean hasPerms(Object player, String perms);

    /**
     * Get an offline permission player.
     *
     * @param name the name
     * @return the permissionable
     */
    Permissionable getOfflinePermissionPlayer(String name);

    /**
     * Check if a player is online.
     *
     * @param name the name
     * @return true if online
     */
    boolean isOnline(String name);

    /**
     * Get a player by name.
     *
     * @param name the name
     * @return the player object
     */
    Object getPlayer(String name);

    /**
     * Kick a player.
     *
     * @param player the player name
     * @param reason the reason
     */
    void kickPlayer(String player, String reason);

    /**
     * Get online players.
     *
     * @return array of player objects
     */
    Object[] getOnlinePlayers();

    /**
     * Schedule an async repeating task.
     *
     * @param runnable the task
     * @param delay    initial delay
     * @param period   period
     */
    void scheduleAsyncRep(Runnable runnable, long delay, long period);

    /**
     * Schedule an async task.
     *
     * @param runnable the task
     * @param delay    delay
     */
    void scheduleAsync(Runnable runnable, long delay);

    /**
     * Run an async task.
     *
     * @param runnable the task
     */
    void runAsync(Runnable runnable);

    /**
     * Run a sync task.
     *
     * @param runnable the task
     */
    void runSync(Runnable runnable);

    /**
     * Execute a console command.
     *
     * @param cmd the command
     */
    void executeCommand(String cmd);

    /**
     * Call chat event check for mute.
     *
     * @param player the player
     * @return true if cancelled
     */
    boolean callChat(Object player);

    /**
     * Call command event check for mute.
     *
     * @param player the player
     * @param cmd    the command
     * @return true if cancelled
     */
    boolean callCMD(Object player, String cmd);

    /**
     * Get MySQL config file.
     *
     * @return the mysql file
     */
    Object getMySQLFile();

    /**
     * Parse JSON from InputStreamReader.
     *
     * @param json the reader
     * @param key  the key
     * @return the value
     */
    String parseJSON(InputStreamReader json, String key);

    /**
     * Parse JSON string.
     *
     * @param json the json string
     * @param key  the key
     * @return the value
     */
    String parseJSON(String json, String key);

    // Config getters with defaults
    Boolean getBoolean(Object file, String path);
    String getString(Object file, String path);
    Long getLong(Object file, String path);
    Integer getInteger(Object file, String path);
    List<String> getStringList(Object file, String path);
    boolean getBoolean(Object file, String path, boolean def);
    String getString(Object file, String path, String def);
    long getLong(Object file, String path, long def);
    int getInteger(Object file, String path, int def);
    boolean contains(Object file, String path);
    String getFileName(Object file);

    /**
     * Call a punishment event.
     *
     * @param punishment the punishment
     */
    void callPunishmentEvent(Punishment punishment);

    /**
     * Call a revoke punishment event.
     *
     * @param punishment the punishment
     * @param massClear  whether it's a mass clear
     */
    void callRevokePunishmentEvent(Punishment punishment, boolean massClear);

    /**
     * Check if the server is in online mode.
     *
     * @return true if online mode
     */
    boolean isOnlineMode();

    /**
     * Notify players with a permission.
     *
     * @param perm         the permission
     * @param notification the notification messages
     */
    void notify(String perm, List<String> notification);

    /**
     * Log a message.
     *
     * @param msg the message
     */
    void log(String msg);

    /**
     * Whether this is a unit testing instance.
     *
     * @return true if unit testing
     */
    boolean isUnitTesting();
}
