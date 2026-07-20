package dev.hauch.phantomBans.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.hauch.phantomBans.MethodInterface;
import dev.hauch.phantomBans.Universal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Manages update checking via the GitHub Releases API.
 * <p>
 * Fetches the latest release tag from {@code Hauchdev/PhantomBans}
 * (configurable in config.yml) and compares it with the current plugin version.
 * </p>
 */
public class UpdateManager {

    private static UpdateManager instance = null;

    /** Cache for the latest version string fetched from GitHub. */
    private String latestVersion = null;
    private boolean checked = false;

    public static synchronized UpdateManager get() {
        if (instance == null) instance = new UpdateManager();
        return instance;
    }

    /**
     * Check for updates against the GitHub Releases API.
     *
     * @return a human-readable update status message
     */
    public String check() {
        MethodInterface mi = Universal.get().getMethods();
        if (mi.isUnitTesting()) return "";

        String repo = mi.getString(mi.getConfig(), "UpdateChecker.Repository",
                "Hauchdev/PhantomBans");
        String apiUrl = "https://api.github.com/repos/" + repo + "/releases/latest";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                checked = true;
                return "<red>Failed to check for updates (HTTP " + responseCode + ")";
            }

            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                String tagName = json.get("tag_name").getAsString();
                latestVersion = tagName.startsWith("v") ? tagName.substring(1) : tagName;
                checked = true;

                String currentVersion = mi.getVersion();
                if (currentVersion.equals(latestVersion)) {
                    return "<green>You have the newest version (<white>" + latestVersion + "</white>)";
                } else {
                    return "<yellow>There is a new version available! "
                            + "<white>[" + latestVersion + "]</white> "
                            + "<gray>— Download at <underlined><click:open_url:'https://github.com/"
                            + repo + "/releases/latest'>github.com/" + repo + "</click></underlined>";
                }
            }
        } catch (IOException e) {
            checked = true;
            Universal.get().debug("Failed to check for updates: " + e.getMessage());
            return "<red>Failed to check for updates — could not connect to GitHub API";
        }
    }

    /**
     * Returns the latest version string fetched from GitHub, or {@code null}
     * if the check hasn't been performed yet or failed.
     */
    public String getLatestVersion() {
        return latestVersion;
    }

    /**
     * Whether an update check has been attempted at least once.
     */
    public boolean hasChecked() {
        return checked;
    }
}
