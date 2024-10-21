package org.example.model;

public class TipoCama {
    private int idTipoCama;
    private String nombre;

    // Constructor
    public TipoCama(int idTipoCama, String nombre) {
        this.idTipoCama = idTipoCama;
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getIdTipoCama() { return idTipoCama; }
    public void setIdTipoCama(int idTipoCama) { this.idTipoCama = idTipoCama; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
