package org.example.DAO;

import org.example.model.Caracteristica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CaracteristicaDAO extends ConnectionDAO {

    public List<Caracteristica> getAllCaracteristicas() {
        List<Caracteristica> caracteristicas = new ArrayList<>();
        String query = "SELECT * FROM Caracteristicas ORDER BY idCaracteristica";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Caracteristica caracteristica = new Caracteristica(
                        resultSet.getInt("idCaracteristica"),
                        resultSet.getString("nombre")
                );
                caracteristicas.add(caracteristica);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return caracteristicas;
    }


    // Método que obtiene todas las características vinculadas a una habitación específica
    public List<Caracteristica> getCaracteristicasByHabitacionId(int idHabitacion) {
        List<Caracteristica> caracteristicas = new ArrayList<>();
        String sql = "SELECT c.idCaracteristica, c.nombre " +
                "FROM habitaciones_caracteristicas hc " +
                "JOIN caracteristicas c ON hc.idCaracteristica = c.idCaracteristica " +
                "WHERE hc.idHabitacion = ?";

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idHabitacion);  // Pasamos el ID de la habitación

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Caracteristica caracteristica = new Caracteristica();
                    caracteristica.setIdCaracteristica(rs.getInt("idCaracteristica"));
                    caracteristica.setNombre(rs.getString("nombre"));
                    caracteristicas.add(caracteristica);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener las características de la habitación: " + e.getMessage());
        }

        return caracteristicas;
    }
}

