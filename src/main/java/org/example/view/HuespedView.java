package org.example.view;

import org.example.controller.HuespedController;

import java.util.InputMismatchException;
import java.util.Scanner;

public class HuespedView {
    private final HuespedController huespedController;

    public HuespedView() {
        this.huespedController = new HuespedController();
    }

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("\n--- Menú de Huéspedes ---");
            System.out.println("1. Crear huésped");
            System.out.println("2. Ver todos los huéspedes");
            System.out.println("3. Actualizar huésped");
            System.out.println("4. Eliminar huésped");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                option = scanner.nextInt();

                switch (option) {
                    case 1:
                        huespedController.createHuesped();
                        break;
                    case 2:
                        huespedController.getAllHuespedes();
                        break;
                    case 3:
                        huespedController.updateHuesped();
                        break;
                    case 4:
                        huespedController.deleteHuesped();
                        break;
                    case 5:
                        System.out.println("Saliendo...");
                        return;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número entero.");
                scanner.nextLine();
            }
        } while (true);
    }
}
