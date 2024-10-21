package org.example.DAO;

import org.example.model.Ciudad;
import org.example.model.Pais;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CiudadDAO extends ConnectionDAO {

    private PaisDAO paisDAO;

    public CiudadDAO() {
        this.paisDAO = new PaisDAO(); // Inicializamos paisDAO en el constructor
    }


    // Método para obtener una ciudad por su ID
    public Ciudad getCiudadById(int idCiudad) {
        String query = "SELECT c.idCiudad, c.nombre, p.idPais, p.nombre AS pais " +
                "FROM Ciudades c " +
                "INNER JOIN Paises p ON c.idPais = p.idPais " +
                "WHERE c.idCiudad = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, idCiudad);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Pais pais = new Pais(
                            resultSet.getInt("idPais"),
                            resultSet.getString("pais")
                    );

                    return new Ciudad(
                            resultSet.getInt("idCiudad"),
                            pais,
                            resultSet.getString("nombre")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Retorna null si no se encuentra la ciudad
    }

    // Método para obtener todas las ciudades
    public List<Ciudad> getAllCiudades() {
        List<Ciudad> ciudades = new ArrayList<>();
        String query = "SELECT c.idCiudad, c.nombre, p.idPais, p.nombre AS pais " +
                "FROM Ciudades c " +
                "INNER JOIN Paises p ON c.idPais = p.idPais " +
                "ORDER BY idCiudad";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Pais pais = new Pais(
                        resultSet.getInt("idPais"),
                        resultSet.getString("pais")
                );

                Ciudad ciudad = new Ciudad(
                        resultSet.getInt("idCiudad"),
                        pais,
                        resultSet.getString("nombre")
                );
                ciudades.add(ciudad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ciudades; // Retorna la lista de todas las ciudades
    }


    public List<Ciudad> getAllCiudadesByPaisId(int paisId) {
        List<Ciudad> ciudades = new ArrayList<>();
        String query = "SELECT * FROM ciudades WHERE idPais = ? ORDER BY idCiudad" ; // Suponiendo que la tabla se llama "ciudades"

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, paisId); // Establecemos el paisId como parámetro
            ResultSet resultSet = statement.executeQuery();

            // Iteramos sobre el ResultSet y creamos las instancias de Ciudad
            while (resultSet.next()) {
                Ciudad ciudad = new Ciudad(
                        resultSet.getInt("idCiudad"),
                        resultSet.getString("nombre"),
                        paisDAO.getPaisById(resultSet.getInt("idPais")) // Utilizamos paisDAO para obtener el país
                );
                ciudades.add(ciudad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ciudades;
    }


    public boolean existsById(int idCiudad) {
        String query = "SELECT COUNT(*) FROM Ciudades WHERE idCiudad = ?";
        boolean exists = false;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idCiudad); // Establecemos el idCiudad como parámetro
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Si el conteo es mayor que 0, significa que la ciudad existe
                exists = resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }

}
