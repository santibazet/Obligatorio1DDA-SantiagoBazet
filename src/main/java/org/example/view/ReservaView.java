package org.example.view;

import org.example.controller.ReservaController;

import java.util.Scanner;

public class ReservaView {
    private final ReservaController reservaController;

    public ReservaView() {
        this.reservaController = new ReservaController();
    }

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("\n--- Menú de Reservas ---");
            System.out.println("1. Crear reserva");
            System.out.println("2. Ver todas las reservas");
            System.out.println("3. Actualizar reserva");
            System.out.println("4. Eliminar reserva");
            System.out.println("5. Listar hoteles por fecha");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");
            option = scanner.nextInt();

            switch (option) {
                case 1:
                    reservaController.createReserva();
                    break;
                case 2:
                    reservaController.getAllReservas();
                    break;
                case 3:
                    reservaController.updateReserva();
                    break;
                case 4:
                    reservaController.deleteReserva();
                    break;
                case 5:
                    reservaController.mostrarHotelesConHabitacionesDisponibles();
                    return;
                case 6:
                    System.out.println("Saliendo...");
                    return;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (option != 6);
    }
}