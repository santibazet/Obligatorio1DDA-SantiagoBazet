package org.example.DAO;
import org.example.model.Pais;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaisDAO extends ConnectionDAO {

    public Pais getPaisById(int idPais) {
        String query = "SELECT * FROM Paises WHERE idPais = ?";
        Pais pais = null;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idPais);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                pais = new Pais(
                        resultSet.getInt("idPais"),
                        resultSet.getString("nombre")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pais;
    }

    public List<Pais> getAllPaises() {
        List<Pais> paises = new ArrayList<>();
        String query = "SELECT * FROM Paises ORDER BY idPais";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                paises.add(new Pais(
                        resultSet.getInt("idPais"),
                        resultSet.getString("nombre")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return paises;
    }

    public boolean existsById(int idPais) {
        String query = "SELECT COUNT(*) FROM Paises WHERE idPais = ?";
        boolean exists = false;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idPais); // Establecemos el idPais como parámetro
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Si el conteo es mayor que 0, significa que el país existe
                exists = resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }



}

