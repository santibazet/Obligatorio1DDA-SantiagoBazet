package org.example.DAO;

import org.example.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

public class HabitacionDAO extends ConnectionDAO {

    // Instancia de los DAOs necesarios para obtener Hotel, TipoHabitacion y TipoCama
    private HotelDAO hotelDAO;
    private TipoHabitacionDAO tipoHabitacionDAO;
    private TipoCamaDAO tipoCamaDAO;

    // Constructor donde se inicializan los DAOs
    public HabitacionDAO() {
        this.hotelDAO = new HotelDAO();
        this.tipoHabitacionDAO = new TipoHabitacionDAO();
        this.tipoCamaDAO = new TipoCamaDAO();
    }


    // Método para obtener todas las habitaciones
    public List<Habitacion> getAllHabitaciones() {
        List<Habitacion> habitaciones = new ArrayList<>();
        String query = "SELECT h.idHabitacion, h.idHotel, h.idTipoHabitacion, h.idTipoCama, h.cantCamas, h.ocupada " +
                "FROM Habitaciones h ORDER BY h.idHabitacion";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                // Obtener el hotel, tipoHabitacion y tipoCama utilizando los métodos correspondientes
                Hotel hotel = hotelDAO.getHotelById(resultSet.getInt("idHotel"));
                TipoHabitacion tipoHabitacion = tipoHabitacionDAO.getTipoHabitacionById(resultSet.getInt("idTipoHabitacion"));
                TipoCama tipoCama = tipoCamaDAO.getTipoCamaById(resultSet.getInt("idTipoCama"));

                // Crear la habitación con los valores completos
                Habitacion habitacion = new Habitacion(
                        resultSet.getInt("idHabitacion"),
                        hotel,
                        tipoHabitacion,
                        tipoCama,
                        resultSet.getInt("cantCamas"),
                        resultSet.getBoolean("ocupada"),
                        new ArrayList<>()  // Inicializar la lista de características como vacía por ahora
                );

                // Consulta adicional para obtener las características de la habitación
                String caracteristicasQuery = "SELECT c.nombre " +
                        "FROM Caracteristicas c " +
                        "JOIN habitaciones_caracteristicas hc ON c.idCaracteristica = hc.idCaracteristica " +
                        "WHERE hc.idHabitacion = ?";

                try (PreparedStatement caracteristicasStatement = connection.prepareStatement(caracteristicasQuery)) {
                    caracteristicasStatement.setInt(1, habitacion.getIdHabitacion());
                    try (ResultSet caracteristicasResultSet = caracteristicasStatement.executeQuery()) {
                        List<Caracteristica> caracteristicas = new ArrayList<>();
                        while (caracteristicasResultSet.next()) {
                            // Agregar las características a la lista de la habitación
                            caracteristicas.add(new Caracteristica(caracteristicasResultSet.getString("nombre")));
                        }
                        // Asignar las características a la habitación
                        habitacion.setCaracteristicas(caracteristicas);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Agregar la habitación con las características a la lista
                habitaciones.add(habitacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return habitaciones;
    }



    // Método para actualizar una habitación existente, incluyendo el idHotel
    public boolean updateHabitacion(Habitacion habitacion) {
        String query = "UPDATE Habitaciones SET idHotel = ?, idTipoHabitacion = ?, idTipoCama = ?, cantCamas = ?, ocupada = ? " +
                "WHERE idHabitacion = ?";
        return executeUpdate(query,
                habitacion.getHotel().getIdHotel(),
                habitacion.getTipoHabitacion().getIdTipoHabitacion(),
                habitacion.getTipoCama().getIdTipoCama(),
                habitacion.getCantCamas(),
                habitacion.isOcupada() ? 1 : 0,
                habitacion.getIdHabitacion());
    }


    public boolean deleteHabitacion(int idHabitacion) {
        String query = "DELETE FROM Habitaciones WHERE idHabitacion = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idHabitacion);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // Si se eliminó al menos una fila, es exitoso
        } catch (SQLException e) {
            System.out.println("Error al eliminar la habitación: " + e.getMessage());
            return false;
        }
    }

    public boolean addCaracteristicaToHabitacion(int idHabitacion, int idCaracteristica) {
        String query = "INSERT INTO habitaciones_caracteristicas (idHabitacion, idCaracteristica) VALUES (?, ?)";

        try (Connection connection = ConnectionDAO.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idHabitacion);
            ps.setInt(2, idCaracteristica);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al vincular característica a habitación: " + e.getMessage());
            return false;
        }
    }

    public int createHabitacionAndGetId(Habitacion habitacion) {
        int idGenerado = -1;

        String sql = "INSERT INTO habitaciones (idHotel, idTipoHabitacion, idTipoCama, cantCamas, ocupada) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, habitacion.getHotel().getIdHotel());
            pstmt.setInt(2, habitacion.getTipoHabitacion().getIdTipoHabitacion());
            pstmt.setInt(3, habitacion.getTipoCama().getIdTipoCama());
            pstmt.setInt(4, habitacion.getCantCamas());
            pstmt.setBoolean(5, habitacion.isOcupada());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Obtener el ID generado
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idGenerado = generatedKeys.getInt(1);  // El primer valor del ResultSet contiene el ID generado
                    }
                }
            } else {
                throw new SQLException("Error al crear la habitación, no se generó ninguna fila.");
            }
        } catch (SQLException e) {
            System.out.println("Error al crear la habitación: " + e.getMessage());
        }

        return idGenerado;
    }

    public boolean removeAllCaracteristicasFromHabitacion(int idHabitacion) {
        String sql = "DELETE FROM habitaciones_caracteristicas WHERE idHabitacion = ?";

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idHabitacion);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;  // Si se eliminan filas, la operación fue exitosa
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Si ocurre un error, la operación falla
        }
    }


    public Habitacion getHabitacionById(int idHabitacion) {
        Habitacion habitacion = null;
        String query = "SELECT h.*, ho.idHotel, ho.nombre AS nombreHotel, " +
                "th.idTipoHabitacion, th.nombre AS nombreTipoHabitacion, " +
                "tc.idTipoCama, tc.nombre AS nombreTipoCama " +
                "FROM Habitaciones h " +
                "JOIN Hoteles ho ON h.idHotel = ho.idHotel " +
                "JOIN TiposHabitacion th ON h.idTipoHabitacion = th.idTipoHabitacion " +
                "JOIN TiposCama tc ON h.idTipoCama = tc.idTipoCama " +
                "WHERE h.idHabitacion = ?";

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idHabitacion);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Instanciar Hotel
                Hotel hotel = new Hotel(
                        rs.getInt("idHotel"),
                        rs.getString("nombreHotel")
                );

                // Instanciar TipoHabitacion
                TipoHabitacion tipoHabitacion = new TipoHabitacion(
                        rs.getInt("idTipoHabitacion"),
                        rs.getString("nombreTipoHabitacion")
                );

                // Instanciar TipoCama
                TipoCama tipoCama = new TipoCama(
                        rs.getInt("idTipoCama"),
                        rs.getString("nombreTipoCama")
                );

                // Obtener características de la habitación (si existen)
                List<Caracteristica> caracteristicas = getCaracteristicasByHabitacionId(idHabitacion);

                // Instanciar Habitacion
                habitacion = new Habitacion(
                        rs.getInt("idHabitacion"),
                        hotel,
                        tipoHabitacion,
                        tipoCama,
                        rs.getInt("cantCamas"),
                        rs.getBoolean("ocupada"),
                        caracteristicas
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return habitacion;
    }

    // Método auxiliar para obtener las características de una habitación
    private List<Caracteristica> getCaracteristicasByHabitacionId(int idHabitacion) {
        List<Caracteristica> caracteristicas = new ArrayList<>();
        String query = "SELECT c.idCaracteristica, c.nombre " +
                "FROM Habitaciones_Caracteristicas hc " +
                "JOIN Caracteristicas c ON hc.idCaracteristica = c.idCaracteristica " +
                "WHERE hc.idHabitacion = ? ORDER BY c.idCaracteristica";

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idHabitacion);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Caracteristica caracteristica = new Caracteristica(
                        rs.getInt("idCaracteristica"),
                        rs.getString("nombre")
                );
                caracteristicas.add(caracteristica);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return caracteristicas;
    }


    public List<Habitacion> getHabitacionesDisponiblesPorHotel(int idHotel) {
        List<Habitacion> habitacionesDisponibles = new ArrayList<>();
        String query = "SELECT h.*, ho.idHotel, ho.nombre AS nombreHotel, " +
                "th.idTipoHabitacion, th.nombre AS nombreTipoHabitacion, " +
                "tc.idTipoCama, tc.nombre AS nombreTipoCama " +
                "FROM Habitaciones h " +
                "JOIN Hoteles ho ON h.idHotel = ho.idHotel " +
                "JOIN TiposHabitacion th ON h.idTipoHabitacion = th.idTipoHabitacion " +
                "JOIN TiposCama tc ON h.idTipoCama = tc.idTipoCama " +
                "WHERE h.ocupada = false AND ho.idHotel = ? ORDER BY h.idHabitacion" ;

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idHotel);  // Pasar el ID del hotel como parámetro
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Crear las instancias de Hotel, TipoHabitacion, TipoCama, etc.
                Hotel hotel = new Hotel(rs.getInt("idHotel"), rs.getString("nombreHotel"));
                TipoHabitacion tipoHabitacion = new TipoHabitacion(rs.getInt("idTipoHabitacion"), rs.getString("nombreTipoHabitacion"));
                TipoCama tipoCama = new TipoCama(rs.getInt("idTipoCama"), rs.getString("nombreTipoCama"));
                List<Caracteristica> caracteristicas = getCaracteristicasByHabitacionId(rs.getInt("idHabitacion"));

                // Crear la habitación y agregarla a la lista
                Habitacion habitacion = new Habitacion(
                        rs.getInt("idHabitacion"),
                        hotel,
                        tipoHabitacion,
                        tipoCama,
                        rs.getInt("cantCamas"),
                        rs.getBoolean("ocupada"),
                        caracteristicas
                );

                habitacionesDisponibles.add(habitacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return habitacionesDisponibles;
    }


    public List<Habitacion> getHabitacionesDisponiblesPorHotelYFecha(int idHotel, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Habitacion> habitacionesDisponibles = new ArrayList<>();

        // Consulta SQL para obtener las habitaciones libres entre las fechas de reserva
        String query = "SELECT h.*, th.* FROM Habitaciones h " +
                "JOIN TiposHabitacion th ON h.idTipoHabitacion = th.idTipoHabitacion " +
                "WHERE h.idHotel = ? " +
                "AND h.idHabitacion NOT IN (" +
                "    SELECT rh.idHabitacion FROM reservas_habitaciones rh " +
                "    JOIN Reservas r ON r.idReserva = rh.idReserva " +
                "    WHERE r.fechaRes < ? AND r.fechaVencimiento > ?" +
                ")";

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idHotel);
            stmt.setDate(2, java.sql.Date.valueOf(fechaFin));  // Fecha final de la reserva
            stmt.setDate(3, java.sql.Date.valueOf(fechaInicio));  // Fecha inicial de la reserva

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Crear el objeto TipoHabitacion usando los datos del ResultSet
                    TipoHabitacion tipoHabitacion = new TipoHabitacion(
                            rs.getInt("th.idTipoHabitacion"),
                            rs.getString("th.nombre")  // Ajusta estos nombres de columna según tu modelo
                    );

                    // Crear el objeto Habitacion con el tipoHabitacion completo
                    Habitacion habitacion = new Habitacion(
                            rs.getInt("h.idHabitacion"),
                            tipoHabitacion,
                            rs.getBoolean("h.ocupada")  // Este campo podría ser innecesario, pero lo dejas si es relevante
                    );

                    habitacionesDisponibles.add(habitacion);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return habitacionesDisponibles;
    }



}


