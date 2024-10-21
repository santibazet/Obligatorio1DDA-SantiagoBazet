package org.example.model;
import java.util.List;
import java.util.Objects;

public class Habitacion {
    private int idHabitacion;
    private Hotel hotel;
    private TipoHabitacion tipoHabitacion;
    private TipoCama tipoCama;
    private int cantCamas;
    private boolean ocupada;
    private List<Caracteristica> caracteristicas;

    public Habitacion() {}

    public Habitacion(int idHabitacion, TipoHabitacion tipoHabitacion, boolean ocupada)
    {
        this.idHabitacion = idHabitacion;
        this.tipoHabitacion = tipoHabitacion;
        this.ocupada = ocupada;
    }

    public Habitacion(int idHabitacion, Hotel hotel, boolean ocupada)
    {
        this.idHabitacion = idHabitacion;
        this.hotel = hotel;
        this.ocupada = ocupada;
    }


    // Constructor
    public Habitacion(int idHabitacion, Hotel hotel, TipoHabitacion tipoHabitacion, TipoCama tipoCama, int cantCamas, boolean ocupada, List<Caracteristica> caracteristicas) {
        this.idHabitacion = idHabitacion;
        this.hotel = hotel;
        this.tipoHabitacion = tipoHabitacion;
        this.tipoCama = tipoCama;
        this.cantCamas = cantCamas;
        this.ocupada = ocupada;
        this.caracteristicas = caracteristicas;
    }

    // Getters y Setters
    public int getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(int idHabitacion) { this.idHabitacion = idHabitacion; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public TipoHabitacion getTipoHabitacion() { return tipoHabitacion; }
    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) { this.tipoHabitacion = tipoHabitacion; }

    public TipoCama getTipoCama() { return tipoCama; }
    public void setTipoCama(TipoCama tipoCama) { this.tipoCama = tipoCama; }

    public int getCantCamas() { return cantCamas; }
    public void setCantCamas(int cantCamas) { this.cantCamas = cantCamas; }

    public boolean isOcupada() { return ocupada; }
    public void setOcupada(boolean ocupada) { this.ocupada = ocupada; }

    public List<Caracteristica> getCaracteristicas() { return caracteristicas; }
    public void setCaracteristicas(List<Caracteristica> caracteristicas) { this.caracteristicas = caracteristicas;}


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------\n")
                .append("ID Habitacion: ").append(this.idHabitacion).append("\n")
                .append("Hotel: ").append(this.hotel.getNombre()).append("\n")
                .append("Tipo de habitación: ").append(this.tipoHabitacion.getNombre()).append("\n")
                .append("Tipo de cama: ").append(this.tipoCama.getNombre()).append("\n")
                .append("Cantidad de camas: ").append(this.cantCamas).append("\n")
                .append("Ocupada: ").append(this.ocupada ? "Sí" : "No").append("\n")
                .append("Características: \n");

        if (this.caracteristicas != null && !this.caracteristicas.isEmpty()) {
            for (Caracteristica caracteristica : this.caracteristicas) {
                sb.append("- ").append(caracteristica.getNombre()).append("\n");
            }
        } else {
            sb.append("Ninguna\n");
        }

        sb.append("-----------------------------------------");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Habitacion that = (Habitacion) o;
        return idHabitacion == that.idHabitacion; // Compara las habitaciones por ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(idHabitacion); // Usa el idHabitacion para generar el hash
    }

}

