package org.example.model;

public class Caracteristica {
    private int idCaracteristica;
    private String nombre;

    public Caracteristica() {}

    // Constructor
    public Caracteristica(int idCaracteristica, String nombre) {
        this.idCaracteristica = idCaracteristica;
        this.nombre = nombre;
    }

    public Caracteristica(String nombre) {
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getIdCaracteristica() { return idCaracteristica; }
    public void setIdCaracteristica(int idCaracteristica) { this.idCaracteristica = idCaracteristica; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}