package org.ulpgc.dacd.persistence;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:housing.db";



    public static Connection getConnection() throws SQLException {
        File dbFile = new File("housing.db");
        System.out.println("Usando base de datos en: " + dbFile.getAbsolutePath());
        return DriverManager.getConnection(URL);
    }

}
