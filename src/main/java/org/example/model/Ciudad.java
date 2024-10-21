package org.example.model;

public class Ciudad {
    private int idCiudad;
    private Pais pais;
    private String nombre;

    public Ciudad() {}

    // Constructor
    public Ciudad(int idCiudad, Pais pais, String nombre) {
        this.idCiudad = idCiudad;
        this.pais = pais;
        this.nombre = nombre;
    }


    public Ciudad(int idCiudad, String nombre, Pais pais) {
        this.idCiudad = idCiudad;
        this.nombre = nombre;
        this.pais = pais;
    }

    // Getters y Setters
    public int getIdCiudad() { return idCiudad; }
    public void setIdCiudad(int idCiudad) { this.idCiudad = idCiudad; }

    public Pais getPais() { return pais; }
    public void setPais(Pais pais) { this.pais = pais; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}

