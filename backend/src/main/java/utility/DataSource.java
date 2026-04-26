package utility;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private final HikariDataSource dataSource;

    private DataSource() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(Config.get("db.url"));
        config.setUsername(Config.get("db.username"));
        config.setPassword(Config.get("db.password"));

        config.setMaximumPoolSize(Config.getInt("db.pool.max"));
        config.setMinimumIdle(Config.getInt("db.pool.min"));
        config.setIdleTimeout(Config.getLong("db.pool.idleTimeout"));
        config.setMaxLifetime(Config.getLong("db.pool.maxLifetime"));

        dataSource = new HikariDataSource(config);
    }

    private static class Holder {
        private static final DataSource INSTANCE = new DataSource();
    }

    public static DataSource getInstance() {
        return Holder.INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}