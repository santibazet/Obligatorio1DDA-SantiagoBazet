package org.example.view;

import org.example.controller.HotelController;
import org.example.model.Hotel;

import java.util.Scanner;
import java.util.List;

public class HotelView {

    private final HotelController hotelController;
    private final Scanner scanner;

    public HotelView() {
        this.hotelController = new HotelController();
        this.scanner = new Scanner(System.in);
    }

    // Método para mostrar el menú de opciones de hoteles
    public void showMenu() {
        while (true) {
            System.out.println("\n---- Gestión de Hoteles ----");
            System.out.println("1. Crear nuevo hotel");
            System.out.println("2. Listar hoteles");
            System.out.println("3. Filtrar hoteles por ciudad");
            System.out.println("4. Filtrar hoteles por nombre");
            System.out.println("5. Filtrar hoteles por estrellas");
            System.out.println("6. Actualizar hotel");
            System.out.println("7. Eliminar hotel");
            System.out.println("8. Salir");
            System.out.print("Seleccione una opción: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    hotelController.createHotel();
                    break;
                case 2:
                    hotelController.getAllHoteles();
                    break;
                case 3:
                    hotelController.filterHotelsByCity();
                    break;
                case 4:
                    hotelController.filterHotelsByName();
                    break;
                case 5:
                    hotelController.filterHotelsByStars();
                    break;
                case 6:
                    hotelController.updateHotel();
                    break;
                case 7:
                    hotelController.deleteHotel();
                    break;
                case 8:
                    System.out.println("Saliendo...");
                    return;
                default:
                    System.out.println("Opción inválida. Por favor, ingrese una opción válida.");
            }
        }
    }



}
