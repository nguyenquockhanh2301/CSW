package com.csw.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Database {
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);
    private static final String DB_FILE = Path.of(System.getProperty("user.home"), "csw-employees.db").toString();
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_FILE;

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver is not available", e);
        }
        return DriverManager.getConnection(JDBC_URL);
    }

    public static void initialize() {
        if (INITIALIZED.compareAndSet(false, true)) {
            runSchema();
        }
    }

    public static void shutdown() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        ClassLoader applicationClassLoader = Database.class.getClassLoader();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == applicationClassLoader) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    throw new IllegalStateException("Failed to unregister database driver", e);
                }
            }
        }
        INITIALIZED.set(false);
    }

    private static void runSchema() {
        String schemaSql = readResource("sql/schema.sql");
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            for (String sql : schemaSql.split(";")) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    statement.execute(trimmed);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize database schema", e);
        }
    }

    private static String readResource(String resourcePath) {
        InputStream inputStream = Database.class.getClassLoader().getResourceAsStream(resourcePath);
        if (Objects.isNull(inputStream)) {
            throw new IllegalStateException("Missing required resource: " + resourcePath);
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append('\n');
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read resource: " + resourcePath, e);
        }
        return content.toString();
    }
}
