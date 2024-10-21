package org.example.controller;

import org.example.DAO.*;
import org.example.model.*;

import java.time.ZoneId;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.ArrayList;

public class ReservaController {

    private ReservaDAO reservaDAO;
    private HuespedDAO huespedDAO;
    private HabitacionDAO habitacionDAO;
    private TarifaDAO tarifaDAO;
    private HotelDAO hotelDAO;
    private Scanner scanner;

    public ReservaController() {
        reservaDAO = new ReservaDAO();
        huespedDAO = new HuespedDAO();
        habitacionDAO = new HabitacionDAO();
        tarifaDAO = new TarifaDAO();
        hotelDAO = new HotelDAO();
        scanner = new Scanner(System.in);
    }

    public void createReserva() {
        // Mostrar todos los huéspedes disponibles
        List<Huesped> huespedes = huespedDAO.getAllHuespedes();
        if (huespedes.isEmpty()) {
            System.out.println("No hay huéspedes disponibles.");
            return;
        }

        System.out.println("Huéspedes disponibles:");
        for (Huesped huesped : huespedes) {
            System.out.println("ID: " + huesped.getIdHuesped() + ", Nombre: " + huesped.getNombre());
        }

        // Pedir el ID del huésped
        int idHuesped = -1;
        while (idHuesped < 0) {
            System.out.print("Ingrese el ID del huésped: ");
            String input = scanner.nextLine();
            if (input.matches("\\d+")) {
                idHuesped = Integer.parseInt(input);
                if (huespedDAO.getHuespedById(idHuesped) == null) {
                    System.out.println("El ID ingresado no es válido.");
                    idHuesped = -1;
                }
            } else {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
            }
        }

        // Mostrar hoteles disponibles
        List<Hotel> hoteles = hotelDAO.getAllHoteles();
        if (hoteles.isEmpty()) {
            System.out.println("No hay hoteles disponibles.");
            return;
        }

        System.out.println("Hoteles disponibles:");
        for (Hotel hotel : hoteles) {
            System.out.println("ID: " + hotel.getIdHotel() + ", Nombre: " + hotel.getNombre());
        }

        // Pedir el ID del hotel
        int idHotel = -1;
        while (idHotel < 0) {
            System.out.print("Ingrese el ID del hotel: ");
            String input = scanner.nextLine();
            if (input.matches("\\d+")) {
                idHotel = Integer.parseInt(input);
                if (hotelDAO.getHotelById(idHotel) == null) {
                    System.out.println("El ID ingresado no es válido.");
                    idHotel = -1;
                }
            } else {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
            }
        }

        // Ingresar la fecha de inicio (primer día de la reserva)
        LocalDate fechaInicio = null;
        while (fechaInicio == null) {
            System.out.print("Ingrese la fecha de inicio (primer día de la reserva) (yyyy-mm-dd): ");
            String input = scanner.nextLine();
            try {
                fechaInicio = LocalDate.parse(input);
                if (fechaInicio.isBefore(LocalDate.now())) {
                    System.out.println("La fecha de inicio no puede ser anterior a la fecha actual.");
                    fechaInicio = null;
                }
            } catch (Exception e) {
                System.out.println("Formato de fecha incorrecto. Intente nuevamente.");
            }
        }

        // Ingresar la fecha de vencimiento (fecha final de la reserva)
        LocalDate fechaVencimiento = null;
        while (fechaVencimiento == null) {
            System.out.print("Ingrese la fecha de vencimiento (fecha final de la reserva) (yyyy-mm-dd): ");
            String input = scanner.nextLine();
            try {
                fechaVencimiento = LocalDate.parse(input);
                if (fechaVencimiento.isBefore(fechaInicio)) {
                    System.out.println("La fecha de vencimiento no puede ser anterior a la fecha de inicio.");
                    fechaVencimiento = null;
                }
            } catch (Exception e) {
                System.out.println("Formato de fecha incorrecto. Intente nuevamente.");
            }
        }

        // Mostrar habitaciones disponibles en el hotel seleccionado para el rango de fechas
        List<Habitacion> habitacionesDisponibles = habitacionDAO.getHabitacionesDisponiblesPorHotelYFecha(idHotel, fechaInicio, fechaVencimiento);
        if (habitacionesDisponibles.isEmpty()) {
            System.out.println("No hay habitaciones disponibles en el hotel seleccionado para el rango de fechas.");
            return;
        }

        System.out.println("Habitaciones disponibles:");
        for (Habitacion habitacion : habitacionesDisponibles) {
            System.out.println("ID: " + habitacion.getIdHabitacion() + ", Tipo: " + habitacion.getTipoHabitacion().getNombre());
        }

        // Seleccionar habitaciones
        List<Habitacion> habitacionesSeleccionadas = new ArrayList<>();
        while (true) {
            System.out.print("Ingrese el ID de la habitación que desea reservar (o presione Enter para terminar): ");
            String input = scanner.nextLine();

            if (input.isEmpty()) break;

            try {
                int idHabitacion = Integer.parseInt(input);
                Habitacion habitacion = habitacionDAO.getHabitacionById(idHabitacion);

                if (habitacion == null) {
                    System.out.println("No se encontró habitación con ID: " + idHabitacion);
                } else if (habitacionesDisponibles.contains(habitacion)) {
                    System.out.println("Habitación seleccionada: " + habitacion);
                    habitacionesSeleccionadas.add(habitacion);
                } else {
                    System.out.println("ID de habitación no válido o la habitación no está disponible.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debes ingresar un número válido.");
            }
        }

        // Ingresar la cantidad de personas
        int cantPersonas = -1;
        while (cantPersonas < 1) {
            System.out.print("Ingrese la cantidad de personas para la reserva: ");
            String input = scanner.nextLine();
            try {
                cantPersonas = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Cantidad inválida. Ingrese un número válido.");
            }
        }

        // La fecha actual será la fecha de reserva
        LocalDate fechaReserva = LocalDate.now();  // Fecha actual

        // Pedir observaciones
        String observaciones = "";
        System.out.print("¿Desea agregar observaciones a la reserva? (s/n): ");
        String respuesta = scanner.nextLine();
        if (respuesta.equalsIgnoreCase("s")) {
            System.out.print("Ingrese las observaciones: ");
            observaciones = scanner.nextLine();
        }

        // Crear la reserva
        Huesped huesped = huespedDAO.getHuespedById(idHuesped);
        Reserva reserva = new Reserva(0, huesped, habitacionesSeleccionadas, cantPersonas, observaciones, java.sql.Date.valueOf(fechaReserva), fechaVencimiento, fechaInicio);

        // Asignar tarifas vigentes según la fecha de la reserva y el tipo de habitación
        List<Tarifa> tarifasSeleccionadas = new ArrayList<>();
        double costoTotal = 0;
        for (Habitacion habitacion : habitacionesSeleccionadas) {
            Tarifa tarifa = tarifaDAO.getTarifaVigente(habitacion.getTipoHabitacion().getIdTipoHabitacion(), fechaReserva);
            if (tarifa != null) {
                tarifasSeleccionadas.add(tarifa);  // Asignar la tarifa correspondiente a cada habitación
                costoTotal += tarifa.getPrecio();
            } else {
                System.out.println("No se encontró tarifa vigente para la habitación ID: " + habitacion.getIdHabitacion());
            }
        }

        long diasEstancia = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaVencimiento);
        costoTotal *= diasEstancia;

        System.out.println("El costo total de la reserva es: $" + costoTotal);

        // Establecer las tarifas en la reserva
        reserva.setTarifas(tarifasSeleccionadas);

        // Guardar la reserva en la base de datos
        if (reservaDAO.createReserva(reserva)) {
            System.out.println("Reserva creada con éxito.");
        } else {
            System.out.println("Error al crear la reserva.");
        }
    }



    // Método para listar todas las reservas
    public void getAllReservas() {
        List<Reserva> reservas = reservaDAO.getAllReservas();
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas registradas.");
            return;
        }

        System.out.println("Lista de Reservas:");
        for (Reserva reserva : reservas) {
            System.out.println(reserva.toString());
        }
    }

    // Método para actualizar una reserva
    public void updateReserva() {
        getAllReservas();  // Mostrar las reservas disponibles

        // Validación del ID de la reserva
        int idReserva = -1;
        while (idReserva < 0) {
            System.out.print("Ingrese el ID de la reserva a actualizar: ");
            String input = scanner.nextLine();
            if (input.matches("\\d+")) {  // Verifica si la entrada es un número
                idReserva = Integer.parseInt(input);
                if (reservaDAO.getReservaById(idReserva) == null) {
                    System.out.println("Reserva no encontrada.");
                    idReserva = -1;  // Si no se encuentra la reserva, repetir la entrada
                }
            } else {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
            }
        }

        // Buscar la reserva por ID
        Reserva reserva = reservaDAO.getReservaById(idReserva);
        if (reserva == null) {
            System.out.println("Reserva no encontrada.");
            return;
        }

        System.out.println("---- Actualización de Reserva ----");

        // Cambiar de huésped
        System.out.print("¿Desea cambiar el huésped de la reserva? (s/n): ");
        String respuesta = scanner.nextLine();
        if (respuesta.equalsIgnoreCase("s")) {
            List<Huesped> huespedes = huespedDAO.getAllHuespedes();
            if (huespedes.isEmpty()) {
                System.out.println("No hay huéspedes disponibles.");
                return;
            }

            System.out.println("Huéspedes disponibles:");
            for (Huesped huesped : huespedes) {
                System.out.println("ID: " + huesped.getIdHuesped() + ", Nombre: " + huesped.getNombre());
            }

            // Pedir el nuevo ID del huésped
            int idNuevoHuesped = -1;
            while (idNuevoHuesped < 0) {
                System.out.print("Ingrese el ID del nuevo huésped: ");
                String input = scanner.nextLine();
                if (input.matches("\\d+")) {
                    idNuevoHuesped = Integer.parseInt(input);
                    if (huespedDAO.getHuespedById(idNuevoHuesped) == null) {
                        System.out.println("El ID ingresado no es válido.");
                        idNuevoHuesped = -1;
                    }
                } else {
                    System.out.println("Entrada inválida. Por favor, ingrese un número.");
                }
            }
            Huesped nuevoHuesped = huespedDAO.getHuespedById(idNuevoHuesped);
            reserva.setHuesped(nuevoHuesped);
        }

        // Actualizar la fecha de inicio
        LocalDate nuevaFechaInicio = null;
        String input = "";
        while (nuevaFechaInicio == null) {
            System.out.print("Ingrese la nueva fecha de inicio (yyyy-mm-dd, actual: " + reserva.getFechaInicio() + "): ");
            input = scanner.nextLine();
            if (!input.isEmpty()) {
                try {
                    nuevaFechaInicio = LocalDate.parse(input);
                    if (nuevaFechaInicio.isBefore(LocalDate.now())) {
                        System.out.println("La fecha de inicio no puede ser anterior a la fecha actual.");
                        nuevaFechaInicio = null;  // Repetir la solicitud de fecha
                    } else {
                        reserva.setFechaInicio(nuevaFechaInicio);
                    }
                } catch (Exception e) {
                    System.out.println("Formato de fecha incorrecto.");
                }
            } else {
                System.out.println("Fecha de inicio no puede estar vacía.");
            }
        }

        // Actualizar la fecha de vencimiento
        LocalDate nuevaFechaVencimiento = null;
        while (nuevaFechaVencimiento == null) {
            System.out.print("Ingrese la nueva fecha de vencimiento (yyyy-mm-dd, actual: " + reserva.getFechaVencimiento() + "): ");
            input = scanner.nextLine();
            if (!input.isEmpty()) {
                try {
                    nuevaFechaVencimiento = LocalDate.parse(input);
                    if (nuevaFechaVencimiento.isBefore(nuevaFechaInicio)) {
                        System.out.println("La fecha de vencimiento no puede ser anterior a la fecha de inicio.");
                        nuevaFechaVencimiento = null;  // Repetir la solicitud de fecha
                    } else {
                        reserva.setFechaVencimiento(nuevaFechaVencimiento);
                    }
                } catch (Exception e) {
                    System.out.println("Formato de fecha incorrecto.");
                }
            } else {
                System.out.println("Fecha de vencimiento no puede estar vacía.");
            }
        }

        // Ahora que las fechas se han actualizado, validar la disponibilidad de las habitaciones

        // Cambiar habitaciones (agregar/eliminar)
        System.out.println("¿Desea agregar o eliminar habitaciones?");
        System.out.print("Ingrese 'a' para agregar habitaciones, 'e' para eliminar habitaciones o 'n' para mantener las habitaciones actuales: ");
        String opcion = scanner.nextLine();

        if (opcion.equalsIgnoreCase("a")) {
            // Obtener hotel y fechas de la reserva actualizadas
            Hotel hotel = reserva.getHabitaciones().get(0).getHotel();  // Asumimos que todas las habitaciones son del mismo hotel
            LocalDate fechaInicioReserva = reserva.getFechaInicio();
            LocalDate fechaVencimientoReserva = reserva.getFechaVencimiento();

            // Obtener habitaciones disponibles
            List<Habitacion> habitacionesDisponibles = habitacionDAO.getHabitacionesDisponiblesPorHotelYFecha(hotel.getIdHotel(), fechaInicioReserva, fechaVencimientoReserva);
            List<Habitacion> habitacionesReserva = reserva.getHabitaciones();

            habitacionesDisponibles.removeAll(habitacionesReserva);

            if (habitacionesDisponibles.isEmpty()) {
                System.out.println("No hay habitaciones disponibles en las fechas seleccionadas.");
            } else {
                System.out.println("Habitaciones disponibles:");
                for (Habitacion habitacion : habitacionesDisponibles) {
                    System.out.println("ID: " + habitacion.getIdHabitacion() + ", Tipo: " + habitacion.getTipoHabitacion().getNombre());
                }

                // Agregar nuevas habitaciones
                List<Habitacion> habitacionesSeleccionadas = new ArrayList<>(reserva.getHabitaciones());
                while (true) {
                    System.out.print("Ingrese el ID de la habitación que desea agregar (o presione Enter para terminar): ");
                    input = scanner.nextLine();

                    if (input.isEmpty()) break;

                    try {
                        int idHabitacion = Integer.parseInt(input);
                        Habitacion habitacion = habitacionDAO.getHabitacionById(idHabitacion);

                        if (habitacion == null) {
                            System.out.println("No se encontró habitación con ID: " + idHabitacion);
                        } else if (habitacionesDisponibles.contains(habitacion)) {
                            habitacionesSeleccionadas.add(habitacion);

                            // Registrar la relación en la tabla reservas_habitaciones
                            if (reservaDAO.agregarHabitacionAReserva(reserva.getIdReserva(), habitacion.getIdHabitacion())) {
                                System.out.println("Habitación agregada correctamente.");
                            } else {
                                System.out.println("Error al agregar la habitación.");
                            }

                        } else {
                            System.out.println("ID de habitación no válido o la habitación no está disponible.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Debes ingresar un número válido.");
                    }
                }

                // Actualizar las habitaciones de la reserva
                reserva.setHabitaciones(habitacionesSeleccionadas);
            }
        } else if (opcion.equalsIgnoreCase("e")) {
            // Eliminar habitaciones
            List<Habitacion> habitacionesDisponibles = reserva.getHabitaciones();
            if (habitacionesDisponibles.isEmpty()) {
                System.out.println("No hay habitaciones para eliminar.");
            } else {
                System.out.println("Habitaciones actuales:");
                for (Habitacion habitacion : habitacionesDisponibles) {
                    System.out.println("ID: " + habitacion.getIdHabitacion() + ", Tipo: " + habitacion.getTipoHabitacion().getNombre());
                }

                while (true) {
                    System.out.print("Ingrese el ID de la habitación que desea eliminar (o presione Enter para terminar): ");
                    input = scanner.nextLine();

                    if (input.isEmpty()) break;

                    try {
                        int idHabitacion = Integer.parseInt(input);
                        Habitacion habitacion = habitacionDAO.getHabitacionById(idHabitacion);

                        if (habitacion != null && habitacionesDisponibles.contains(habitacion)) {
                            // Eliminar la relación en la base de datos
                            if (reservaDAO.deleteHabitacionFromReserva(reserva.getIdReserva(), idHabitacion)) {
                                habitacionesDisponibles.remove(habitacion);
                                System.out.println("Habitación eliminada correctamente.");
                            } else {
                                System.out.println("Error al eliminar la habitación.");
                            }
                        } else {
                            System.out.println("Habitación no encontrada o no está asociada a la reserva.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Debes ingresar un número válido.");
                    }
                }

                // Actualizar las habitaciones de la reserva
                reserva.setHabitaciones(habitacionesDisponibles);
            }
        }

        // Guardar los cambios en la reserva
        reservaDAO.updateReserva(reserva);
        System.out.println("Reserva actualizada correctamente.");
    }




    // Método para eliminar una reserva
    public void deleteReserva() {
        getAllReservas();  // Mostrar las reservas disponibles

        int idReserva = -1;
        while (idReserva < 0) {
            System.out.print("Ingrese el ID de la reserva a eliminar: ");
            String input = scanner.nextLine();

            // Validar que el ID ingresado sea un número válido
            if (input.matches("\\d+")) {
                idReserva = Integer.parseInt(input);

                // Verificar si la reserva existe
                if (reservaDAO.getReservaById(idReserva) == null) {
                    System.out.println("Reserva no encontrada.");
                    idReserva = -1;  // Si la reserva no existe, repetir la solicitud de ID
                }
            } else {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
            }
        }

        // Confirmar la eliminación
        System.out.print("¿Está seguro de que desea eliminar la reserva? (s/n): ");
        String confirmacion = scanner.nextLine();
        if (confirmacion.equalsIgnoreCase("s")) {
            if (reservaDAO.deleteReserva(idReserva)) {
                System.out.println("Reserva eliminada con éxito.");
            } else {
                System.out.println("Error al eliminar la reserva.");
            }
        } else {
            System.out.println("Eliminación cancelada.");
        }
    }



    public void mostrarHotelesConHabitacionesDisponibles() {
        // Ingresar las fechas de inicio y fin
        LocalDate fechaInicio = null;
        LocalDate fechaFin = null;

        // Pedir fecha de inicio
        while (fechaInicio == null) {
            System.out.print("Ingrese la fecha de inicio (yyyy-mm-dd): ");
            String input = scanner.nextLine();
            try {
                fechaInicio = LocalDate.parse(input);
                if (fechaInicio.isBefore(LocalDate.now())) {
                    System.out.println("La fecha de inicio no puede ser anterior a la fecha actual.");
                    fechaInicio = null;
                }
            } catch (Exception e) {
                System.out.println("Formato de fecha incorrecto. Intente nuevamente.");
            }
        }

        // Pedir fecha de fin
        while (fechaFin == null) {
            System.out.print("Ingrese la fecha de fin (yyyy-mm-dd): ");
            String input = scanner.nextLine();
            try {
                fechaFin = LocalDate.parse(input);
                if (fechaFin.isBefore(fechaInicio)) {
                    System.out.println("La fecha de fin no puede ser anterior a la fecha de inicio.");
                    fechaFin = null;
                }
            } catch (Exception e) {
                System.out.println("Formato de fecha incorrecto. Intente nuevamente.");
            }
        }

        // Obtener la lista de hoteles
        List<Hotel> hoteles = hotelDAO.getAllHoteles();
        if (hoteles.isEmpty()) {
            System.out.println("No hay hoteles disponibles.");
            return;
        }

        System.out.println("Hoteles con habitaciones disponibles entre " + fechaInicio + " y " + fechaFin + ":");

        // Filtrar hoteles con habitaciones disponibles en el rango de fechas
        for (Hotel hotel : hoteles) {
            // Obtener habitaciones disponibles en el hotel para las fechas ingresadas
            List<Habitacion> habitacionesDisponibles = habitacionDAO.getHabitacionesDisponiblesPorHotelYFecha(hotel.getIdHotel(), fechaInicio, fechaFin);
            if (!habitacionesDisponibles.isEmpty()) {
                System.out.println("Hotel: " + hotel.getNombre() + " (ID: " + hotel.getIdHotel() + ")");
                System.out.println("Habitaciones disponibles:");
                for (Habitacion habitacion : habitacionesDisponibles) {
                    System.out.println("- ID: " + habitacion.getIdHabitacion() + ", Tipo: " + habitacion.getTipoHabitacion().getNombre());
                }
            }
        }
    }
}
