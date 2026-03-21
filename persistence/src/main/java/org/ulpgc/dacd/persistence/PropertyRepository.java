package org.ulpgc.dacd.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PropertyRepository {

    public void save(String title, double price, String url) {

        String sql = "INSERT INTO properties(title, price, url) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setDouble(2, price);
            stmt.setString(3, url);

            stmt.executeUpdate();

            System.out.println("Propiedad guardada");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
