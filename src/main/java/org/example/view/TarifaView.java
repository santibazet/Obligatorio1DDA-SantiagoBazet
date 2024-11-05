package org.example.view;

import org.example.controller.TarifaController;

import java.util.InputMismatchException;
import java.util.Scanner;

public class TarifaView {

    private final TarifaController tarifaController;

    public TarifaView() {
        this.tarifaController = new TarifaController();
    }

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("\n--- Menú de Tarifas ---");
            System.out.println("1. Crear tarifa");
            System.out.println("2. Ver todas las tarifas");
            System.out.println("3. Actualizar tarifa");
            System.out.println("4. Eliminar tarifa");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                option = scanner.nextInt();

                switch (option) {
                    case 1:
                        tarifaController.createTarifa();
                        break;
                    case 2:
                        tarifaController.getAllTarifas();
                        break;
                    case 3:
                        tarifaController.updateTarifa();
                        break;
                    case 4:
                        tarifaController.deleteTarifa();
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
