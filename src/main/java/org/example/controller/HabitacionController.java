package org.example.controller;

import org.example.DAO.*;
import org.example.model.Habitacion;
import org.example.model.Hotel;
import org.example.model.TipoCama;
import org.example.model.TipoHabitacion;
import org.example.model.Caracteristica;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HabitacionController {
    private final HabitacionDAO habitacionDAO;
    private final HotelDAO hotelDAO;
    private final TipoCamaDAO tipoCamaDAO;
    private final TipoHabitacionDAO tipoHabitacionDAO;
    private final CaracteristicaDAO caracteristicaDAO;

    public HabitacionController() {
        this.habitacionDAO = new HabitacionDAO();
        this.hotelDAO = new HotelDAO();
        this.tipoCamaDAO = new TipoCamaDAO();
        this.tipoHabitacionDAO = new TipoHabitacionDAO();
        this.caracteristicaDAO = new CaracteristicaDAO();
    }

    public void createHabitacion() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("---- Creación de Habitación ----");

        // Listar los hoteles disponibles
        System.out.println("\nHoteles disponibles:");
        List<Hotel> hoteles = hotelDAO.getAllHoteles();
        for (Hotel hotel : hoteles) {
            System.out.println(hotel.getIdHotel() + ". " + hotel.getNombre());
        }

        int idHotel = -1;
        while (idHotel < 0) {
            System.out.print("Seleccione el número del hotel: ");
            idHotel = scanner.nextInt();
            scanner.nextLine();

            if (!hotelDAO.existsById(idHotel)) {
                System.out.println("El ID ingresado no existe. Por favor, ingrese un ID válido.");
                idHotel = -1;
            }
        }

        System.out.println("\nTipos de habitación disponibles:");
        List<TipoHabitacion> tiposHabitacion = tipoHabitacionDAO.getAllTiposHabitacion();
        for (TipoHabitacion tipoHabitacion : tiposHabitacion) {
            System.out.println(tipoHabitacion.getIdTipoHabitacion() + ". " + tipoHabitacion.getNombre());
        }

        int idTipoHabitacion = -1;
        while (idTipoHabitacion < 0) {
            System.out.print("Seleccione el número del tipo de habitación: ");
            idTipoHabitacion = scanner.nextInt();
            scanner.nextLine();

            if (!tipoHabitacionDAO.existsById(idTipoHabitacion)) {
                System.out.println("El ID ingresado no existe. Por favor, ingrese un ID válido.");
                idTipoHabitacion = -1;
            }
        }

        System.out.println("\nTipos de cama disponibles:");
        List<TipoCama> tiposCama = tipoCamaDAO.getAllTiposCama();
        for (TipoCama tipoCama : tiposCama) {
            System.out.println(tipoCama.getIdTipoCama() + ". " + tipoCama.getNombre());
        }

        int idTipoCama = -1;
        while (idTipoCama < 0) {
            System.out.print("Seleccione el número del tipo de cama: ");
            idTipoCama = scanner.nextInt();
            scanner.nextLine();

            if (!tipoCamaDAO.existsById(idTipoCama)) {
                System.out.println("El ID ingresado no existe. Por favor, ingrese un ID válido.");
                idTipoCama = -1;
            }
        }

        System.out.print("Ingrese la cantidad de camas: ");
        int cantCamas = scanner.nextInt();
        scanner.nextLine();

        System.out.print("¿Está la habitación ocupada? (true/false): ");
        boolean ocupada = scanner.nextBoolean();
        scanner.nextLine();

        System.out.println("\nCaracterísticas disponibles:");
        List<Caracteristica> caracteristicasDisponibles = caracteristicaDAO.getAllCaracteristicas();
        for (Caracteristica caracteristica : caracteristicasDisponibles) {
            System.out.println(caracteristica.getIdCaracteristica() + ". " + caracteristica.getNombre());
        }

        List<Caracteristica> caracteristicasSeleccionadas = new ArrayList<>();
        String respuesta;
        do {
            System.out.print("Seleccione el ID de la característica que desea agregar (o 'fin' para terminar): ");
            respuesta = scanner.nextLine();

            if (!respuesta.equalsIgnoreCase("fin")) {
                try {
                    int idCaracteristica = Integer.parseInt(respuesta);
                    Caracteristica caracteristicaSeleccionada = caracteristicasDisponibles.stream()
                            .filter(c -> c.getIdCaracteristica() == idCaracteristica)
                            .findFirst()
                            .orElse(null);

                    if (caracteristicaSeleccionada != null) {
                        caracteristicasSeleccionadas.add(caracteristicaSeleccionada);
                        System.out.println("Característica agregada: " + caracteristicaSeleccionada.getNombre());
                    } else {
                        System.out.println("El ID ingresado no es válido.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Por favor ingrese un número válido o 'fin' para terminar.");
                }
            }
        } while (!respuesta.equalsIgnoreCase("fin"));

        // Crear la nueva habitación
        Habitacion habitacion = new Habitacion(
                0,  // ID generado por la base de datos
                hotelDAO.getHotelById(idHotel),
                tipoHabitacionDAO.getTipoHabitacionById(idTipoHabitacion),
                tipoCamaDAO.getTipoCamaById(idTipoCama),
                cantCamas,
                ocupada,
                caracteristicasSeleccionadas
        );

        // Guardar la habitación en la base de datos y obtener el ID generado
        int idHabitacionGenerado = habitacionDAO.createHabitacionAndGetId(habitacion);
        if (idHabitacionGenerado > 0) {
            System.out.println("Habitación creada con éxito.");

            // Vincular características a la habitación en la tabla intermedia
            for (Caracteristica caracteristica : caracteristicasSeleccionadas) {
                if (habitacionDAO.addCaracteristicaToHabitacion(idHabitacionGenerado, caracteristica.getIdCaracteristica())) {
                    System.out.println("Característica vinculada: " + caracteristica.getNombre());
                } else {
                    System.out.println("Error al vincular la característica: " + caracteristica.getNombre());
                }
            }
        } else {
            System.out.println("Error al crear la habitación.");
        }
    }



    public void getAllHabitaciones() {
        List<Habitacion> habitaciones = habitacionDAO.getAllHabitaciones();
        if (habitaciones.isEmpty()) {
            System.out.println("No hay habitaciones registradas.");
            return;
        }

        System.out.println("Lista de habitaciones:");
        for (Habitacion habitacion : habitaciones) {
            System.out.println(habitacion);
        }
    }


    public void updateHabitacion() {
        getAllHabitaciones();  // Mostrar las habitaciones disponibles

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ID de la habitación a actualizar: ");
        int idHabitacion = scanner.nextInt();
        scanner.nextLine(); // Limpiar el buffer

        Habitacion habitacion = habitacionDAO.getAllHabitaciones().stream()
                .filter(h -> h.getIdHabitacion() == idHabitacion)
                .findFirst()
                .orElse(null);

        if (habitacion == null) {
            System.out.println("Habitación no encontrada.");
            return;
        }

        System.out.println("---- Actualización de Habitación ----");

        // Listar los hoteles disponibles y seleccionar uno
        System.out.println("\nHoteles disponibles:");
        List<Hotel> hoteles = hotelDAO.getAllHoteles();
        for (Hotel hotel : hoteles) {
            System.out.println(hotel.getIdHotel() + ". " + hotel.getNombre());
        }

        int idHotel = -1;
        while (idHotel < 0) {
            System.out.print("Seleccione el número del hotel (actual: " + habitacion.getHotel().getNombre() + "): ");
            idHotel = scanner.nextInt();
            scanner.nextLine();

            if (!hotelDAO.existsById(idHotel)) {
                System.out.println("El ID ingresado no existe. Por favor, ingrese un ID válido.");
                idHotel = -1;
            }
        }

        // Listar los tipos de habitación disponibles y seleccionar uno
        System.out.println("\nTipos de habitación disponibles:");
        List<TipoHabitacion> tiposHabitacion = tipoHabitacionDAO.getAllTiposHabitacion();
        for (TipoHabitacion tipo : tiposHabitacion) {
            System.out.println(tipo.getIdTipoHabitacion() + ". " + tipo.getNombre());
        }

        int idTipoHabitacion = -1;
        while (idTipoHabitacion < 0) {
            System.out.print("Seleccione el número del tipo de habitación (actual: " + habitacion.getTipoHabitacion().getNombre() + "): ");
            idTipoHabitacion = scanner.nextInt();
            scanner.nextLine();

            if (!tipoHabitacionDAO.existsById(idTipoHabitacion)) {
                System.out.println("El ID ingresado no existe. Por favor, ingrese un ID válido.");
                idTipoHabitacion = -1;
            }
        }

        // Listar los tipos de cama disponibles y seleccionar uno
        System.out.println("\nTipos de cama disponibles:");
        List<TipoCama> tiposCama = tipoCamaDAO.getAllTiposCama();
        for (TipoCama tipoCama : tiposCama) {
            System.out.println(tipoCama.getIdTipoCama() + ". " + tipoCama.getNombre());
        }

        int idTipoCama = -1;
        while (idTipoCama < 0) {
            System.out.print("Seleccione el número del tipo de cama (actual: " + habitacion.getTipoCama().getNombre() + "): ");
            idTipoCama = scanner.nextInt();
            scanner.nextLine();

            if (!tipoCamaDAO.existsById(idTipoCama)) {
                System.out.println("El ID ingresado no existe. Por favor, ingrese un ID válido.");
                idTipoCama = -1;
            }
        }

        // Actualizar la cantidad de camas
        System.out.print("Ingrese la nueva cantidad de camas (dejar vacío para no cambiar, actual: " + habitacion.getCantCamas() + "): ");
        String cantCamasInput = scanner.nextLine();
        if (!cantCamasInput.isEmpty()) {
            habitacion.setCantCamas(Integer.parseInt(cantCamasInput));
        }

        // Actualizar estado de ocupación
        System.out.print("¿Está la habitación ocupada? (true/false, actual: " + habitacion.isOcupada() + "): ");
        habitacion.setOcupada(scanner.nextBoolean());
        scanner.nextLine(); // Limpiar el buffer

        // Eliminar las características anteriores (relación en la tabla habitaciones_caracteristicas)
        if (habitacionDAO.removeAllCaracteristicasFromHabitacion(idHabitacion)) {
            System.out.println("Características antiguas eliminadas.");
        } else {
            System.out.println("Error al eliminar características antiguas.");
        }

        // Actualizar características
        System.out.println("Ingrese las nuevas características de la habitación (ingrese 'fin' para terminar): ");
        List<Caracteristica> caracteristicasDisponibles = caracteristicaDAO.getAllCaracteristicas();
        for (Caracteristica caracteristica : caracteristicasDisponibles) {
            System.out.println(caracteristica.getIdCaracteristica() + ". " + caracteristica.getNombre());
        }

        List<Caracteristica> caracteristicasSeleccionadas = new ArrayList<>();
        String caracteristica;
        while (!(caracteristica = scanner.nextLine()).equals("fin")) {
            try {
                int idCaracteristica = Integer.parseInt(caracteristica);
                Caracteristica caracteristicaSeleccionada = caracteristicasDisponibles.stream()
                        .filter(c -> c.getIdCaracteristica() == idCaracteristica)
                        .findFirst()
                        .orElse(null);

                if (caracteristicaSeleccionada != null) {
                    caracteristicasSeleccionadas.add(caracteristicaSeleccionada);
                    System.out.println("Característica agregada: " + caracteristicaSeleccionada.getNombre());
                } else {
                    System.out.println("El ID ingresado no es válido.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido o 'fin' para terminar.");
            }
        }
        habitacion.setCaracteristicas(caracteristicasSeleccionadas);


        // Actualiza el hotel de la habitación
        Hotel nuevoHotel = hotelDAO.getHotelById(idHotel);
        habitacion.setHotel(nuevoHotel);


        // Guardar los cambios en la base de datos
        if (habitacionDAO.updateHabitacion(habitacion)) {
            System.out.println("Habitación actualizada con éxito.");

            // Vincular nuevas características a la habitación en la tabla intermedia
            for (Caracteristica caracteristicaNueva : caracteristicasSeleccionadas) {
                if (habitacionDAO.addCaracteristicaToHabitacion(idHabitacion, caracteristicaNueva.getIdCaracteristica())) {
                    System.out.println("Característica vinculada: " + caracteristicaNueva.getNombre());
                } else {
                    System.out.println("Error al vincular la característica: " + caracteristicaNueva.getNombre());
                }
            }
        } else {
            System.out.println("Error al actualizar la habitación.");
        }
    }

    public void deleteHabitacion() {
        getAllHabitaciones();  // Mostrar las habitaciones disponibles

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ID de la habitación a eliminar: ");
        int idHabitacion = scanner.nextInt();
        scanner.nextLine();  // Limpiar el buffer

        // Buscar la habitación por ID
        Habitacion habitacion = habitacionDAO.getAllHabitaciones().stream()
                .filter(h -> h.getIdHabitacion() == idHabitacion)
                .findFirst()
                .orElse(null);

        if (habitacion == null) {
            System.out.println("Habitación no encontrada.");
            return;
        }

        // Confirmación para eliminar
        System.out.print("¿Está seguro que desea eliminar la habitación? (sí/no): ");
        String respuesta = scanner.nextLine();

        if (respuesta.equalsIgnoreCase("si")) {
            // Eliminar la habitación
            if (habitacionDAO.deleteHabitacion(idHabitacion)) {
                System.out.println("Habitación eliminada con éxito.");
            } else {
                System.out.println("Error al eliminar la habitación.");
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    public void getHabitacionesDisponiblesPorHotel() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("---- Consulta de Habitaciones Disponibles por Hotel ----");

        // Listar los hoteles disponibles
        System.out.println("\nHoteles disponibles:");
        List<Hotel> hoteles = hotelDAO.getAllHoteles();
        for (Hotel hotel : hoteles) {
            System.out.println(hotel.getIdHotel() + ". " + hotel.getNombre());
        }

        int idHotel = -1;
        while (idHotel < 0) {
            System.out.print("Seleccione el número del hotel: ");
            idHotel = scanner.nextInt();
            scanner.nextLine();

            if (!hotelDAO.existsById(idHotel)) {
                System.out.println("El ID ingresado no existe. Por favor, ingrese un ID válido.");
                idHotel = -1;
            }
        }

        // Obtener las habitaciones disponibles para el hotel seleccionado
        List<Habitacion> habitacionesDisponibles = habitacionDAO.getHabitacionesDisponiblesPorHotel(idHotel);

        if (habitacionesDisponibles.isEmpty()) {
            System.out.println("No hay habitaciones disponibles para el hotel seleccionado.");
        } else {
            System.out.println("\nHabitaciones disponibles para el hotel " + hotelDAO.getHotelById(idHotel).getNombre() + ":");
            for (Habitacion habitacion : habitacionesDisponibles) {
                System.out.println(habitacion);
            }
        }
    }

}
