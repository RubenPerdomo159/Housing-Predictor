package org.ulpgc.dacd.controller.persistence;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteFotocasaPropertyStore {

    private static final String URL = "jdbc:sqlite:housing.db";



    public static Connection getConnection() throws SQLException {
        File dbFile = new File("housing.db");
        System.out.println("Usando base de datos en: " + dbFile.getAbsolutePath());
        return DriverManager.getConnection(URL);
    }

    private static void createPropertiesTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS properties (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                price REAL NOT NULL,
                url TEXT NOT NULL,
                captured_at TEXT NOT NULL
            );
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Tabla properties creada o ya existente");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
