package org.example.DAO;

import org.example.model.TipoHabitacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoHabitacionDAO extends ConnectionDAO {


    // Método para obtener un tipo de habitación por ID
    public TipoHabitacion getTipoHabitacionById(int idTipoHabitacion) {
        // Obtener todos los tipos de habitación
        List<TipoHabitacion> tiposHabitacion = getAllTiposHabitacion();

        // Buscar el tipo de habitación por ID
        for (TipoHabitacion tipo : tiposHabitacion) {
            if (tipo.getIdTipoHabitacion() == idTipoHabitacion) {
                return tipo;  // Retornar el tipo de habitación encontrado
            }
        }

        // Si no se encuentra el tipo de habitación, retornar null o lanzar una excepción
        return null;  // O puedes lanzar una excepción si prefieres
    }

    public boolean existsById(int idTipoHabitacion) {
        List<TipoHabitacion> tiposHabitacion = getAllTiposHabitacion();
        return tiposHabitacion.stream().anyMatch(tipo -> tipo.getIdTipoHabitacion() == idTipoHabitacion);
    }

    public List<TipoHabitacion> getAllTiposHabitacion() {
        List<TipoHabitacion> tiposHabitacion = new ArrayList<>();
        String query = "SELECT * FROM TiposHabitacion ORDER BY idTipoHabitacion";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                TipoHabitacion tipoHabitacion = new TipoHabitacion(
                        resultSet.getInt("idTipoHabitacion"),
                        resultSet.getString("nombre")
                );
                tiposHabitacion.add(tipoHabitacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tiposHabitacion;
    }
}