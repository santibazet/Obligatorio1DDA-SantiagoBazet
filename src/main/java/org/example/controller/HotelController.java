package org.example.controller;

import org.example.DAO.*;
import org.example.model.Habitacion;
import org.example.model.Hotel;
import org.example.model.Ciudad;
import org.example.model.Pais;

import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Scanner;

public class HotelController {
    private final HotelDAO hotelDAO;
    private final CiudadDAO ciudadDAO;
    private final PaisDAO paisDAO;

    public HotelController() {
        this.hotelDAO = new HotelDAO();
        this.ciudadDAO = new CiudadDAO();
        this.paisDAO = new PaisDAO();
    }

    public void createHotel() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("---- Creación de Hotel ----");

        // Listar los países disponibles
        System.out.println("\nPaíses disponibles:");
        List<Pais> paises = paisDAO.getAllPaises();
        for (Pais pais : paises) {
            System.out.println(pais.getIdPais() + ". " + pais.getNombre());
        }

        int idPais = -1;
        while (idPais < 0) {
            System.out.print("Seleccione el número del país: ");
            idPais = scanner.nextInt();
            scanner.nextLine();

            if (!paisDAO.existsById(idPais)) {
                System.out.println("El ID ingresado no existe. Por favor, ingrese un ID válido.");
                idPais = -1;
            }
        }

        // Listar las ciudades disponibles en el país seleccionado
        System.out.println("\nCiudades disponibles:");
        List<Ciudad> ciudades = ciudadDAO.getAllCiudadesByPaisId(idPais);
        for (Ciudad ciudad : ciudades) {
            System.out.println(ciudad.getIdCiudad() + ". " + ciudad.getNombre());
        }

        int idCiudad = -1;
        while (idCiudad < 0) {
            System.out.print("Seleccione el número de la ciudad: ");
            idCiudad = scanner.nextInt();
            scanner.nextLine();

            if (!ciudadDAO.existsById(idCiudad)) {
                System.out.println("El ID ingresado no existe. Por favor, ingrese un ID válido.");
                idCiudad = -1;
            }
        }

        // Ingresar el nombre del hotel
        System.out.print("Ingrese el nombre del hotel: ");
        String nombreHotel = scanner.nextLine();

        // Ingresar las estrellas del hotel
        int estrellas = -1;
        while (estrellas < 0 || estrellas > 5) {
            System.out.print("Ingrese la cantidad de estrellas del hotel (0-5): ");
            estrellas = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer
            if (estrellas < 0 || estrellas > 5) {
                System.out.println("Por favor, ingrese un valor entre 0 y 5.");
            }
        }

        // Crear el nuevo hotel
        Hotel hotel = new Hotel(
                0,  // ID generado por la base de datos
                ciudadDAO.getCiudadById(idCiudad),
                nombreHotel,
                estrellas
        );

        // Guardar el hotel en la base de datos y obtener el ID generado
        int idHotelGenerado = hotelDAO.createHotelAndGetId(hotel);
        if (idHotelGenerado > 0) {
            System.out.println("Hotel creado con éxito.");
        } else {
            System.out.println("Error al crear el hotel.");
        }
    }

    public void getAllHoteles() {
        List<Hotel> hoteles = hotelDAO.getAllHoteles();  // Asegúrate de que este método también obtenga las habitaciones
        if (hoteles.isEmpty()) {
            System.out.println("No hay hoteles registrados.");
            return;
        }

        System.out.println("Lista de hoteles:");
        for (Hotel hotel : hoteles) {
            // Imprimir información del hotel
            System.out.println(hotel);
        }
    }


    public void updateHotel() {
        getAllHoteles();  // Mostrar los hoteles disponibles

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ID del hotel a actualizar: ");
        int idHotel = scanner.nextInt();
        scanner.nextLine(); // Limpiar el buffer

        Hotel hotel = hotelDAO.getAllHoteles().stream()
                .filter(h -> h.getIdHotel() == idHotel)
                .findFirst()
                .orElse(null);

        if (hotel == null) {
            System.out.println("Hotel no encontrado.");
            return;
        }

        System.out.println("---- Actualización de Hotel ----");

        // Listar los países disponibles y seleccionar uno
        System.out.println("\nPaíses disponibles:");
        List<Pais> paises = paisDAO.getAllPaises();
        for (Pais pais : paises) {
            System.out.println(pais.getIdPais() + ". " + pais.getNombre());
        }

        int idPais = -1;
        while (idPais < 0) {
            System.out.print("Seleccione el número del país (actual: " + hotel.getCiudad().getPais().getNombre() + "): ");
            idPais = scanner.nextInt();
            scanner.nextLine();

            if (!paisDAO.existsById(idPais)) {
                System.out.println("El ID ingresado no existe. Por favor, ingrese un ID válido.");
                idPais = -1;
            }
        }

        // Listar las ciudades disponibles y seleccionar una
        System.out.println("\nCiudades disponibles:");
        List<Ciudad> ciudades = ciudadDAO.getAllCiudadesByPaisId(idPais);
        for (Ciudad ciudad : ciudades) {
            System.out.println(ciudad.getIdCiudad() + ". " + ciudad.getNombre());
        }

        int idCiudad = -1;
        while (idCiudad < 0) {
            System.out.print("Seleccione el número de la ciudad (actual: " + hotel.getCiudad().getNombre() + "): ");
            idCiudad = scanner.nextInt();
            scanner.nextLine();

            if (!ciudadDAO.existsById(idCiudad)) {
                System.out.println("El ID ingresado no existe. Por favor, ingrese un ID válido.");
                idCiudad = -1;
            }
        }

        // Actualizar la ciudad del hotel con la nueva ciudad seleccionada
        Ciudad nuevaCiudad = ciudadDAO.getCiudadById(idCiudad);
        hotel.setCiudad(nuevaCiudad);  // Actualizar el campo de ciudad

        // Ingresar el nuevo nombre del hotel
        System.out.print("Ingrese el nuevo nombre del hotel (actual: " + hotel.getNombre() + "): ");
        String nombreHotel = scanner.nextLine();
        if (!nombreHotel.isEmpty()) {
            hotel.setNombre(nombreHotel);
        }

        // Ingresar las nuevas estrellas
        int nuevasEstrellas = -1;
        while (nuevasEstrellas < 0 || nuevasEstrellas > 5) {
            System.out.print("Ingrese las nuevas estrellas del hotel (actual: " + hotel.getEstrellas() + "): ");
            String estrellasInput = scanner.nextLine();

            if (!estrellasInput.isEmpty()) {
                try {
                    nuevasEstrellas = Integer.parseInt(estrellasInput);
                    if (nuevasEstrellas < 0 || nuevasEstrellas > 5) {
                        System.out.println("El valor ingresado no es válido. Ingrese un número entre 0 y 5.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, ingrese un número válido.");
                }
            } else {
                break;  // Si el usuario no ingresa nada, mantenemos el valor actual
            }
        }

        // Actualizar las estrellas si el valor es válido
        if (nuevasEstrellas >= 0 && nuevasEstrellas <= 5) {
            hotel.setEstrellas(nuevasEstrellas);
        }

        // Guardar los cambios en la base de datos
        if (hotelDAO.updateHotel(hotel)) {
            System.out.println("Hotel actualizado con éxito.");
        } else {
            System.out.println("Error al actualizar el hotel.");
        }
    }


    public void deleteHotel() {
        getAllHoteles();  // Mostrar los hoteles disponibles

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ID del hotel a eliminar: ");
        int idHotel = scanner.nextInt();
        scanner.nextLine();  // Limpiar el buffer

        // Mostrar el hotel que se va a eliminar
        Hotel hotel = hotelDAO.getHotelById(idHotel);
        if (hotel != null) {
            System.out.println("Ha seleccionado el hotel: " + hotel.getNombre());
            System.out.print("¿Está seguro de que desea eliminar este hotel? (sí/no): ");
            String confirmacion = scanner.nextLine().trim().toLowerCase();

            if (confirmacion.equals("sí") || confirmacion.equals("si")) {
                // Eliminar el hotel
                if (hotelDAO.deleteHotel(idHotel)) {
                    System.out.println("Hotel eliminado con éxito.");
                } else {
                    System.out.println("Error al eliminar el hotel.");
                }
            } else {
                System.out.println("Operación cancelada. El hotel no fue eliminado.");
            }
        } else {
            System.out.println("No se encontró el hotel con el ID proporcionado.");
        }
    }


    public void filterHotelsByCity() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el nombre de la ciudad: ");
        String cityName = scanner.nextLine();
        List<Hotel> hoteles = hotelDAO.getHotelsByCity(cityName);
        displayHotels(hoteles);
    }

    public void filterHotelsByName() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el nombre del hotel: ");
        String name = scanner.nextLine();
        List<Hotel> hoteles = hotelDAO.getHotelsByName(name);
        displayHotels(hoteles);
    }

    public void filterHotelsByStars() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la cantidad de estrellas: ");
        int stars = scanner.nextInt();
        List<Hotel> hoteles = hotelDAO.getHotelsByStars(stars);
        displayHotels(hoteles);
    }

    private void displayHotels(List<Hotel> hoteles) {
        if (hoteles.isEmpty()) {
            System.out.println("No se encontraron hoteles con los criterios seleccionados.");
        } else {
            for (Hotel hotel : hoteles) {
                System.out.println(hotel);
            }
        }
    }

}
