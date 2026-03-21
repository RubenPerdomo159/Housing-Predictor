package org.ulpgc.dacd;

import org.ulpgc.dacd.persistence.DatabaseConnection;
import org.ulpgc.dacd.persistence.DatabaseInitializer;

import java.sql.Connection;


public class Main {

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn != null) {
                System.out.println("Conectado a SQLite correctamente");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseInitializer.init();
    }
}