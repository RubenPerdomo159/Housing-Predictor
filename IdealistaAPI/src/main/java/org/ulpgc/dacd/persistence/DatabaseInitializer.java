package org.ulpgc.dacd.persistence;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void init() {
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

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Tabla properties creada o ya existente");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
