package org.example.DAO;

import org.example.model.Tarifa;
import org.example.model.TipoHabitacion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TarifaDAO extends ConnectionDAO {

    // Método para obtener todas las tarifas
    public List<Tarifa> getAllTarifas() {
        List<Tarifa> tarifas = new ArrayList<>();
        String query = "SELECT t.idTarifa, t.idTipoHabitacion, t.precio, t.fechaFinVigencia, th.nombre " +
                "FROM Tarifas t " +
                "JOIN TiposHabitacion th ON t.idTipoHabitacion = th.idTipoHabitacion ORDER BY t.idTarifa";

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Tarifa tarifa = new Tarifa();
                tarifa.setIdTarifa(rs.getInt("idTarifa"));
                tarifa.setPrecio(rs.getDouble("precio"));
                tarifa.setFechaFinVigencia(rs.getDate("fechaFinVigencia").toLocalDate());  // Se convierte Date a LocalDate

                TipoHabitacion tipoHabitacion = new TipoHabitacion();
                tipoHabitacion.setIdTipoHabitacion(rs.getInt("idTipoHabitacion"));
                tipoHabitacion.setNombre(rs.getString("nombre"));

                tarifa.setTipoHabitacion(tipoHabitacion);

                tarifas.add(tarifa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tarifas;
    }

    // Método para obtener una tarifa por su id
    public Tarifa getTarifaById(int idTarifa) {
        Tarifa tarifa = null;
        String query = "SELECT t.idTarifa, t.idTipoHabitacion, t.precio, t.fechaFinVigencia, th.nombre " +
                "FROM Tarifas t " +
                "JOIN TiposHabitacion th ON t.idTipoHabitacion = th.idTipoHabitacion " +
                "WHERE t.idTarifa = ?";

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idTarifa);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    tarifa = new Tarifa();
                    tarifa.setIdTarifa(rs.getInt("idTarifa"));
                    tarifa.setPrecio(rs.getDouble("precio"));
                    tarifa.setFechaFinVigencia(rs.getDate("fechaFinVigencia").toLocalDate());  // Se convierte Date a LocalDate

                    TipoHabitacion tipoHabitacion = new TipoHabitacion();
                    tipoHabitacion.setIdTipoHabitacion(rs.getInt("idTipoHabitacion"));
                    tipoHabitacion.setNombre(rs.getString("nombre"));

                    tarifa.setTipoHabitacion(tipoHabitacion);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tarifa;
    }

    // Método para actualizar una tarifa
    public boolean updateTarifa(Tarifa tarifa) {
        String query = "UPDATE Tarifas SET idTipoHabitacion = ?, precio = ?, fechaFinVigencia = ? WHERE idTarifa = ?";

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, tarifa.getTipoHabitacion().getIdTipoHabitacion());
            stmt.setDouble(2, tarifa.getPrecio());
            stmt.setDate(3, Date.valueOf(tarifa.getFechaFinVigencia()));  // Se convierte LocalDate a Date
            stmt.setInt(4, tarifa.getIdTarifa());

            stmt.executeUpdate();
            return true;  // Retorna true si la actualización fue exitosa
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Retorna false en caso de error
    }

    // Método para eliminar una tarifa
    public boolean deleteTarifa(int idTarifa) {
        String query = "DELETE FROM Tarifas WHERE idTarifa = ?";

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idTarifa);
            stmt.executeUpdate();
            return true;  // Retorna true si la eliminación fue exitosa
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Retorna false en caso de error
    }

    public int createTarifaAndGetId(Tarifa tarifa) {
        String sql = "INSERT INTO Tarifas (idTipoHabitacion, precio, fechaFinVigencia) VALUES (?, ?, ?)";
        int idGenerado = -1;

        try (Connection connection = getConnection();  // Obtener la conexión desde el ConnectionDAO o método adecuado
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Establecer los valores de la tarifa
            statement.setInt(1, tarifa.getTipoHabitacion().getIdTipoHabitacion());
            statement.setDouble(2, tarifa.getPrecio());
            statement.setDate(3, java.sql.Date.valueOf(tarifa.getFechaFinVigencia())); // Convertir LocalDate a java.sql.Date

            // Ejecutar la consulta
            int filasAfectadas = statement.executeUpdate();

            // Si la inserción fue exitosa, obtener el ID generado automáticamente
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idGenerado = generatedKeys.getInt(1); // Obtener el ID generado
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idGenerado;  // Retornar el ID generado o -1 en caso de error
    }

    public Tarifa getTarifaVigente(int idTipoHabitacion, LocalDate fechaReserva) {
        Tarifa tarifa = null;
        String query = "SELECT * FROM Tarifas WHERE idTipoHabitacion = ? AND fechaFinVigencia >= ? ORDER BY fechaFinVigencia ASC LIMIT 1";

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idTipoHabitacion);
            stmt.setDate(2, java.sql.Date.valueOf(fechaReserva));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                tarifa = new Tarifa(
                        rs.getInt("idTarifa"),
                        new TipoHabitacionDAO().getTipoHabitacionById(rs.getInt("idTipoHabitacion")),
                        rs.getDouble("precio"),
                        rs.getDate("fechaFinVigencia").toLocalDate()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tarifa;
    }

}
