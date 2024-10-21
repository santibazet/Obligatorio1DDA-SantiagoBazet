package org.example.DAO;

import org.example.model.Huesped;
import org.example.model.Pais;
import org.example.model.TipoDocumento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HuespedDAO extends ConnectionDAO {

    private PaisDAO paisDAO = new PaisDAO();
    private TipoDocumentoDAO tipoDocumentoDAO = new TipoDocumentoDAO();

    public boolean createHuesped(Huesped huesped) {
        String query = "INSERT INTO Huespedes (idPais, idTipoDocumento, nombre, apaterno, amaterno, numDocumento, fechaNacimiento, telefono) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return executeUpdate(query, huesped.getPais().getIdPais(), huesped.getTipoDocumento().getIdTipoDocumento(), huesped.getNombre(),
                huesped.getApaterno(), huesped.getAmaterno(), huesped.getNumDocumento(),
                huesped.getFechaNacimiento(), huesped.getTelefono());
    }

    public List<Huesped> getAllHuespedes() {
        List<Huesped> huespedes = new ArrayList<>();
        String query = "SELECT h.idHuesped, h.nombre, h.apaterno, h.amaterno, h.numDocumento, " +
                "h.fechaNacimiento, h.telefono, p.nombre AS paisNombre, t.nombre AS tipoDocumentoNombre " +
                "FROM Huespedes h " +
                "JOIN Paises p ON h.idPais = p.idPais " +
                "JOIN TiposDocumento t ON h.idTipoDocumento = t.idTipoDocumento " +
                "ORDER BY h.idHuesped";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                // Crear objetos Pais y TipoDocumento con los datos obtenidos
                Pais pais = new Pais(resultSet.getString("paisNombre"));
                TipoDocumento tipoDocumento = new TipoDocumento(resultSet.getString("tipoDocumentoNombre"));

                // Crear el objeto Huesped
                Huesped huesped = new Huesped(
                        resultSet.getInt("idHuesped"),
                        pais,
                        tipoDocumento,
                        resultSet.getString("nombre"),
                        resultSet.getString("apaterno"),
                        resultSet.getString("amaterno"),
                        resultSet.getString("numDocumento"),
                        resultSet.getDate("fechaNacimiento"),
                        resultSet.getString("telefono")
                );
                huespedes.add(huesped);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return huespedes;
    }


    public boolean updateHuesped(Huesped huesped) {
        String query = "UPDATE Huespedes SET idPais = ?, idTipoDocumento = ?, nombre = ?, apaterno = ?, amaterno = ?, numDocumento = ?, fechaNacimiento = ?, telefono = ? WHERE idHuesped = ?";
        return executeUpdate(query, huesped.getPais().getIdPais(), huesped.getTipoDocumento().getIdTipoDocumento(), huesped.getNombre(),
                huesped.getApaterno(), huesped.getAmaterno(), huesped.getNumDocumento(),
                huesped.getFechaNacimiento(), huesped.getTelefono(), huesped.getIdHuesped());
    }

    public boolean deleteHuesped(int idHuesped) {
        String query = "DELETE FROM Huespedes WHERE idHuesped = ?";
        return executeUpdate(query, idHuesped);
    }

    public Huesped getHuespedById(int idHuesped) {
        Huesped huesped = null;
        String query = "SELECT h.*, p.idPais, p.nombre AS nombrePais, td.idTipoDocumento, td.nombre AS nombreTipoDocumento " +
                "FROM Huespedes h " +
                "JOIN Paises p ON h.idPais = p.idPais " +
                "JOIN TiposDocumento td ON h.idTipoDocumento = td.idTipoDocumento " +
                "WHERE h.idHuesped = ?";

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idHuesped);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Pais pais = new Pais(
                        rs.getInt("idPais"),
                        rs.getString("nombrePais")
                );

                TipoDocumento tipoDocumento = new TipoDocumento(
                        rs.getInt("idTipoDocumento"),
                        rs.getString("nombreTipoDocumento")
                );

                huesped = new Huesped(
                        rs.getInt("idHuesped"),
                        pais,
                        tipoDocumento,
                        rs.getString("nombre"),
                        rs.getString("apaterno"),
                        rs.getString("amaterno"),
                        rs.getString("numDocumento"),
                        rs.getDate("fechaNacimiento"),
                        rs.getString("telefono")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return huesped;
    }

    public boolean existsByNumDocumento(String numDocumento) {
        String query = "SELECT COUNT(*) FROM Huespedes WHERE numDocumento = ?";

        try (Connection conn = ConnectionDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, numDocumento); // Establecer el número de documento en la consulta

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Si el conteo es mayor que 0, el número de documento ya existe
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Si no se encontró el número de documento, retornamos false
    }


}


