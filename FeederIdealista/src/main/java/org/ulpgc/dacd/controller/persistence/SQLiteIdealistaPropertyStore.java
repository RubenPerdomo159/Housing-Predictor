package org.ulpgc.dacd.controller.persistence;

import org.ulpgc.dacd.model.IdealistaProperty;

import java.sql.*;
import java.util.List;

public class SQLiteIdealistaPropertyStore implements IdealistaPropertyStore {
    private final Connection connection;

    public SQLiteIdealistaPropertyStore(String dbPath) throws Exception {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        initializeDatabase();
    }

    private void initializeDatabase() throws Exception {
        String sqlProperties = """
            CREATE TABLE IF NOT EXISTS properties (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                propertyCode TEXT UNIQUE,
                price REAL,
                price_m2 REAL,
                size REAL,
                rooms INTEGER,
                bathrooms INTEGER,
                floor TEXT,
                exterior BOOLEAN,
                propertyType TEXT,
                status TEXT,
                address TEXT,
                neighborhood TEXT,
                district TEXT,
                municipality TEXT,
                province TEXT,
                locationId TEXT,
                latitude REAL,
                longitude REAL,
                hasLift BOOLEAN,
                newDevelopment BOOLEAN,
                url TEXT,
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

    public int getLastPage() throws Exception {
        String sql = "SELECT value FROM metadata WHERE key = 'last_page'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return Integer.parseInt(rs.getString("value"));
            }
        }

        return 0;
    }

    public void saveLastPage(int page) throws Exception {
        String sql = "INSERT OR REPLACE INTO metadata (key, value) VALUES ('last_page', ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, String.valueOf(page));
            stmt.executeUpdate();
        }
    }

    @Override
    public void store(List<IdealistaProperty> properties) throws Exception {

        String sql = """
            INSERT OR IGNORE INTO properties (
                propertyCode, price, price_m2, size, rooms, bathrooms, floor, exterior,
                propertyType, status, address, neighborhood, district, municipality,
                province, locationId, latitude, longitude, hasLift, newDevelopment,
                url, captured_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (IdealistaProperty p : properties) {

                stmt.setString(1, p.propertyCode);
                stmt.setDouble(2, p.precio);
                stmt.setDouble(3, p.precioM2);
                stmt.setDouble(4, p.metros);
                stmt.setInt(5, p.habitaciones);
                stmt.setInt(6, p.bathrooms);
                stmt.setString(7, p.floor);
                stmt.setBoolean(8, p.exterior);
                stmt.setString(9, p.propertyType);
                stmt.setString(10, p.status);
                stmt.setString(11, p.address);
                stmt.setString(12, p.neighborhood);
                stmt.setString(13, p.district);
                stmt.setString(14, p.municipality);
                stmt.setString(15, p.province);
                stmt.setString(16, p.locationId);
                stmt.setDouble(17, p.latitude);
                stmt.setDouble(18, p.longitude);
                stmt.setBoolean(19, p.hasLift);
                stmt.setBoolean(20, p.newDevelopment);
                stmt.setString(21, p.url);
                stmt.setString(22, p.capturedAt);

                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }
}

