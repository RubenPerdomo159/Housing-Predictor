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
        String sql = """
            CREATE TABLE IF NOT EXISTS properties (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
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
                url TEXT
            );
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public void store(List<IdealistaProperty> properties) throws Exception {

        String sql = """
            INSERT INTO properties (
                price, price_m2, size, rooms, bathrooms, floor, exterior,
                propertyType, status, address, neighborhood, district,
                municipality, province, locationId, latitude, longitude,
                hasLift, newDevelopment, url
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (IdealistaProperty p : properties) {
                stmt.setDouble(1, p.precio);
                stmt.setDouble(2, p.precioM2);
                stmt.setDouble(3, p.metros);
                stmt.setInt(4, p.habitaciones);
                stmt.setInt(5, p.bathrooms);
                stmt.setString(6, p.floor);
                stmt.setBoolean(7, p.exterior);
                stmt.setString(8, p.propertyType);
                stmt.setString(9, p.status);
                stmt.setString(10, p.address);
                stmt.setString(11, p.neighborhood);
                stmt.setString(12, p.district);
                stmt.setString(13, p.municipality);
                stmt.setString(14, p.province);
                stmt.setString(15, p.locationId);
                stmt.setDouble(16, p.latitude);
                stmt.setDouble(17, p.longitude);
                stmt.setBoolean(18, p.hasLift);
                stmt.setBoolean(19, p.newDevelopment);
                stmt.setString(20, p.url);

                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }
}
