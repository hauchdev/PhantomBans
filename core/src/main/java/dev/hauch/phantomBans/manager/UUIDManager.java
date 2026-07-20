package dev.hauch.phantomBans.manager;

import dev.hauch.phantomBans.MethodInterface;
import dev.hauch.phantomBans.Universal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Manages UUID resolution and caching.
 */
public class UUIDManager {

    private static UUIDManager instance = null;
    private FetcherMode mode;
    private final Map<String, String> activeUUIDs = new HashMap<>();

    public static synchronized UUIDManager get() {
        if (instance == null) instance = new UUIDManager();
        return instance;
    }

    public void setup() {
        MethodInterface mi = mi();
        if (mi.getBoolean(mi.getConfig(), "UUID-Fetcher.Dynamic", true)) {
            if (!mi.isOnlineMode()) {
                mode = FetcherMode.DISABLED;
            } else {
                if (Universal.get().isProxy()) {
                    mode = FetcherMode.MIXED;
                } else {
                    mode = FetcherMode.INTERN;
                }
            }
        } else {
            if (!mi.getBoolean(mi.getConfig(), "UUID-Fetcher.Enabled", true)) {
                mode = FetcherMode.DISABLED;
            } else if (mi.getBoolean(mi.getConfig(), "UUID-Fetcher.Intern", false)) {
                mode = FetcherMode.INTERN;
            } else {
                mode = FetcherMode.RESTFUL;
            }
        }
    }

    public String getInitialUUID(String name) {
        MethodInterface mi = mi();
        name = name.toLowerCase();
        if (mode == FetcherMode.DISABLED) return name;
        if (mode == FetcherMode.INTERN || mode == FetcherMode.MIXED) {
            String internUUID = mi.getInternUUID(name);
            if (mode == FetcherMode.INTERN || internUUID != null) return internUUID;
        }
        String uuid = null;
        try {
            uuid = askAPI(mi.getString(mi.getConfig(), "UUID-Fetcher.REST-API.URL"),
                    name, mi.getString(mi.getConfig(), "UUID-Fetcher.REST-API.Key"));
        } catch (IOException e) {
            System.out.println("Error -> " + e.getMessage());
            System.out.println("!! Failed fetching UUID of " + name);
            System.out.println("!! Could not connect to REST-API under "
                    + mi.getString(mi.getConfig(), "UUID-Fetcher.REST-API.URL"));
        }
        if (uuid == null) {
            System.out.println("Trying to fetch UUID from BackUp-API...");
            try {
                uuid = askAPI(mi.getString(mi.getConfig(), "UUID-Fetcher.BackUp-API.URL"),
                        name, mi.getString(mi.getConfig(), "UUID-Fetcher.BackUp-API.Key"));
            } catch (IOException e) {
                System.out.println("!! Failed fetching UUID of " + name);
            }
        }
        if (uuid == null) {
            System.out.println("!! Warning we have not been able to fetch the UUID of the Player " + name);
            System.out.println("!! Make sure that the name is spelled correctly and if it is change your UUID-Fetcher settings!");
        }
        return uuid;
    }

    public void supplyInternUUID(String name, UUID uuid) {
        if (mode == FetcherMode.INTERN || mode == FetcherMode.MIXED) {
            activeUUIDs.put(name.toLowerCase(), uuid.toString().replace("-", ""));
        }
    }

    public UUID fromString(String uuid) {
        if (!uuid.contains("-") && uuid.length() == 32) {
            uuid = uuid.replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                    "$1-$2-$3-$4-$5");
        }
        return uuid.length() == 36 && uuid.contains("-") ? UUID.fromString(uuid) : null;
    }

    public String getUUID(String name) {
        String inMemoryUuid = getInMemoryUUID(name);
        return (inMemoryUuid != null) ? inMemoryUuid : getInitialUUID(name);
    }

    public String getInMemoryUUID(String name) {
        return activeUUIDs.get(name.toLowerCase());
    }

    public String getInMemoryName(String uuid) {
        for (Map.Entry<String, String> rs : activeUUIDs.entrySet()) {
            if (rs.getValue().equalsIgnoreCase(uuid)) return rs.getKey();
        }
        return null;
    }

    public String getNameFromUUID(String uuid, boolean forceInitial) {
        MethodInterface mi = mi();
        if (mode == FetcherMode.DISABLED) return uuid;
        if (mode == FetcherMode.INTERN || mode == FetcherMode.MIXED) {
            String internName = mi.getName(uuid);
            if (mode == FetcherMode.INTERN || internName != null) return internName;
        }
        if (!forceInitial) {
            String inMemoryName = getInMemoryName(uuid);
            if (inMemoryName != null) return inMemoryName;
        }
        try (Scanner scanner = new Scanner(new URL("https://api.mojang.com/user/profiles/" + uuid + "/names").openStream(), "UTF-8")) {
            String s = scanner.useDelimiter("\\A").next();
            s = s.substring(s.lastIndexOf('{'), s.lastIndexOf('}') + 1);
            return mi.parseJSON(s, "name");
        } catch (Exception exc) {
            return null;
        }
    }

    private String askAPI(String url, String name, String key) throws IOException {
        MethodInterface mi = mi();
        name = name.toLowerCase();
        HttpURLConnection request = (HttpURLConnection) new URL(url.replaceAll("%NAME%", name)
                .replaceAll("%TIMESTAMP%", new Date().getTime() + "")).openConnection();
        request.connect();
        String uuid = mi.parseJSON(new InputStreamReader(request.getInputStream()), key);
        if (uuid == null) {
            System.out.println("!! Failed fetching UUID of " + name);
            System.out.println("!! Could not find key '" + key + "' in the servers response");
            System.out.println("!! Response: " + request.getResponseMessage());
        } else {
            activeUUIDs.put(name, uuid);
        }
        return uuid;
    }

    public FetcherMode getMode() { return mode; }

    private MethodInterface mi() {
        return Universal.get().getMethods();
    }

    public enum FetcherMode {
        DISABLED, INTERN, MIXED, RESTFUL
    }
}
