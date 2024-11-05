package org.example;
import org.example.model.Hotel;
import org.example.view.*;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option = 0;

        while (option != 6) {
            System.out.println("\n--- Menú Principal ---");
            System.out.println("1. Gestionar Hoteles");
            System.out.println("2. Gestionar Habitaciones");
            System.out.println("3. Gestionar Huéspedes");
            System.out.println("4. Gestionar Tarifas");
            System.out.println("5. Gestionar Reservas");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                option = scanner.nextInt();

                switch (option) {
                    case 1:
                        HotelView hotelView = new HotelView();
                        hotelView.showMenu();
                        break;
                    case 2:
                        HabitacionView habitacionView = new HabitacionView();
                        habitacionView.showMenu();
                        break;
                    case 3:
                        HuespedView huespedView = new HuespedView();
                        huespedView.showMenu();
                        break;
                    case 4:
                        TarifaView tarifaView = new TarifaView();
                        tarifaView.showMenu();
                        break;
                    case 5:
                        ReservaView reservaView = new ReservaView();
                        reservaView.showMenu();
                        break;
                    case 6:
                        System.out.println("Saliendo...");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número entero.");
                scanner.nextLine();
            }
        }

        scanner.close();
    }
}
