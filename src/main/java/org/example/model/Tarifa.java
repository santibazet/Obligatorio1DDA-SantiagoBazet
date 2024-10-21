package org.example.model;

import java.time.LocalDate;
import java.util.Date;

public class Tarifa {
    private int idTarifa;
    private TipoHabitacion tipoHabitacion;  // Relación con TipoHabitacion
    private double precio;
    private LocalDate fechaFinVigencia;  // Fecha hasta la cual es válida la tarifa

    public Tarifa () {}

    // Constructor
    public Tarifa(int idTarifa, TipoHabitacion tipoHabitacion, double precio, LocalDate fechaFinVigencia) {
        this.idTarifa = idTarifa;
        this.tipoHabitacion = tipoHabitacion;
        this.precio = precio;
        this.fechaFinVigencia = fechaFinVigencia;
    }

    // Getters y setters
    public int getIdTarifa() {
        return idTarifa;
    }

    public void setIdTarifa(int idTarifa) {
        this.idTarifa = idTarifa;
    }

    public TipoHabitacion getTipoHabitacion() {
        return tipoHabitacion;
    }

    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public LocalDate getFechaFinVigencia() {
        return fechaFinVigencia;
    }

    public void setFechaFinVigencia(LocalDate fechaFinVigencia) {
        this.fechaFinVigencia = fechaFinVigencia;
    }

    public boolean esValidaEnFecha(LocalDate fecha) {
        return (fecha.isBefore(fechaFinVigencia) || fecha.isEqual(fechaFinVigencia));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------\n")
                .append("Tarifa ID: ").append(idTarifa).append("\n")
                .append("Tipo de Habitación: ").append(tipoHabitacion.getNombre()).append("\n")
                .append("Precio: $").append(precio).append("\n")
                .append("Fecha de fin de vigencia: ").append(fechaFinVigencia).append("\n")
                .append("-----------------------------------------");
        return sb.toString();
    }
}
