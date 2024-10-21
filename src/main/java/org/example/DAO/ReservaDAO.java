package org.example.DAO;

import org.example.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO extends ConnectionDAO {

    // Método para crear una reserva y asignar las tarifas correspondientes
    public boolean createReserva(Reserva reserva) {
        java.sql.Date sqlFechaRes = new java.sql.Date(reserva.getFechaRes().getTime());
        java.sql.Date sqlFechaVencimiento = java.sql.Date.valueOf(reserva.getFechaVencimiento());
        java.sql.Date sqlFechaInicio = java.sql.Date.valueOf(reserva.getFechaInicio());  // Nueva fechaInicio

        String queryReserva = "INSERT INTO Reservas (idHuesped, cantPersonas, observaciones, fechaRes, fechaVencimiento, fechaInicio) VALUES (?, ?, ?, ?, ?, ?)";
        boolean reservaCreada = false;

        try (Connection conn = getConnection();
             PreparedStatement pstReserva = conn.prepareStatement(queryReserva, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstReserva.setInt(1, reserva.getHuesped().getIdHuesped());
            pstReserva.setInt(2, reserva.getCantPersonas());
            pstReserva.setString(3, reserva.getObservaciones());
            pstReserva.setDate(4, sqlFechaRes);
            pstReserva.setDate(5, sqlFechaVencimiento);
            pstReserva.setDate(6, sqlFechaInicio);  // Incluir fechaInicio

            int affectedRows = pstReserva.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstReserva.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reserva.setIdReserva(generatedKeys.getInt(1));
                        reservaCreada = true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Ahora que tienes el idReserva generado, puedes insertar las habitaciones asociadas
        if (reservaCreada) {
            // Inserta las habitaciones asociadas en la tabla reservas_habitaciones
            insertarHabitaciones(reserva);
            // Inserta las tarifas asociadas a la reserva
            insertarTarifas(reserva);
        }

        return reservaCreada;
    }


    private void insertarHabitaciones(Reserva reserva) {
        String queryHabitaciones = "INSERT INTO reservas_habitaciones (idReserva, idHabitacion) VALUES (?, ?)";
        try (Connection conn = getConnection()) {
            for (Habitacion habitacion : reserva.getHabitaciones()) {
                try (PreparedStatement pstHabitacion = conn.prepareStatement(queryHabitaciones)) {
                    pstHabitacion.setInt(1, reserva.getIdReserva());
                    pstHabitacion.setInt(2, habitacion.getIdHabitacion());
                    pstHabitacion.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertarTarifas(Reserva reserva) {
        String queryTarifas = "INSERT INTO tarifas_reservas (idReserva, idTarifa) VALUES (?, ?)";

        // Convertir java.util.Date a java.sql.Date para la fecha de vencimiento
        java.sql.Date sqlFechaVencimiento = java.sql.Date.valueOf(reserva.getFechaVencimiento());

        for (Habitacion habitacion : reserva.getHabitaciones()) {
            TipoHabitacion tipoHabitacion = habitacion.getTipoHabitacion();
            Tarifa tarifa = obtenerTarifaVigente(tipoHabitacion, sqlFechaVencimiento); // Usar la fecha de vencimiento convertida

            try (Connection conn = getConnection();
                 PreparedStatement pstTarifa = conn.prepareStatement(queryTarifas)) {
                pstTarifa.setInt(1, reserva.getIdReserva());
                if (tarifa != null) {
                    pstTarifa.setInt(2, tarifa.getIdTarifa());
                } else {
                    System.out.println("No se encontró tarifa vigente para la habitación: " + tipoHabitacion.getIdTipoHabitacion());
                    // Manejar el caso de tarifa null si es necesario (puedes lanzar una excepción o continuar con otro comportamiento)
                    continue;
                }
                pstTarifa.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }






    // Método para obtener la tarifa vigente según el tipo de habitación y la fecha de vencimiento
    private Tarifa obtenerTarifaVigente(TipoHabitacion tipoHabitacion, java.sql.Date fechaVencimiento) {
        // Consulta para obtener la tarifa vigente basada en el idTipoHabitacion y la fecha de vencimiento
        String query = "SELECT t.idTarifa, t.precio, t.fechaFinVigencia FROM Tarifas t " +
                "WHERE t.idTipoHabitacion = ? AND t.fechaFinVigencia >= ? ORDER BY t.fechaFinVigencia DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            // Establecemos el idTipoHabitacion y la fecha de vencimiento
            pst.setInt(1, tipoHabitacion.getIdTipoHabitacion());
            pst.setDate(2, fechaVencimiento);  // Usamos java.sql.Date

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // Si encontramos una tarifa, la retornamos
                    return new Tarifa(rs.getInt("idTarifa"), tipoHabitacion, rs.getDouble("precio"), rs.getDate("fechaFinVigencia").toLocalDate());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Si no se encuentra tarifa, retornamos null
        return null;
    }


    public List<Reserva> getAllReservas() {
        List<Reserva> reservas = new ArrayList<>();
        String query = "SELECT * FROM Reservas ORDER BY idReserva";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int idReserva = resultSet.getInt("idReserva");
                int idHuesped = resultSet.getInt("idHuesped");
                int cantPersonas = resultSet.getInt("cantPersonas");
                String observaciones = resultSet.getString("observaciones");
                java.sql.Date fechaRes = resultSet.getDate("fechaRes");
                LocalDate fechaVencimiento = resultSet.getDate("fechaVencimiento").toLocalDate();
                LocalDate fechaInicio = resultSet.getDate("fechaInicio").toLocalDate();  // Nueva fechaInicio

                // Cargar el huésped completo
                HuespedDAO huespedDAO = new HuespedDAO();
                Huesped huesped = huespedDAO.getHuespedById(idHuesped);

                // Cargar las habitaciones asociadas
                List<Habitacion> habitaciones = getHabitacionesByReserva(idReserva);

                Reserva reserva = new Reserva(idReserva, huesped, habitaciones, cantPersonas, observaciones, fechaRes, fechaVencimiento, fechaInicio);  // Incluir fechaInicio
                reservas.add(reserva);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservas;
    }


    public Reserva getReservaById(int idReserva) {
        Reserva reserva = null;
        String query = "SELECT * FROM Reservas WHERE idReserva = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idReserva);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int idHuesped = resultSet.getInt("idHuesped");
                    int cantPersonas = resultSet.getInt("cantPersonas");
                    String observaciones = resultSet.getString("observaciones");
                    java.sql.Date fechaRes = resultSet.getDate("fechaRes");
                    LocalDate fechaVencimiento = resultSet.getDate("fechaVencimiento").toLocalDate();
                    LocalDate fechaInicio = resultSet.getDate("fechaInicio").toLocalDate();  // Nueva fechaInicio

                    // Cargar el huésped completo
                    HuespedDAO huespedDAO = new HuespedDAO();
                    Huesped huesped = huespedDAO.getHuespedById(idHuesped);

                    // Cargar las habitaciones asociadas
                    List<Habitacion> habitaciones = getHabitacionesByReserva(idReserva);

                    reserva = new Reserva(idReserva, huesped, habitaciones, cantPersonas, observaciones, fechaRes, fechaVencimiento, fechaInicio);  // Incluir fechaInicio
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reserva;
    }

    public boolean updateReserva(Reserva reserva) {
        String query = "UPDATE Reservas SET idHuesped = ?, cantPersonas = ?, observaciones = ?, fechaRes = ?, fechaVencimiento = ?, fechaInicio = ? WHERE idReserva = ?";
        return executeUpdate(query, reserva.getHuesped().getIdHuesped(), reserva.getCantPersonas(), reserva.getObservaciones(), reserva.getFechaRes(),
                reserva.getFechaVencimiento(), reserva.getFechaInicio(), reserva.getIdReserva());
    }


    public boolean deleteReserva(int idReserva) {
        String query = "DELETE FROM Reservas WHERE idReserva = ?";
        return executeUpdate(query, idReserva);
    }

    // Método para obtener las habitaciones asociadas a una reserva, incluyendo el tipo de habitación
    private List<Habitacion> getHabitacionesByReserva(int idReserva) {
        List<Habitacion> habitaciones = new ArrayList<>();
        String query = "SELECT h.idHabitacion, h.idHotel, h.ocupada, hotel.nombre, th.idTipoHabitacion, th.nombre AS tipoHabitacionNombre " +
                "FROM Habitaciones h " +
                "JOIN reservas_habitaciones rh ON h.idHabitacion = rh.idHabitacion " +
                "JOIN Hoteles hotel ON h.idHotel = hotel.idHotel " +
                "JOIN TiposHabitacion th ON h.idTipoHabitacion = th.idTipoHabitacion " + // Unir con la tabla TiposHabitacion
                "WHERE rh.idReserva = ? ORDER BY h.idHabitacion";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idReserva);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Obtener los datos de la habitación
                    int idHabitacion = resultSet.getInt("idHabitacion");
                    boolean ocupada = resultSet.getBoolean("ocupada");

                    // Crear el objeto Hotel
                    int idHotel = resultSet.getInt("idHotel");
                    String nombreHotel = resultSet.getString("nombre");
                    Hotel hotel = new Hotel(idHotel, nombreHotel);

                    // Crear el objeto TipoHabitacion
                    int idTipoHabitacion = resultSet.getInt("idTipoHabitacion");
                    String nombreTipoHabitacion = resultSet.getString("tipoHabitacionNombre");
                    TipoHabitacion tipoHabitacion = new TipoHabitacion(idTipoHabitacion, nombreTipoHabitacion);

                    // Crear el objeto Habitación con el Hotel y el TipoHabitacion
                    Habitacion habitacion = new Habitacion(idHabitacion, hotel, ocupada);
                    habitacion.setTipoHabitacion(tipoHabitacion); // Establecer el tipo de habitación
                    habitaciones.add(habitacion);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return habitaciones;
    }

    public boolean deleteHabitacionFromReserva(int idReserva, int idHabitacion) {
        // SQL para eliminar la relación entre la reserva y la habitación en la tabla "reservas_habitaciones"
        String sql = "DELETE FROM reservas_habitaciones WHERE idReserva = ? AND idHabitacion = ?";

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer los parámetros
            stmt.setInt(1, idReserva);
            stmt.setInt(2, idHabitacion);

            // Ejecutar la actualización
            int rowsAffected = stmt.executeUpdate();

            // Si se ha eliminado al menos una fila, la eliminación fue exitosa
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean agregarHabitacionAReserva(int idReserva, int idHabitacion) {
        // Verificar si la habitación ya está ocupada en las fechas de la reserva
        Reserva reservaExistente = getReservaById(idReserva);
        if (reservaExistente != null) {
            LocalDate fechaInicio = reservaExistente.getFechaInicio();
            LocalDate fechaVencimiento = reservaExistente.getFechaVencimiento();

            // Comprobar si la habitación ya está reservada en las fechas
            boolean habitacionOcupada = checkHabitacionOcupada(idHabitacion, fechaInicio, fechaVencimiento);
            if (habitacionOcupada) {
                System.out.println("La habitación ya está reservada en las fechas seleccionadas.");
                return false;  // La habitación ya está ocupada
            }
        }

        // Si la habitación no está ocupada, proceder con la inserción
        String query = "INSERT INTO reservas_habitaciones (idReserva, idHabitacion) VALUES (?, ?)";
        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idReserva);
            ps.setInt(2, idHabitacion);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Método para verificar si la habitación está ocupada en el rango de fechas
    public boolean checkHabitacionOcupada(int idHabitacion, LocalDate fechaInicio, LocalDate fechaVencimiento) {
        String query = "SELECT * FROM reservas_habitaciones rh " +
                "JOIN reservas r ON rh.idReserva = r.idReserva " +
                "WHERE rh.idHabitacion = ? " +
                "AND (r.fechaInicio < ? AND r.fechaVencimiento > ?)";

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idHabitacion);
            ps.setDate(2, Date.valueOf(fechaVencimiento));
            ps.setDate(3, Date.valueOf(fechaInicio));
            ResultSet rs = ps.executeQuery();

            // Si la consulta devuelve alguna fila, significa que la habitación está ocupada
            return rs.next();  // Si hay resultados, la habitación está ocupada
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


    }





}