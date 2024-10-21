package org.example.view;

import org.example.controller.HabitacionController;
import java.util.Scanner;

public class HabitacionView {
    private final HabitacionController habitacionController;

    public HabitacionView() {
        this.habitacionController = new HabitacionController();
    }

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("\n--- Menú de Habitaciones ---");
            System.out.println("1. Crear habitación");
            System.out.println("2. Ver todas las habitaciones");
            System.out.println("3. Actualizar habitación");
            System.out.println("4. Eliminar habitación");
            System.out.println("5. Listar habitaciones disponibles por hotel");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");
            option = scanner.nextInt();

            switch (option) {
                case 1:
                    habitacionController.createHabitacion(); // Método para crear habitación
                    break;
                case 2:
                    habitacionController.getAllHabitaciones(); // Método para listar todas las habitaciones
                    break;
                case 3:
                    habitacionController.updateHabitacion(); // Método para actualizar habitación
                    break;
                case 4:
                    habitacionController.deleteHabitacion(); // Método para eliminar habitación
                    break;
                case 5:
                    habitacionController.getHabitacionesDisponiblesPorHotel(); // Método para mostrar habitaciones disponibles por hotel
                    break;
                case 6:
                    System.out.println("Saliendo...");
                    return;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (option != 6);
    }
}
