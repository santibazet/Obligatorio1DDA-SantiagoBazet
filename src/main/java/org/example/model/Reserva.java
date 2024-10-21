package org.example.model;
import org.example.DAO.TarifaDAO;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class Reserva {
    private int idReserva;
    private Huesped huesped;
    private List<Habitacion> habitaciones;
    private int cantPersonas;
    private String observaciones;
    private Date fechaRes;
    private LocalDate fechaVencimiento;
    private LocalDate fechaInicio;
    private List<Tarifa> tarifas;

    // Constructor
    public Reserva(int idReserva, Huesped huesped, List<Habitacion> habitaciones, int cantPersonas, String observaciones, Date fechaRes, LocalDate fechaVencimiento, LocalDate fechaInicio, List<Tarifa> tarifas) {
        this.idReserva = idReserva;
        this.huesped = huesped;
        this.habitaciones = habitaciones;
        this.cantPersonas = cantPersonas;
        this.observaciones = observaciones;
        this.fechaRes = fechaRes;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaInicio = fechaInicio;  // Inicialización de la nueva propiedad
        this.tarifas = tarifas;
    }

    public Reserva(int idReserva, Huesped huesped, List<Habitacion> habitaciones, int cantPersonas, String observaciones, Date fechaRes, LocalDate fechaVencimiento, LocalDate fechaInicio) {
        this.idReserva = idReserva;
        this.huesped = huesped;
        this.habitaciones = habitaciones;
        this.cantPersonas = cantPersonas;
        this.observaciones = observaciones;
        this.fechaRes = fechaRes;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaInicio = fechaInicio;  // Inicialización de la nueva propiedad
    }

    // Getters y Setters
    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public Huesped getHuesped() { return huesped; }
    public void setHuesped(Huesped huesped) { this.huesped = huesped; }

    public List<Habitacion> getHabitaciones() { return habitaciones; }
    public void setHabitaciones(List<Habitacion> habitaciones) { this.habitaciones = habitaciones; }

    public int getCantPersonas() { return cantPersonas; }
    public void setCantPersonas(int cantPersonas) { this.cantPersonas = cantPersonas; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Date getFechaRes() { return fechaRes; }
    public void setFechaRes(Date fechaRes) { this.fechaRes = fechaRes; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public LocalDate getFechaInicio() { return fechaInicio; } // Getter para la nueva propiedad
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; } // Setter para la nueva propiedad

    public List<Tarifa> getTarifas() {
        return tarifas;
    }
    public void setTarifas(List<Tarifa> tarifas) {
        this.tarifas = tarifas;
    }

    // Método para calcular el costo total (sin almacenarlo)
    public double calcularCostoTotal() {
        TarifaDAO tarifaDAO = new TarifaDAO();
        LocalDate fechaReserva = LocalDate.now();  // O la fecha de reserva que se pase al crear la reserva
        double total = 0.0;

        for (Habitacion habitacion : habitaciones) {
            Tarifa tarifaVigente = tarifaDAO.getTarifaVigente(habitacion.getTipoHabitacion().getIdTipoHabitacion(), fechaReserva);
            if (tarifaVigente != null) {
                // Calcular el precio por noche
                double precioPorNoche = tarifaVigente.getPrecio();
                // Multiplicamos por la cantidad de noches reservadas
                long diasReservados = fechaVencimiento.toEpochDay() - fechaInicio.toEpochDay();
                total += precioPorNoche * diasReservados;
            }
        }

        return total;
    }

    // Método para obtener el nombre del hotel (sin almacenarlo)
    public String obtenerNombreHotel() {
        // Asumimos que todas las habitaciones son del mismo hotel
        if (habitaciones != null && !habitaciones.isEmpty()) {
            return habitaciones.get(0).getHotel().getNombre();
        }
        return "No especificado";
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------\n")
                .append("ID Reserva: ").append(this.idReserva).append("\n")
                .append("Huésped: ").append(this.huesped != null
                        ? this.huesped.getNombre() + " "
                        + this.huesped.getApaterno() + " "
                        + this.huesped.getAmaterno()
                        : "No especificado").append("\n")
                .append("Cantidad de personas: ").append(this.cantPersonas).append("\n")
                .append("Observaciones: ").append(this.observaciones != null ? this.observaciones : "Ninguna").append("\n")
                .append("Fecha de Reserva: ").append(this.fechaRes != null ? this.fechaRes.toString() : "No especificada").append("\n")
                .append("Fecha de Inicio: ").append(this.fechaInicio != null ? this.fechaInicio.toString() : "No especificada").append("\n")
                .append("Fecha de Vencimiento: ").append(this.fechaVencimiento != null ? this.fechaVencimiento.toString() : "No especificada").append("\n")
                .append("Nombre del Hotel: ").append(this.obtenerNombreHotel()).append("\n")
                .append("Costo Total: $").append(this.calcularCostoTotal()).append("\n")
                .append("Habitaciones reservadas: \n");

        if (this.habitaciones != null && !this.habitaciones.isEmpty()) {
            for (Habitacion habitacion : this.habitaciones) {
                sb.append("- ").append(habitacion.getIdHabitacion()).append(" (Tipo: ").append(habitacion.getTipoHabitacion().getNombre()).append(")\n");
            }
        } else {
            sb.append("Ninguna\n");
        }

        sb.append("-----------------------------------------");
        return sb.toString();
    }
}