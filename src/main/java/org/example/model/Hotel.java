package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private int idHotel;
    private Ciudad ciudad;
    private String nombre;
    private int estrellas;
    private List<Habitacion> habitaciones;

    public Hotel () {}

    public Hotel (int idHotel, String nombre)
    {
        this.idHotel = idHotel;
        this.nombre = nombre;
    }

    public Hotel(int idHotel, Ciudad ciudad, String nombre, int estrellas) {
        this.idHotel = idHotel;
        this.ciudad = ciudad;
        this.nombre = nombre;
        this.estrellas = estrellas;
    }
    // Constructor
    public Hotel(int idHotel, Ciudad ciudad, String nombre, int estrellas, List<Habitacion> habitaciones) {
        this.idHotel = idHotel;
        this.ciudad = ciudad;
        this.nombre = nombre;
        this.estrellas = estrellas;
        this.habitaciones = habitaciones;
    }

    // Getters y Setters
    public int getIdHotel() { return idHotel; }
    public void setIdHotel(int idHotel) { this.idHotel = idHotel; }

    public Ciudad getCiudad() { return ciudad; }
    public void setCiudad(Ciudad ciudad) { this.ciudad = ciudad; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getEstrellas() { return estrellas; }
    public void setEstrellas(int estrellas) { this.estrellas = estrellas; }

    public List<Habitacion> getHabitaciones() { return habitaciones; }
    public void setHabitaciones(List<Habitacion> habitaciones) { this.habitaciones = habitaciones; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------\n")
                .append("Hotel ID: ").append(idHotel).append("\n")
                .append("Nombre: ").append(nombre).append("\n")
                .append("Estrellas: ").append(estrellas).append("\n")
                .append("Ciudad: ").append(ciudad.getNombre()).append("\n")
                .append("País: ").append(ciudad.getPais().getNombre()).append("\n")
                .append("Habitaciones: \n");

        if (habitaciones != null && !habitaciones.isEmpty()) {
            for (Habitacion habitacion : habitaciones) {
                // Mostrar ID, TipoHabitacion y ocupación (ocupada o libre)
                sb.append("  - ").append(habitacion.getIdHabitacion()).append(". ")
                        .append(habitacion.getTipoHabitacion().getNombre()).append(", ")
                        .append(habitacion.isOcupada() ? "Ocupada" : "Libre").append("\n");
            }
        } else {
            sb.append("  No hay habitaciones disponibles.\n");
        }

        sb.append("-----------------------------------------");
        return sb.toString();
    }




}

