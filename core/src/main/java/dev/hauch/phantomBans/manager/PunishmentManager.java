package dev.hauch.phantomBans.manager;

import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.utils.InterimData;
import dev.hauch.phantomBans.utils.Punishment;
import dev.hauch.phantomBans.utils.PunishmentType;
import dev.hauch.phantomBans.utils.SQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Handles punishments - loading, caching, and management.
 */
public class PunishmentManager {

    private static PunishmentManager instance = null;
    private final Set<Punishment> punishments = Collections.synchronizedSet(new HashSet<>());
    private final Set<Punishment> history = Collections.synchronizedSet(new HashSet<>());
    private final Set<String> cached = Collections.synchronizedSet(new HashSet<>());

    public static synchronized PunishmentManager get() {
        if (instance == null) {
            instance = new PunishmentManager();
        }
        return instance;
    }

    public void setup() {
        DatabaseManager.get().executeStatement(SQLQuery.DELETE_OLD_PUNISHMENTS, TimeManager.getTime());
    }

    public InterimData load(String name, String uuid, String ip) {
        Set<Punishment> punishments = new HashSet<>();
        Set<Punishment> history = new HashSet<>();
        try (ResultSet resultsPunishments = DatabaseManager.get().executeResultStatement(
                SQLQuery.SELECT_USER_PUNISHMENTS_WITH_IP, uuid, ip);
             ResultSet resultsHistory = DatabaseManager.get().executeResultStatement(
                     SQLQuery.SELECT_USER_PUNISHMENTS_HISTORY_WITH_IP, uuid, ip)) {
            if (resultsHistory == null || resultsPunishments == null) return null;
            while (resultsPunishments.next()) {
                punishments.add(getPunishmentFromResultSet(resultsPunishments));
            }
            while (resultsHistory.next()) {
                history.add(getPunishmentFromResultSet(resultsHistory));
            }
        } catch (SQLException ex) {
            Universal.get().log("An error has occurred loading the punishments from the database.");
            Universal.get().debugSqlException(ex);
            return null;
        }
        return new InterimData(uuid, name, ip, punishments, history);
    }

    public void discard(String name) {
        name = name.toLowerCase();
        String ip = Universal.get().getIps().get(name);
        String uuid = UUIDManager.get().getUUID(name);
        cached.remove(name);
        cached.remove(uuid);
        cached.remove(ip);
        Iterator<Punishment> iterator = punishments.iterator();
        while (iterator.hasNext()) {
            Punishment punishment = iterator.next();
            if (punishment.getUuid().equals(uuid) || punishment.getUuid().equals(ip)) {
                iterator.remove();
            }
        }
        iterator = history.iterator();
        while (iterator.hasNext()) {
            Punishment punishment = iterator.next();
            if (punishment.getUuid().equals(uuid) || punishment.getUuid().equals(ip)) {
                iterator.remove();
            }
        }
    }

    public List<Punishment> getPunishments(String target, PunishmentType put, boolean current) {
        List<Punishment> ptList = new ArrayList<>();
        if (isCached(target)) {
            Iterator<Punishment> iterator = (current ? punishments : history).iterator();
            while (iterator.hasNext()) {
                Punishment pt = iterator.next();
                if ((put == null || put == pt.getType().getBasic()) && pt.getUuid().equals(target)) {
                    if (!current || !pt.isExpired()) {
                        ptList.add(pt);
                    } else {
                        pt.delete(null, false, false);
                        iterator.remove();
                    }
                }
            }
        } else {
            try (ResultSet rs = DatabaseManager.get().executeResultStatement(
                    current ? SQLQuery.SELECT_USER_PUNISHMENTS : SQLQuery.SELECT_USER_PUNISHMENTS_HISTORY, target)) {
                while (rs.next()) {
                    Punishment punishment = getPunishmentFromResultSet(rs);
                    if ((put == null || put == punishment.getType().getBasic())
                            && (!current || !punishment.isExpired())) {
                        ptList.add(punishment);
                    }
                }
            } catch (SQLException ex) {
                Universal.get().log("An error has occurred getting the punishments for " + target);
                Universal.get().debugSqlException(ex);
            }
        }
        return ptList;
    }

    public List<Punishment> getPunishments(SQLQuery sqlQuery, Object... parameters) {
        List<Punishment> ptList = new ArrayList<>();
        ResultSet rs = DatabaseManager.get().executeResultStatement(sqlQuery, parameters);
        try {
            while (rs.next()) {
                Punishment punishment = getPunishmentFromResultSet(rs);
                ptList.add(punishment);
            }
            rs.close();
        } catch (SQLException ex) {
            Universal.get().log("An error has occurred executing a query in the database.");
            Universal.get().debug("Query: \n" + sqlQuery);
            Universal.get().debugSqlException(ex);
        }
        return ptList;
    }

    public Punishment getPunishment(int id) {
        Optional<Punishment> cachedPunishment = getLoadedPunishments(false).stream()
                .filter(punishment -> punishment.getId() == id).findAny();
        if (cachedPunishment.isPresent()) return cachedPunishment.get();
        try (ResultSet rs = DatabaseManager.get().executeResultStatement(SQLQuery.SELECT_PUNISHMENT_BY_ID, id)) {
            if (rs.next()) {
                Punishment punishment = getPunishmentFromResultSet(rs);
                if (!punishment.isExpired()) return punishment;
            }
        } catch (SQLException ex) {
            Universal.get().log("An error has occurred getting a punishment by id.");
            Universal.get().debug("Punishment id: '" + id + "'");
            Universal.get().debugSqlException(ex);
        }
        return null;
    }

    public Punishment getWarn(int id) {
        Punishment punishment = getPunishment(id);
        if (punishment == null) return null;
        return punishment.getType().getBasic() == PunishmentType.WARNING ? punishment : null;
    }

    public List<Punishment> getWarns(String uuid) {
        return getPunishments(uuid, PunishmentType.WARNING, true);
    }

    public Punishment getNote(int id) {
        Punishment punishment = getPunishment(id);
        if (punishment == null) return null;
        return punishment.getType().getBasic() == PunishmentType.NOTE ? punishment : null;
    }

    public List<Punishment> getNotes(String uuid) {
        return getPunishments(uuid, PunishmentType.NOTE, true);
    }

    public Punishment getBan(String uuid) {
        List<Punishment> punishments = getPunishments(uuid, PunishmentType.BAN, true);
        return punishments.isEmpty() ? null : punishments.get(0);
    }

    public Punishment getMute(String uuid) {
        List<Punishment> punishments = getPunishments(uuid, PunishmentType.MUTE, true);
        return punishments.isEmpty() ? null : punishments.get(0);
    }

    public boolean isBanned(String uuid) { return getBan(uuid) != null; }
    public boolean isMuted(String uuid) { return getMute(uuid) != null; }
    public boolean isCached(String target) { return cached.contains(target); }

    public void setCached(InterimData data) {
        cached.add(data.getName());
        cached.add(data.getIp());
        cached.add(data.getUuid());
    }

    public int getCalculationLevel(String uuid, String layout) {
        if (isCached(uuid)) {
            return (int) history.stream()
                    .filter(pt -> pt.getUuid().equals(uuid) && layout.equalsIgnoreCase(pt.getCalculation()))
                    .count();
        }
        int i = 0;
        try (ResultSet resultSet = DatabaseManager.get().executeResultStatement(
                SQLQuery.SELECT_USER_PUNISHMENTS_HISTORY_BY_CALCULATION, uuid, layout)) {
            while (resultSet.next()) { i++; }
        } catch (SQLException ex) {
            Universal.get().log("An error has occurred getting the level for the layout '" + layout + "' for '" + uuid + "'");
            Universal.get().debugSqlException(ex);
        }
        return i;
    }

    public int getCurrentWarns(String uuid) { return getWarns(uuid).size(); }
    public int getCurrentNotes(String uuid) { return getNotes(uuid).size(); }

    public Set<Punishment> getLoadedPunishments(boolean checkExpired) {
        if (checkExpired) {
            List<Punishment> toDelete = new ArrayList<>();
            for (Punishment pu : punishments) {
                if (pu.isExpired()) toDelete.add(pu);
            }
            for (Punishment pu : toDelete) pu.delete();
        }
        return punishments;
    }

    public Punishment getPunishmentFromResultSet(ResultSet rs) throws SQLException {
        return new Punishment(
                rs.getString("name"),
                rs.getString("uuid"),
                rs.getString("reason"),
                rs.getString("operator"),
                PunishmentType.valueOf(rs.getString("punishmentType")),
                rs.getLong("start"),
                rs.getLong("end"),
                rs.getString("calculation"),
                rs.getInt("id"));
    }

    public Set<Punishment> getLoadedHistory() { return history; }
}
