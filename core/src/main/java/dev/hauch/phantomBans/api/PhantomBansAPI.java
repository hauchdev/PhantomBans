package dev.hauch.phantomBans.api;

import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.manager.PunishmentManager;
import dev.hauch.phantomBans.manager.TimeManager;
import dev.hauch.phantomBans.manager.UUIDManager;
import dev.hauch.phantomBans.utils.Punishment;
import dev.hauch.phantomBans.utils.PunishmentType;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * PhantomBansAPI - Main entry point for other plugins to integrate with PhantomBans.
 * <p>
 * Usage:
 * <pre>
 * // Listen for events
 * PhantomBansAPI.addListener(new PunishmentListener() {
 *     public void onPunish(PunishEvent e) {
 *         Bukkit.broadcastMessage(e.getPlayerName() + " was banned!");
 *     }
 * });
 *
 * // Check if a player is banned
 * boolean banned = PhantomBansAPI.isBanned(playerUuid);
 *
 * // Get active punishment
 * Punishment mute = PhantomBansAPI.getActiveMute(playerUuid);
 * </pre>
 */
public class PhantomBansAPI {

    private static final List<PunishmentListener> listeners = new CopyOnWriteArrayList<>();

    private PhantomBansAPI() {} // Utility class

    // ── Listener Registration ─────────────────────────────────────────── //

    /**
     * Register a listener to receive punishment events.
     *
     * @param listener the listener to register
     */
    public static void addListener(PunishmentListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Unregister a previously registered listener.
     *
     * @param listener the listener to unregister
     */
    public static void removeListener(PunishmentListener listener) {
        listeners.remove(listener);
    }

    /**
     * Get all registered listeners.
     *
     * @return unmodifiable list of listeners
     */
    public static List<PunishmentListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    // ── Event Firing (internal use) ──────────────────────────────────── //

    /**
     * Fire a punish event to all registered listeners.
     *
     * @param punishment the punishment
     * @param silent     whether silent
     */
    public static void firePunishEvent(Punishment punishment, boolean silent) {
        if (listeners.isEmpty()) return;
        PunishEvent event = new PunishEvent(punishment, silent);
        for (PunishmentListener listener : listeners) {
            try {
                listener.onPunish(event);
                if (punishment.getType() == PunishmentType.KICK) {
                    listener.onKick(event);
                }
            } catch (Exception e) {
                Universal.get().debug("Exception in PunishmentListener.onPunish: " + e.getMessage());
            }
        }
    }

    /**
     * Fire a revoke event to all registered listeners.
     *
     * @param punishment the punishment
     * @param massClear  whether mass clear
     * @param revokedBy  who revoked it
     */
    public static void fireRevokeEvent(Punishment punishment, boolean massClear, String revokedBy) {
        if (listeners.isEmpty()) return;
        RevokePunishEvent event = new RevokePunishEvent(punishment, massClear, revokedBy);
        for (PunishmentListener listener : listeners) {
            try {
                listener.onRevoke(event);
            } catch (Exception e) {
                Universal.get().debug("Exception in PunishmentListener.onRevoke: " + e.getMessage());
            }
        }
    }

    // ── Query Methods ────────────────────────────────────────────────── //

    /**
     * Check if a player is currently banned.
     *
     * @param uuid the player's UUID
     * @return true if the player has an active ban
     */
    public static boolean isBanned(String uuid) {
        return PunishmentManager.get().isBanned(uuid);
    }

    /**
     * Check if a player is currently muted.
     *
     * @param uuid the player's UUID
     * @return true if the player has an active mute
     */
    public static boolean isMuted(String uuid) {
        return PunishmentManager.get().isMuted(uuid);
    }

    /**
     * Get the active ban for a player, or null if not banned.
     *
     * @param uuid the player's UUID
     * @return the ban punishment, or null
     */
    public static Punishment getActiveBan(String uuid) {
        return PunishmentManager.get().getBan(uuid);
    }

    /**
     * Get the active mute for a player, or null if not muted.
     *
     * @param uuid the player's UUID
     * @return the mute punishment, or null
     */
    public static Punishment getActiveMute(String uuid) {
        return PunishmentManager.get().getMute(uuid);
    }

    /**
     * Get a list of active warnings for a player.
     *
     * @param uuid the player's UUID
     * @return list of active warning punishments
     */
    public static List<Punishment> getWarnings(String uuid) {
        return PunishmentManager.get().getWarns(uuid);
    }

    /**
     * Get the punishment history for a player.
     *
     * @param uuid the player's UUID
     * @return list of all punishments (active and expired)
     */
    public static List<Punishment> getHistory(String uuid) {
        return PunishmentManager.get().getPunishments(uuid, null, false);
    }

    /**
     * Get a punishment by its ID.
     *
     * @param id the punishment ID
     * @return the punishment, or null if not found
     */
    public static Punishment getPunishmentById(int id) {
        return PunishmentManager.get().getPunishment(id);
    }

    /**
     * Get count of active warnings for a player.
     *
     * @param uuid the player's UUID
     * @return warning count
     */
    public static int getWarningCount(String uuid) {
        return PunishmentManager.get().getCurrentWarns(uuid);
    }

    // ── Private Helpers ─────────────────────────────────────────────── //

    private static String resolveUuid(String playerName) {
        return UUIDManager.get().getUUID(playerName);
    }

    private static long now() {
        return TimeManager.getTime();
    }

    // ── Punishment Creation (Programmatic API) ──────────────────────── //

    /**
     * Permanently ban a player.
     *
     * @param playerName the player to ban
     * @param operator   the staff member issuing the ban
     * @param reason     the reason for the ban
     * @return the created punishment, or null if UUID fetch failed
     */
    public static Punishment banPlayer(String playerName, String operator, String reason) {
        String uuid = resolveUuid(playerName);
        if (uuid == null) return null;
        Punishment punishment = new Punishment(playerName, uuid, reason, operator,
                PunishmentType.BAN, now(), -1, "", -1);
        punishment.create(false);
        return punishment;
    }

    /**
     * Temporarily ban a player.
     *
     * @param playerName the player to ban
     * @param operator   the staff member
     * @param reason     the reason
     * @param durationMs duration in milliseconds
     * @return the created punishment, or null if UUID fetch failed
     */
    public static Punishment tempBanPlayer(String playerName, String operator, String reason, long durationMs) {
        String uuid = resolveUuid(playerName);
        if (uuid == null) return null;
        long end = now() + durationMs;
        Punishment punishment = new Punishment(playerName, uuid, reason, operator,
                PunishmentType.TEMP_BAN, now(), end, "", -1);
        punishment.create(false);
        return punishment;
    }

    /**
     * Permanently mute a player.
     *
     * @param playerName the player to mute
     * @param operator   the staff member
     * @param reason     the reason
     * @return the created punishment, or null if UUID fetch failed
     */
    public static Punishment mutePlayer(String playerName, String operator, String reason) {
        String uuid = resolveUuid(playerName);
        if (uuid == null) return null;
        Punishment punishment = new Punishment(playerName, uuid, reason, operator,
                PunishmentType.MUTE, now(), -1, "", -1);
        punishment.create(false);
        return punishment;
    }

    /**
     * Temporarily mute a player.
     *
     * @param playerName the player to mute
     * @param operator   the staff member
     * @param reason     the reason
     * @param durationMs duration in milliseconds
     * @return the created punishment, or null if UUID fetch failed
     */
    public static Punishment tempMutePlayer(String playerName, String operator, String reason, long durationMs) {
        String uuid = resolveUuid(playerName);
        if (uuid == null) return null;
        long end = now() + durationMs;
        Punishment punishment = new Punishment(playerName, uuid, reason, operator,
                PunishmentType.TEMP_MUTE, now(), end, "", -1);
        punishment.create(false);
        return punishment;
    }

    /**
     * Warn a player.
     *
     * @param playerName the player to warn
     * @param operator   the staff member
     * @param reason     the reason
     * @return the created punishment, or null if UUID fetch failed
     */
    public static Punishment warnPlayer(String playerName, String operator, String reason) {
        String uuid = resolveUuid(playerName);
        if (uuid == null) return null;
        Punishment punishment = new Punishment(playerName, uuid, reason, operator,
                PunishmentType.WARNING, now(), -1, "", -1);
        punishment.create(false);
        return punishment;
    }

    /**
     * Temporarily warn a player.
     *
     * @param playerName the player to warn
     * @param operator   the staff member
     * @param reason     the reason
     * @param durationMs duration in milliseconds
     * @return the created punishment, or null if UUID fetch failed
     */
    public static Punishment tempWarnPlayer(String playerName, String operator, String reason, long durationMs) {
        String uuid = resolveUuid(playerName);
        if (uuid == null) return null;
        long end = now() + durationMs;
        Punishment punishment = new Punishment(playerName, uuid, reason, operator,
                PunishmentType.TEMP_WARNING, now(), end, "", -1);
        punishment.create(false);
        return punishment;
    }

    /**
     * Kick a player.
     *
     * @param playerName the player to kick
     * @param operator   the staff member
     * @param reason     the kick reason
     * @return the created punishment, or null if UUID fetch failed or player offline
     */
    public static Punishment kickPlayer(String playerName, String operator, String reason) {
        String uuid = resolveUuid(playerName);
        if (uuid == null) return null;
        if (!Universal.get().getMethods().isOnline(playerName)) return null;
        Punishment punishment = new Punishment(playerName, uuid, reason, operator,
                PunishmentType.KICK, now(), -1, "", -1);
        punishment.create(false);
        return punishment;
    }

    /**
     * Add a note to a player.
     *
     * @param playerName the player to note
     * @param operator   the staff member
     * @param note       the note content
     * @return the created punishment, or null if UUID fetch failed
     */
    public static Punishment notePlayer(String playerName, String operator, String note) {
        String uuid = resolveUuid(playerName);
        if (uuid == null) return null;
        Punishment punishment = new Punishment(playerName, uuid, note, operator,
                PunishmentType.NOTE, now(), -1, "", -1);
        punishment.create(false);
        return punishment;
    }

    /**
     * Unban a player.
     *
     * @param playerName the player to unban
     * @param operator   the staff member
     * @return true if the player was unbanned, false if not banned
     */
    public static boolean unbanPlayer(String playerName, String operator) {
        String uuid = resolveUuid(playerName);
        if (uuid == null) return false;
        Punishment ban = PunishmentManager.get().getBan(uuid);
        if (ban == null) return false;
        ban.delete(operator, false, true);
        return true;
    }

    /**
     * Unmute a player.
     *
     * @param playerName the player to unmute
     * @param operator   the staff member
     * @return true if the player was unmuted, false if not muted
     */
    public static boolean unmutePlayer(String playerName, String operator) {
        String uuid = resolveUuid(playerName);
        if (uuid == null) return false;
        Punishment mute = PunishmentManager.get().getMute(uuid);
        if (mute == null) return false;
        mute.delete(operator, false, true);
        return true;
    }

    /**
     * Get the version of PhantomBans.
     *
     * @return version string
     */
    public static String getVersion() {
        return Universal.get().getMethods().getVersion();
    }
}
