package org.ulpgc.dacd.controller.persistence;

import org.ulpgc.dacd.model.FotocasaProperty;
import java.sql.*;
import java.util.List;

public class SQLiteFotocasaPropertyStore implements FotocasaPropertyStore {

    private final Connection connection;

    public SQLiteFotocasaPropertyStore(String dbPath) throws Exception {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        initializeDatabase();
    }

    private void initializeDatabase() throws Exception {
        String sqlProperties = """
            CREATE TABLE IF NOT EXISTS fotocasa_properties (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                url TEXT UNIQUE,
                precio REAL,
                metros REAL,
                habitaciones INTEGER,
                ubicacion TEXT,
                captured_at TEXT
            );
        """;

        String sqlMetadata = """
            CREATE TABLE IF NOT EXISTS metadata (
                key TEXT PRIMARY KEY,
                value TEXT
            );
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlProperties);
            stmt.execute(sqlMetadata);
        }
    }

    @Override
    public void store(List<FotocasaProperty> properties) throws Exception {
        String sql = """
            INSERT OR IGNORE INTO fotocasa_properties (
                url, precio, metros, habitaciones, ubicacion, captured_at
            ) VALUES (?, ?, ?, ?, ?, ?);
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (FotocasaProperty p : properties) {
                stmt.setString(1, p.url);
                stmt.setDouble(2, p.precio);
                stmt.setDouble(3, p.metros);
                stmt.setInt(4, p.habitaciones);
                stmt.setString(5, p.ubicacion);
                stmt.setString(6, p.capturedAt);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    @Override
    public int getLastPage() throws Exception {
        String sql = "SELECT value FROM metadata WHERE key = 'last_page'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return Integer.parseInt(rs.getString("value"));
        }
        return 0;
    }

    @Override
    public void saveLastPage(int page) throws Exception {
        String sql = "INSERT OR REPLACE INTO metadata (key, value) VALUES ('last_page', ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, String.valueOf(page));
            stmt.executeUpdate();
        }
    }
}
