package org.example.controller;


import org.example.DAO.TarifaDAO;
import org.example.DAO.TipoHabitacionDAO;
import org.example.model.Tarifa;
import org.example.model.TipoHabitacion;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class TarifaController {

    private TarifaDAO tarifaDAO;
    private TipoHabitacionDAO tipoHabitacionDAO;
    private Scanner scanner;

    public TarifaController() {
        tarifaDAO = new TarifaDAO();
        tipoHabitacionDAO = new TipoHabitacionDAO();
        scanner = new Scanner(System.in);
    }

    // Método para validar si ya existe una tarifa para el mismo TipoHabitacion con fechas solapadas
    private boolean esFechaValida(Tarifa nuevaTarifa) {
        List<Tarifa> tarifasExistentes = tarifaDAO.getAllTarifas();
        for (Tarifa tarifa : tarifasExistentes) {
            if (tarifa.getTipoHabitacion().getIdTipoHabitacion() == nuevaTarifa.getTipoHabitacion().getIdTipoHabitacion()) {
                // Verificar si la nueva fecha de fin es menor o igual a la fecha de fin de la tarifa existente
                // Esto asegura que no se solapen las tarifas
                if (!nuevaTarifa.getFechaFinVigencia().isAfter(tarifa.getFechaFinVigencia())) {
                    System.out.println("La tarifa no es válida. Se solapa con la tarifa existente que tiene fecha de fin: " + tarifa.getFechaFinVigencia());
                    return false;  // Las fechas se solapan, por lo que no es válida
                }
            }
        }
        return true; // La fecha es válida
    }



    // Método para crear una tarifa
    public void createTarifa() {
        // Mostrar todos los tipos de habitación
        List<TipoHabitacion> tiposHabitacion = tipoHabitacionDAO.getAllTiposHabitacion();
        if (tiposHabitacion.isEmpty()) {
            System.out.println("No hay tipos de habitación disponibles.");
            return;
        }

        System.out.println("Tipos de Habitación disponibles:");
        for (TipoHabitacion tipo : tiposHabitacion) {
            System.out.println("ID: " + tipo.getIdTipoHabitacion() + ", Nombre: " + tipo.getNombre());
        }

        // Pedir el ID del tipo de habitación
        int idTipoHabitacion = -1;
        while (idTipoHabitacion < 0) {
            System.out.print("Ingrese el id del Tipo de Habitación: ");
            String input = scanner.nextLine();
            if (input.matches("\\d+")) {
                idTipoHabitacion = Integer.parseInt(input);
                if (tipoHabitacionDAO.getTipoHabitacionById(idTipoHabitacion) == null) {
                    System.out.println("El ID ingresado no es válido.");
                    idTipoHabitacion = -1;  // Reiniciar el valor en caso de error
                }
            } else {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
            }
        }

        // Ingresar el precio de la tarifa
        double precio = -1;
        while (precio < 0) {
            System.out.print("Ingrese el precio de la tarifa: ");
            String precioStr = scanner.nextLine();
            try {
                precio = Double.parseDouble(precioStr);
                if (precio <= 0) {
                    System.out.println("El precio debe ser positivo. Intente nuevamente.");
                    precio = -1;
                }
            } catch (NumberFormatException e) {
                System.out.println("Precio inválido. Ingrese un número válido.");
            }
        }

        // Ingresar la fecha de fin de vigencia
        LocalDate fechaFinVigencia = null;
        while (fechaFinVigencia == null) {
            System.out.print("Ingrese la fecha de fin de vigencia (yyyy-mm-dd): ");
            String fechaFinVigenciaStr = scanner.nextLine();
            try {
                fechaFinVigencia = LocalDate.parse(fechaFinVigenciaStr);
            } catch (Exception e) {
                System.out.println("Formato de fecha incorrecto. Intente nuevamente.");
            }
        }

        // Crear la nueva tarifa
        Tarifa tarifa = new Tarifa(0, tipoHabitacionDAO.getTipoHabitacionById(idTipoHabitacion), precio, fechaFinVigencia);

        // Validar la fecha antes de guardar la tarifa
        if (!esFechaValida(tarifa)) {
            return; // Salir del método si las fechas se solapan
        }

        // Guardar la tarifa en la base de datos
        int idTarifaGenerado = tarifaDAO.createTarifaAndGetId(tarifa);
        if (idTarifaGenerado > 0) {
            System.out.println("Tarifa creada con éxito.");
        } else {
            System.out.println("Error al crear la tarifa.");
        }
    }

    // Método para mostrar todas las tarifas
    public void getAllTarifas() {
        List<Tarifa> tarifas = tarifaDAO.getAllTarifas();
        if (tarifas.isEmpty()) {
            System.out.println("No hay tarifas registradas.");
            return;
        }

        System.out.println("Lista de Tarifas:");
        for (Tarifa tarifa : tarifas) {
            System.out.println(tarifa.toString());
        }
    }

    // Método para actualizar una tarifa
    public void updateTarifa() {
        getAllTarifas();  // Mostrar las tarifas disponibles

        System.out.print("Ingrese el ID de la tarifa a actualizar: ");
        int idTarifa = Integer.parseInt(scanner.nextLine());

        // Buscar la tarifa por ID
        Tarifa tarifa = tarifaDAO.getTarifaById(idTarifa);
        if (tarifa == null) {
            System.out.println("Tarifa no encontrada.");
            return;
        }

        System.out.println("---- Actualización de Tarifa ----");

        // Actualizar el tipo de habitación
        List<TipoHabitacion> tiposHabitacion = tipoHabitacionDAO.getAllTiposHabitacion();
        System.out.println("\nTipos de Habitación disponibles:");
        for (TipoHabitacion tipo : tiposHabitacion) {
            System.out.println("ID: " + tipo.getIdTipoHabitacion() + ", Nombre: " + tipo.getNombre());
        }

        System.out.print("Seleccione el nuevo ID del Tipo de Habitación (actual: " + tarifa.getTipoHabitacion().getNombre() + ") o presione Enter para mantener el actual: ");
        String idTipoHabitacionInput = scanner.nextLine();
        if (!idTipoHabitacionInput.isEmpty()) {
            int idTipoHabitacion = Integer.parseInt(idTipoHabitacionInput);
            TipoHabitacion tipoHabitacionSeleccionado = tipoHabitacionDAO.getTipoHabitacionById(idTipoHabitacion);
            if (tipoHabitacionSeleccionado != null) {
                tarifa.setTipoHabitacion(tipoHabitacionSeleccionado);
            } else {
                System.out.println("El ID ingresado no es válido. Se mantiene el valor actual.");
            }
        }

        // Actualizar el precio
        System.out.print("Ingrese el nuevo precio de la tarifa (actual: " + tarifa.getPrecio() + ") o presione Enter para mantener el actual: ");
        String nuevoPrecioInput = scanner.nextLine();
        if (!nuevoPrecioInput.isEmpty()) {
            try {
                double nuevoPrecio = Double.parseDouble(nuevoPrecioInput);
                tarifa.setPrecio(nuevoPrecio);
            } catch (NumberFormatException e) {
                System.out.println("Precio inválido. Se mantiene el valor actual.");
            }
        }

        // Actualizar la fecha de fin de vigencia
        System.out.print("Ingrese la nueva fecha de fin de vigencia (yyyy-mm-dd, actual: " + tarifa.getFechaFinVigencia() + ") o presione Enter para mantener el valor actual: ");
        String nuevaFechaFinVigenciaInput = scanner.nextLine();
        if (!nuevaFechaFinVigenciaInput.isEmpty()) {
            try {
                LocalDate nuevaFechaFinVigencia = LocalDate.parse(nuevaFechaFinVigenciaInput);
                tarifa.setFechaFinVigencia(nuevaFechaFinVigencia);
            } catch (Exception e) {
                System.out.println("Fecha inválida. Se mantiene el valor actual.");
            }
        }

        // Validar las fechas antes de guardar la tarifa actualizada
        if (!esFechaValida(tarifa)) {
            return; // Salir del método si las fechas se solapan
        }

        // Guardar los cambios en la base de datos
        if (tarifaDAO.updateTarifa(tarifa)) {
            System.out.println("Tarifa actualizada con éxito.");
        } else {
            System.out.println("Error al actualizar la tarifa.");
        }
    }


    // Método para eliminar una tarifa
    public void deleteTarifa() {
        getAllTarifas();  // Mostrar las tarifas disponibles

        System.out.print("Ingrese el ID de la tarifa a eliminar: ");
        int idTarifa = Integer.parseInt(scanner.nextLine());

        // Confirmar la eliminación
        System.out.print("¿Está seguro de que desea eliminar la tarifa? (sí/no): ");
        String respuesta = scanner.nextLine();

        if (respuesta.equalsIgnoreCase("si")) {
            // Eliminar la tarifa
            if (tarifaDAO.deleteTarifa(idTarifa)) {
                System.out.println("Tarifa eliminada con éxito.");
            } else {
                System.out.println("Error al eliminar la tarifa.");
            }
        } else {
            System.out.println("Eliminación cancelada.");
        }
    }



}
