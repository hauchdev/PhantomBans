package dev.hauch.phantomBans.manager;

import com.zaxxer.hikari.HikariDataSource;
import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.utils.DynamicDataSource;
import dev.hauch.phantomBans.utils.SQLQuery;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Manages database connections and queries.
 */
public class DatabaseManager {

    private static DatabaseManager instance = null;
    private HikariDataSource dataSource;
    private boolean useMySQL;
    private RowSetFactory factory;

    public static synchronized DatabaseManager get() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void setup(boolean useMySQLServer) {
        useMySQL = useMySQLServer;
        try {
            dataSource = new DynamicDataSource(useMySQL).generateDataSource();
        } catch (ClassNotFoundException ex) {
            Universal.get().log("§cERROR: Failed to configure data source!");
            Universal.get().debug(ex.getMessage());
            return;
        }
        executeStatement(SQLQuery.CREATE_TABLE_PUNISHMENT);
        executeStatement(SQLQuery.CREATE_TABLE_PUNISHMENT_HISTORY);
    }

    public void shutdown() {
        if (!useMySQL) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SHUTDOWN")) {
                statement.execute();
            } catch (SQLException | NullPointerException exc) {
                Universal.get().log("An unexpected error has occurred turning off the database");
                Universal.get().debugException(exc);
            }
        }
        if (dataSource != null) {
            dataSource.close();
        }
    }

    private CachedRowSet createCachedRowSet() throws SQLException {
        if (factory == null) {
            factory = RowSetProvider.newFactory();
        }
        return factory.createCachedRowSet();
    }

    public void executeStatement(SQLQuery sql, Object... parameters) {
        executeStatement(sql, false, parameters);
    }

    public ResultSet executeResultStatement(SQLQuery sql, Object... parameters) {
        return executeStatement(sql, true, parameters);
    }

    private ResultSet executeStatement(SQLQuery sql, boolean result, Object... parameters) {
        return executeStatement(sql.toString(), result, parameters);
    }

    private synchronized ResultSet executeStatement(String sql, boolean result, Object... parameters) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            if (result) {
                CachedRowSet results = createCachedRowSet();
                results.populate(statement.executeQuery());
                return results;
            }
            statement.execute();
        } catch (SQLException ex) {
            Universal.get().log("An unexpected error has occurred executing a statement in the database\n"
                    + "Please check the logs/latest.log file and report this error.");
            Universal.get().debug("Query: \n" + sql);
            Universal.get().debugSqlException(ex);
        } catch (NullPointerException ex) {
            Universal.get().log("An unexpected error has occurred connecting to the database\n"
                    + "Check if your MySQL data is correct and if your MySQL-Server is online");
            Universal.get().debugException(ex);
        }
        return null;
    }

    public boolean isConnectionValid() {
        return dataSource != null && dataSource.isRunning();
    }

    public boolean isUseMySQL() {
        return useMySQL;
    }
}
