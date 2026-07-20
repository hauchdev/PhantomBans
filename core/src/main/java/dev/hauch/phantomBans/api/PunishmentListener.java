package dev.hauch.phantomBans.api;

/**
 * Listener interface that other plugins can implement to receive
 * PhantomBans punishment events.
 * <p>
 * Register via {@link PhantomBansAPI#addListener(PunishmentListener)}.
 */
public interface PunishmentListener {

    /**
     * Called when a punishment is created (ban, mute, warn, kick, note).
     *
     * @param event the punishment event
     */
    default void onPunish(PunishEvent event) {}

    /**
     * Called when a punishment is revoked (unban, unmute, unwarn, unnote).
     *
     * @param event the revoke event
     */
    default void onRevoke(RevokePunishEvent event) {}

    /**
     * Called when a player is kicked (special case of punishment).
     *
     * @param event the punishment event
     */
    default void onKick(PunishEvent event) {}
}
