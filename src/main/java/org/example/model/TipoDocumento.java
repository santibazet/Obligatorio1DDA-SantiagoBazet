package org.example.model;

public class TipoDocumento {
    private int idTipoDocumento;
    private String nombre;

    // Constructor
    public TipoDocumento(int idTipoDocumento, String nombre) {
        this.idTipoDocumento = idTipoDocumento;
        this.nombre = nombre;
    }

    public TipoDocumento(String nombre) {
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getIdTipoDocumento() { return idTipoDocumento; }
    public void setIdTipoDocumento(int idTipoDocumento) { this.idTipoDocumento = idTipoDocumento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}

