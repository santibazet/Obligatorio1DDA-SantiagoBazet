package org.example.DAO;

import org.example.model.TipoCama;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoCamaDAO extends ConnectionDAO {


    public TipoCama getTipoCamaById(int idTipoCama) {
        // Obtener todos los tipos de cama
        List<TipoCama> tiposCama = getAllTiposCama();

        // Buscar el tipo de cama por ID
        for (TipoCama tipo : tiposCama) {
            if (tipo.getIdTipoCama() == idTipoCama) {
                return tipo;  // Retornar el tipo de cama encontrado
            }
        }

        // Si no se encuentra el tipo de cama, retornar null o lanzar una excepción
        return null;  // O puedes lanzar una excepción si prefieres
    }


    public boolean existsById(int idTipoCama) {
        List<TipoCama> tiposCama = getAllTiposCama();
        return tiposCama.stream().anyMatch(tipo -> tipo.getIdTipoCama() == idTipoCama);
    }

    public List<TipoCama> getAllTiposCama() {
        List<TipoCama> tiposCama = new ArrayList<>();
        String query = "SELECT * FROM TiposCama ORDER BY idTipoCama";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                TipoCama tipoCama = new TipoCama(
                        resultSet.getInt("idTipoCama"),
                        resultSet.getString("nombre")
                );
                tiposCama.add(tipoCama);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tiposCama;
    }
}
