package org.example.DAO;

import org.example.model.TipoDocumento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoDocumentoDAO extends ConnectionDAO {

    public TipoDocumento getTipoDocumentoById(int idTipoDocumento) {
        String query = "SELECT * FROM TiposDocumento WHERE idTipoDocumento = ?";
        TipoDocumento tipoDocumento = null;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idTipoDocumento);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                tipoDocumento = new TipoDocumento(
                        resultSet.getInt("idTipoDocumento"),
                        resultSet.getString("nombre")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tipoDocumento;
    }

    public List<TipoDocumento> getAllTipoDocumentos() {
        List<TipoDocumento> tiposDocumento = new ArrayList<>();
        String query = "SELECT * FROM TiposDocumento ORDER BY idTipoDocumento";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                tiposDocumento.add(new TipoDocumento(
                        resultSet.getInt("idTipoDocumento"),
                        resultSet.getString("nombre")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tiposDocumento;
    }
}
