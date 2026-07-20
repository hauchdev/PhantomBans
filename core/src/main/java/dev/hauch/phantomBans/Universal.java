package dev.hauch.phantomBans;

import com.google.gson.Gson;
import dev.hauch.phantomBans.manager.*;
import dev.hauch.phantomBans.utils.Command;
import dev.hauch.phantomBans.utils.InterimData;
import dev.hauch.phantomBans.utils.Punishment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * This is the server independent entry point of the plugin.
 */
public class Universal {

    private static Universal instance = null;
    private static boolean redis = false;

    private final Map<String, String> ips = new HashMap<>();
    private MethodInterface mi;
    private LogManager logManager;
    private final Gson gson = new Gson();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer =
            LegacyComponentSerializer.builder().character('§').hexColors().build();

    public static void setRedis(boolean redis) {
        Universal.redis = redis;
    }

    /**
     * Get the universal instance.
     *
     * @return the universal instance
     */
    public static Universal get() {
        if (instance == null) {
            instance = new Universal();
        }
        return instance;
    }

    /**
     * Initially sets up the plugin.
     *
     * @param mi the method interface
     */
    public void setup(MethodInterface mi) {
        this.mi = mi;
        mi.loadFiles();
        logManager = new LogManager();
        UUIDManager.get().setup();
        try {
            DatabaseManager.get().setup(mi.getBoolean(mi.getConfig(), "UseMySQL", false));
        } catch (Exception ex) {
            log("Failed enabling database-manager...");
            debugException(ex);
        }
        mi.setupMetrics();
        PunishmentManager.get().setup();
        for (Command command : Command.values()) {
            for (String commandName : command.getNames()) {
                mi.setCommandExecutor(commandName, command.getPermission(), command.getTabCompleter());
            }
        }

        // Check for updates via GitHub Releases
        String upt = UpdateManager.get().check();

        if (mi.getBoolean(mi.getConfig(), "DetailedEnableMessage", true)) {
            Component enableMsg = miniMessage.deserialize(
                    "\n \n<dark_gray>[]=====[<gray>Enabling PhantomBans<dark_gray>]=====[]"
                    + "\n<dark_gray>| <red>Information:"
                    + "\n<dark_gray>| <red>Name: <gray>PhantomBans"
                    + "\n<dark_gray>| <red>Developer: <gray>Hauchdev"
                    + "\n<dark_gray>| <red>Version: <gray>" + mi.getVersion()
                    + "\n<dark_gray>| <red>Storage: <gray>" + (DatabaseManager.get().isUseMySQL() ? "MySQL (external)" : "HSQLDB (local)")
                    + "\n<dark_gray>| <red>Update:"
                    + "\n<dark_gray>| <gray>" + upt.replace('§', '&')
                    + "\n<dark_gray>[]================================[]\n ");
            mi.log(legacySerializer.serialize(enableMsg));
        } else {
            Component simpleMsg = miniMessage.deserialize(
                    "<red>Enabling PhantomBans on Version <gray>" + mi.getVersion());
            mi.log(legacySerializer.serialize(simpleMsg));
            mi.log(legacySerializer.serialize(
                    miniMessage.deserialize("<red>Coded by <gray>Hauchdev")));
        }
    }

    /**
     * Shutdown the plugin.
     */
    public void shutdown() {
        DatabaseManager.get().shutdown();
        if (mi.getBoolean(mi.getConfig(), "DetailedDisableMessage", true)) {
            Component disableMsg = miniMessage.deserialize(
                    "\n \n<dark_gray>[]=====[<gray>Disabling PhantomBans<dark_gray>]=====[]"
                    + "\n<dark_gray>| <red>Information:"
                    + "\n<dark_gray>| <red>Name: <gray>PhantomBans"
                    + "\n<dark_gray>| <red>Developer: <gray>Hauchdev"
                    + "\n<dark_gray>| <red>Version: <gray>" + getMethods().getVersion()
                    + "\n<dark_gray>| <red>Storage: <gray>" + (DatabaseManager.get().isUseMySQL() ? "MySQL (external)" : "HSQLDB (local)")
                    + "\n<dark_gray>[]================================[]\n ");
            mi.log(legacySerializer.serialize(disableMsg));
        } else {
            mi.log(legacySerializer.serialize(
                    miniMessage.deserialize("<red>Disabling PhantomBans on Version <gray>" + getMethods().getVersion())));
            mi.log(legacySerializer.serialize(
                    miniMessage.deserialize("<red>Coded by Hauchdev")));
        }
    }

    /**
     * Get the method interface.
     *
     * @return the method interface
     */
    public MethodInterface getMethods() {
        return mi;
    }

    /**
     * Check if running on a proxy.
     *
     * @return true if proxy
     */
    public boolean isProxy() {
        return mi.isProxy();
    }

    public Map<String, String> getIps() {
        return ips;
    }

    public static boolean isRedis() {
        return redis;
    }

    public Gson getGson() {
        return gson;
    }

    /**
     * Get content from a URL.
     *
     * @param surl the URL
     * @return the response string
     */
    public String getFromURL(String surl) {
        String response = null;
        try {
            URL url = new URL(surl);
            Scanner s = new Scanner(url.openStream());
            if (s.hasNext()) {
                response = s.next();
                s.close();
            }
        } catch (IOException exc) {
            debug("!! Failed to connect to URL: " + surl);
        }
        return response;
    }

    /**
     * Check if a command is a mute command.
     *
     * @param cmd the command
     * @return true if mute command
     */
    public boolean isMuteCommand(String cmd) {
        return isMuteCommand(cmd, getMethods().getStringList(getMethods().getConfig(), "MuteCommands"));
    }

    boolean isMuteCommand(String cmd, List<String> muteCommands) {
        String[] words = cmd.split(" ");
        if (words[0].indexOf(':') != -1) {
            words[0] = words[0].split(":", 2)[1];
        }
        for (String muteCommand : muteCommands) {
            if (muteCommandMatches(words, muteCommand)) {
                return true;
            }
        }
        return false;
    }

    boolean muteCommandMatches(String[] commandWords, String muteCommand) {
        if (commandWords[0].equalsIgnoreCase(muteCommand)) {
            return true;
        }
        if (muteCommand.indexOf(' ') != -1) {
            String[] muteCommandWords = muteCommand.split(" ");
            if (muteCommandWords.length > commandWords.length) {
                return false;
            }
            for (int n = 0; n < muteCommandWords.length; n++) {
                if (!muteCommandWords[n].equalsIgnoreCase(commandWords[n])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Check if a player is exempt.
     *
     * @param name the name
     * @return true if exempt
     */
    public boolean isExemptPlayer(String name) {
        List<String> exempt = getMethods().getStringList(getMethods().getConfig(), "ExemptPlayers");
        if (exempt != null) {
            for (String str : exempt) {
                if (name.equalsIgnoreCase(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Handle a player connection.
     *
     * @param name the player name
     * @param ip   the player IP
     * @return kick message or null
     */
    public String callConnection(String name, String ip) {
        name = name.toLowerCase();
        String uuid = UUIDManager.get().getUUID(name);
        if (uuid == null) return "[PhantomBans] Failed to fetch your UUID";

        if (ip != null) {
            getIps().remove(name);
            getIps().put(name, ip);
        }

        InterimData interimData = PunishmentManager.get().load(name, uuid, ip);
        if (interimData == null) {
            if (getMethods().getBoolean(mi.getConfig(), "LockdownOnError", true)) {
                return "[PhantomBans] Failed to load player data!";
            } else {
                return null;
            }
        }

        Punishment pt = interimData.getBan();
        if (pt == null) {
            interimData.accept();
            return null;
        }
        return pt.getLayoutBSN();
    }

    /**
     * Check if a player has permission with super-permission support.
     *
     * @param player the player
     * @param perms  the permission
     * @return true if has permission
     */
    public boolean hasPerms(Object player, String perms) {
        if (mi.hasPerms(player, perms)) {
            return true;
        }
        if (mi.getBoolean(mi.getConfig(), "EnableAllPermissionNodes", false)) {
            while (perms.contains(".")) {
                perms = perms.substring(0, perms.lastIndexOf('.'));
                if (mi.hasPerms(player, perms + ".all")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Log a message.
     *
     * @param msg the message
     */
    public void log(String msg) {
        mi.log("§8[§cPhantomBans§8] §7" + msg);
        debugToFile(msg);
    }

    /**
     * Log a debug message.
     *
     * @param msg the message
     */
    public void debug(Object msg) {
        if (mi.getBoolean(mi.getConfig(), "Debug", false)) {
            mi.log("§8[§cPhantomBans§8] §cDebug: §7" + msg.toString());
        }
        debugToFile(msg);
    }

    public void debugException(Exception exc) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exc.printStackTrace(pw);
        debug(sw.toString());
    }

    public void debugSqlException(SQLException ex) {
        if (mi.getBoolean(mi.getConfig(), "Debug", false)) {
            debug("§7An error has occurred with the database, the error code is: '" + ex.getErrorCode() + "'");
            debug("§7The state of the sql is: " + ex.getSQLState());
            debug("§7Error message: " + ex.getMessage());
        }
        debugException(ex);
    }

    private void debugToFile(Object msg) {
        File debugFile = new File(mi.getDataFolder(), "logs/latest.log");
        if (!debugFile.exists()) {
            try {
                debugFile.getParentFile().mkdirs();
                debugFile.createNewFile();
            } catch (IOException ex) {
                System.out.print("An error has occurred creating the 'latest.log' file again, check your server.");
                System.out.print("Error message" + ex.getMessage());
            }
        } else {
            logManager.checkLastLog(false);
        }
        try {
            FileUtils.writeStringToFile(debugFile, "[" + new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()) + "] "
                    + mi.clearFormatting(msg.toString()) + "\n", "UTF8", true);
        } catch (IOException ex) {
            System.out.print("An error has occurred writing to 'latest.log' file.");
            System.out.print(ex.getMessage());
        }
    }
}
