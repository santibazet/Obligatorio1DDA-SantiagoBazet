package org.example.DAO;

import org.example.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class HotelDAO extends ConnectionDAO {

    TipoHabitacionDAO tipoHabitacionDAO = new TipoHabitacionDAO();
    TipoCamaDAO tipoCamaDAO = new TipoCamaDAO();
    CaracteristicaDAO caracteristicaDAO = new CaracteristicaDAO();


public HotelDAO() {}

    public HotelDAO(TipoHabitacionDAO tipoHabitacionDAO, TipoCamaDAO tipoCamaDAO, CaracteristicaDAO caracteristicaDAO) {
        this.tipoHabitacionDAO = tipoHabitacionDAO;
        this.tipoCamaDAO = tipoCamaDAO;
        this.caracteristicaDAO = caracteristicaDAO;
    }


    public boolean existsById(int idHotel) {
        List<Hotel> hoteles = getAllHoteles();
        return hoteles.stream().anyMatch(hotel -> hotel.getIdHotel() == idHotel);
    }

    // Método para obtener todos los hoteles
    public List<Hotel> getAllHoteles() {
        List<Hotel> hoteles = new ArrayList<>();
        String query = "SELECT h.idHotel, h.nombre, h.estrellas, h.idCiudad, c.nombre AS ciudad, c.idPais, p.nombre AS pais " +
                "FROM Hoteles h " +
                "INNER JOIN Ciudades c ON h.idCiudad = c.idCiudad " +
                "INNER JOIN Paises p ON c.idPais = p.idPais ORDER BY h.idHotel";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                // Obtener el Pais asociado a la Ciudad
                Pais pais = new Pais(
                        resultSet.getInt("idPais"),
                        resultSet.getString("pais")
                );

                // Crear el objeto Ciudad con su Pais asociado
                Ciudad ciudad = new Ciudad(
                        resultSet.getInt("idCiudad"),
                        pais,
                        resultSet.getString("ciudad")
                );

                // Crear el objeto Hotel
                Hotel hotel = new Hotel(
                        resultSet.getInt("idHotel"),
                        ciudad,
                        resultSet.getString("nombre"),
                        resultSet.getInt("estrellas"),
                        new ArrayList<>() // Lista de habitaciones vacía por ahora
                );

                // Obtener las habitaciones asociadas a este hotel
                List<Habitacion> habitaciones = getHabitacionesByHotelId(hotel.getIdHotel());
                hotel.setHabitaciones(habitaciones); // Establecer las habitaciones en el hotel

                // Agregar el hotel a la lista
                hoteles.add(hotel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hoteles;
    }


    // Método para obtener un hotel por su ID y asignar sus habitaciones
    public Hotel getHotelById(int idHotel) {
        String query = "SELECT h.idHotel, h.nombre, h.estrellas, h.idCiudad, c.nombre AS ciudad, c.idPais, p.nombre AS pais " +
                "FROM Hoteles h " +
                "INNER JOIN Ciudades c ON h.idCiudad = c.idCiudad " +
                "INNER JOIN Paises p ON c.idPais = p.idPais " +
                "WHERE h.idHotel = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, idHotel);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Obtener el Pais asociado a la Ciudad
                    Pais pais = new Pais(
                            resultSet.getInt("idPais"),
                            resultSet.getString("pais")
                    );

                    // Crear el objeto Ciudad con su Pais asociado
                    Ciudad ciudad = new Ciudad(
                            resultSet.getInt("idCiudad"),
                            pais,
                            resultSet.getString("ciudad")
                    );

                    // Crear el objeto Hotel
                    Hotel hotel = new Hotel(
                            resultSet.getInt("idHotel"),
                            ciudad,
                            resultSet.getString("nombre"),
                            resultSet.getInt("estrellas"),
                            new ArrayList<>()
                    );

                    // Obtener y asignar las habitaciones del hotel
                    List<Habitacion> habitaciones = getHabitacionesByHotelId(idHotel);
                    hotel.setHabitaciones(habitaciones);

                    return hotel;  // Retornar el hotel con sus habitaciones
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;  // Retorna null si no se encuentra el hotel
    }


    // Método para actualizar los detalles de un hotel
    public boolean updateHotel(Hotel hotel) {
        String query = "UPDATE Hoteles SET nombre = ?, estrellas = ?, idCiudad = ? WHERE idHotel = ?";
        return executeUpdate(query, hotel.getNombre(), hotel.getEstrellas(), hotel.getCiudad().getIdCiudad(), hotel.getIdHotel());
    }

    // Método para eliminar un hotel por su ID
    public boolean deleteHotel(int idHotel) {
        String query = "DELETE FROM Hoteles WHERE idHotel = ?";
        return executeUpdate(query, idHotel);
    }

    public int createHotelAndGetId(Hotel hotel) {
        String query = "INSERT INTO Hoteles (nombre, idCiudad, estrellas) VALUES (?, ?, ?)";
        int idGenerado = -1;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Establecer los valores del hotel
            statement.setString(1, hotel.getNombre());
            statement.setInt(2, hotel.getCiudad().getIdCiudad()); // idCiudad de la ciudad asociada
            statement.setInt(3, hotel.getEstrellas());

            // Ejecutar la consulta
            int rowsAffected = statement.executeUpdate();

            // Si la inserción fue exitosa, obtener el ID generado automáticamente
            if (rowsAffected > 0) {
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

    public List<Habitacion> getHabitacionesByHotelId(int idHotel) {
        List<Habitacion> habitaciones = new ArrayList<>();
        String query = "SELECT * FROM Habitaciones WHERE idHotel = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idHotel);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // Obtener información de cada habitación
                int idHabitacion = resultSet.getInt("idHabitacion");
                int idTipoHabitacion = resultSet.getInt("idTipoHabitacion");
                int idTipoCama = resultSet.getInt("idTipoCama");
                int cantCamas = resultSet.getInt("cantCamas");
                boolean ocupada = resultSet.getBoolean("ocupada");

                // Obtener el tipo de habitación y cama desde sus respectivos DAOs
                TipoHabitacion tipoHabitacion = tipoHabitacionDAO.getTipoHabitacionById(idTipoHabitacion);
                TipoCama tipoCama = tipoCamaDAO.getTipoCamaById(idTipoCama);

                // Crear la lista de características de la habitación
                List<Caracteristica> caracteristicas = caracteristicaDAO.getCaracteristicasByHabitacionId(idHabitacion);

                // Crear el objeto Hotel (solo con el id) y asociarlo a la habitación
                Hotel hotel = new Hotel();
                hotel.setIdHotel(idHotel);

                // Crear el objeto Habitacion con el hotel asociado
                Habitacion habitacion = new Habitacion(idHabitacion, hotel, tipoHabitacion, tipoCama, cantCamas, ocupada, caracteristicas);

                // Añadir la habitación a la lista
                habitaciones.add(habitacion);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return habitaciones;
    }

    public List<Hotel> getHotelsByCity(String cityName) {
        List<Hotel> hoteles = new ArrayList<>();
        String query = "SELECT h.idHotel, h.nombre, h.estrellas, h.idCiudad, c.nombre AS ciudad, c.idPais, p.nombre AS pais " +
                "FROM Hoteles h " +
                "INNER JOIN Ciudades c ON h.idCiudad = c.idCiudad " +
                "INNER JOIN Paises p ON c.idPais = p.idPais " +
                "WHERE c.nombre LIKE ? ORDER BY h.idHotel";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + cityName + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Pais pais = new Pais(resultSet.getInt("idPais"), resultSet.getString("pais"));
                    Ciudad ciudad = new Ciudad(resultSet.getInt("idCiudad"), pais, resultSet.getString("ciudad"));
                    Hotel hotel = new Hotel(resultSet.getInt("idHotel"), ciudad, resultSet.getString("nombre"),
                            resultSet.getInt("estrellas"), new ArrayList<>());
                    hotel.setHabitaciones(getHabitacionesByHotelId(hotel.getIdHotel()));
                    hoteles.add(hotel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hoteles;
    }

    public List<Hotel> getHotelsByName(String name) {
        List<Hotel> hoteles = new ArrayList<>();
        String query = "SELECT h.idHotel, h.nombre, h.estrellas, h.idCiudad, c.nombre AS ciudad, c.idPais, p.nombre AS pais " +
                "FROM Hoteles h " +
                "INNER JOIN Ciudades c ON h.idCiudad = c.idCiudad " +
                "INNER JOIN Paises p ON c.idPais = p.idPais " +
                "WHERE h.nombre LIKE ? ORDER BY h.idHotel";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + name + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Pais pais = new Pais(resultSet.getInt("idPais"), resultSet.getString("pais"));
                    Ciudad ciudad = new Ciudad(resultSet.getInt("idCiudad"), pais, resultSet.getString("ciudad"));
                    Hotel hotel = new Hotel(resultSet.getInt("idHotel"), ciudad, resultSet.getString("nombre"),
                            resultSet.getInt("estrellas"), new ArrayList<>());
                    hotel.setHabitaciones(getHabitacionesByHotelId(hotel.getIdHotel()));
                    hoteles.add(hotel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hoteles;
    }

    public List<Hotel> getHotelsByStars(int stars) {
        List<Hotel> hoteles = new ArrayList<>();
        String query = "SELECT h.idHotel, h.nombre, h.estrellas, h.idCiudad, c.nombre AS ciudad, c.idPais, p.nombre AS pais " +
                "FROM Hoteles h " +
                "INNER JOIN Ciudades c ON h.idCiudad = c.idCiudad " +
                "INNER JOIN Paises p ON c.idPais = p.idPais " +
                "WHERE h.estrellas = ? ORDER BY h.idHotel";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, stars);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Pais pais = new Pais(resultSet.getInt("idPais"), resultSet.getString("pais"));
                    Ciudad ciudad = new Ciudad(resultSet.getInt("idCiudad"), pais, resultSet.getString("ciudad"));
                    Hotel hotel = new Hotel(resultSet.getInt("idHotel"), ciudad, resultSet.getString("nombre"),
                            resultSet.getInt("estrellas"), new ArrayList<>());
                    hotel.setHabitaciones(getHabitacionesByHotelId(hotel.getIdHotel()));
                    hoteles.add(hotel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hoteles;
    }


}
