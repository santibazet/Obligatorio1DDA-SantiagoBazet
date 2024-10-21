package org.example.controller;

import org.example.DAO.HuespedDAO;
import org.example.DAO.PaisDAO;
import org.example.DAO.TipoDocumentoDAO;
import org.example.model.Huesped;
import org.example.model.Pais;
import org.example.model.TipoDocumento;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class HuespedController {
    private final HuespedDAO huespedDAO;
    private final PaisDAO paisDAO;
    private final TipoDocumentoDAO tipoDocumentoDAO;

    public HuespedController() {
        this.huespedDAO = new HuespedDAO();
        this.paisDAO = new PaisDAO();
        this.tipoDocumentoDAO = new TipoDocumentoDAO();
    }

    public void createHuesped() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("---- Creación de Huésped ----");

        System.out.print("\nIngrese el nombre del huésped: ");
        String nombre = scanner.nextLine();

        System.out.print("Ingrese el apellido paterno: ");
        String apaterno = scanner.nextLine();

        System.out.print("Ingrese el apellido materno: ");
        String amaterno = scanner.nextLine();

        // Listar los tipos de documentos disponibles con formato "ID. Nombre"
        System.out.println("\nTipos de documento disponibles:");
        List<TipoDocumento> tiposDocumento = tipoDocumentoDAO.getAllTipoDocumentos();
        for (TipoDocumento tipoDocumento : tiposDocumento) {
            System.out.println(tipoDocumento.getIdTipoDocumento() + ". " + tipoDocumento.getNombre());
        }

        int idTipoDocumento = -1; // Inicializar con un valor no válido
        // Mientras el ID no sea válido
        while (idTipoDocumento < 0) {
            System.out.print("Seleccione el número del tipo de documento: ");
            String input = scanner.nextLine(); // Leer como String

            // Verificar si la entrada es numérica
            if (input.matches("\\d+")) {
                idTipoDocumento = Integer.parseInt(input); // Convertir a entero

                // Verificar si el ID ingresado existe en la lista
                boolean exists = false;
                for (TipoDocumento tipo : tiposDocumento) {
                    if (tipo.getIdTipoDocumento() == idTipoDocumento) {
                        exists = true;
                        break; // Salir del bucle si se encuentra
                    }
                }

                if (!exists) {
                    System.out.println("El ID ingresado no existe. Por favor, ingrese un ID válido.");
                    idTipoDocumento = -1; // Reiniciar el valor si no existe
                }
            } else {
                System.out.println("Entrada inválida. Por favor, ingrese un número válido.");
            }
        }

        System.out.print("Ingrese el número de documento: ");
        String numDocumento = scanner.nextLine();

        // Verificar si el número de documento ya existe
        if (huespedDAO.existsByNumDocumento(numDocumento)) {
            System.out.println("El número de documento ya está registrado. No se puede crear el huésped.");
            return; // Salir sin continuar el proceso
        }

        // Listar los países disponibles con formato "ID. Nombre"
        System.out.println("\nPaíses disponibles:");
        List<Pais> paises = paisDAO.getAllPaises();
        for (Pais pais : paises) {
            System.out.println(pais.getIdPais() + ". " + pais.getNombre());
        }
        int idPais = -1; // Inicializar con un valor no válido

        // Mientras el ID no sea válido
        while (idPais < 0) {
            System.out.print("Seleccione el número del país: ");
            String input = scanner.nextLine(); // Leer como String

            // Verificar si la entrada es numérica
            if (input.matches("\\d+")) {
                idPais = Integer.parseInt(input); // Convertir a entero

                // Verificar si el ID ingresado existe en la lista
                boolean exists = false;
                for (Pais pais : paises) {
                    if (pais.getIdPais() == idPais) {
                        exists = true;
                        break; // Salir del bucle si se encuentra
                    }
                }

                if (!exists) {
                    System.out.println("El ID ingresado no existe. Por favor, ingrese un ID válido.");
                    idPais = -1; // Reiniciar el valor si no existe
                }
            } else {
                System.out.println("Entrada inválida. Por favor, ingrese un número válido.");
            }
        }

        String fechaNacimiento = null;
        while (fechaNacimiento == null) {
            System.out.print("Ingrese la fecha de nacimiento (yyyy-mm-dd): ");
            String fechaInput = scanner.nextLine();
            if (fechaInput.matches("\\d{4}-\\d{2}-\\d{2}")) {
                fechaNacimiento = fechaInput;
            } else {
                System.out.println("Formato de fecha incorrecto. Intente nuevamente.");
            }
        }

        System.out.print("Ingrese el teléfono: ");
        String telefono = scanner.nextLine();

        Huesped huesped = new Huesped(
                paisDAO.getPaisById(idPais),
                tipoDocumentoDAO.getTipoDocumentoById(idTipoDocumento),
                nombre,
                apaterno,
                amaterno,
                numDocumento,
                java.sql.Date.valueOf(fechaNacimiento),
                telefono
        );

        if (huespedDAO.createHuesped(huesped)) {
            System.out.println("Huésped creado con éxito.");
        } else {
            System.out.println("Error al crear el huésped.");
        }
    }

    public void getAllHuespedes() {
        List<Huesped> huespedes = huespedDAO.getAllHuespedes();
        huespedes.sort(Comparator.comparingInt(Huesped::getIdHuesped));
        if (huespedes.isEmpty()) {
            System.out.println("No hay huéspedes registrados.");
            return;
        }

        System.out.println("Lista de huéspedes:");
        for (Huesped huesped : huespedes) {
            System.out.println(huesped);
        }
    }

    public void updateHuesped() {
        getAllHuespedes();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ID del huésped a actualizar: ");
        int idHuesped = scanner.nextInt();
        scanner.nextLine(); // Limpiar el buffer

        Huesped huesped = huespedDAO.getAllHuespedes().stream()
                .filter(h -> h.getIdHuesped() == idHuesped)
                .findFirst()
                .orElse(null);

        if (huesped == null) {
            System.out.println("Huésped no encontrado.");
            return;
        }

        // Actualizar otros atributos del huésped
        System.out.print("Ingrese el nuevo nombre (dejar vacío para no cambiar): ");
        String nombre = scanner.nextLine();
        if (!nombre.isEmpty()) {
            huesped.setNombre(nombre);
        }

        System.out.print("Ingrese el nuevo apellido paterno (dejar vacío para no cambiar): ");
        String apaterno = scanner.nextLine();
        if (!apaterno.isEmpty()) {
            huesped.setApaterno(apaterno);
        }

        System.out.print("Ingrese el nuevo apellido materno (dejar vacío para no cambiar): ");
        String amaterno = scanner.nextLine();
        if (!amaterno.isEmpty()) {
            huesped.setAmaterno(amaterno);
        }

        // Listar los países disponibles con formato "ID. Nombre"
        System.out.println("\nPaíses disponibles:");
        List<Pais> paises = paisDAO.getAllPaises();
        for (Pais pais : paises) {
            System.out.println(pais.getIdPais() + ". " + pais.getNombre());
        }

        int idPais = -1; // Inicializar con un valor no válido

        // Mientras el ID no sea válido
        while (idPais < 0) {
            System.out.print("Seleccione el número del país: ");
            String inputt = scanner.nextLine(); // Leer como String

            // Verificar si la entrada es numérica
            if (inputt.matches("\\d+")) {
                idPais = Integer.parseInt(inputt); // Convertir a entero

                // Verificar si el ID ingresado existe en la lista
                boolean exists = false;
                for (Pais pais : paises) {
                    if (pais.getIdPais() == idPais) {
                        exists = true;
                        break; // Salir del bucle si se encuentra
                    }
                }

                if (!exists) {
                    System.out.println("El ID ingresado no existe. Por favor, ingrese un ID válido.");
                    idPais = -1; // Reiniciar el valor si no existe
                }
            } else {
                System.out.println("Entrada inválida. Por favor, ingrese un número válido.");
            }
        }

        // Listar los tipos de documentos disponibles con formato "ID. Nombre"
        System.out.println("\nTipos de documento disponibles:");
        List<TipoDocumento> tiposDocumento = tipoDocumentoDAO.getAllTipoDocumentos();
        for (TipoDocumento tipoDocumento : tiposDocumento) {
            System.out.println(tipoDocumento.getIdTipoDocumento() + ". " + tipoDocumento.getNombre());
        }

        int idTipoDocumento = -1; // Inicializar con un valor no válido
        // Mientras el ID no sea válido
        while (idTipoDocumento < 0) {
            System.out.print("Seleccione el número del tipo de documento: ");
            String input = scanner.nextLine(); // Leer como String

            // Verificar si la entrada es numérica
            if (input.matches("\\d+")) {
                idTipoDocumento = Integer.parseInt(input); // Convertir a entero

                // Verificar si el ID ingresado existe en la lista
                boolean exists = false;
                for (TipoDocumento tipo : tiposDocumento) {
                    if (tipo.getIdTipoDocumento() == idTipoDocumento) {
                        exists = true;
                        break; // Salir del bucle si se encuentra
                    }
                }

                if (!exists) {
                    System.out.println("El ID ingresado no existe. Por favor, ingrese un ID válido.");
                    idTipoDocumento = -1; // Reiniciar el valor si no existe
                }
            } else {
                System.out.println("Entrada inválida. Por favor, ingrese un número válido.");
            }
        }

        System.out.print("Ingrese el número de documento (dejar vacío para no cambiar): ");
        String numDocumento = scanner.nextLine();

        // Si el número de documento se cambió, verificar si ya existe en la base de datos
        if (!numDocumento.isEmpty() && huespedDAO.existsByNumDocumento(numDocumento)) {
            System.out.println("El número de documento ya está registrado. No se puede actualizar el huésped.");
            return; // Salir sin continuar el proceso
        }

        // Continuar con el resto de los campos (teléfono, fecha de nacimiento, etc.)
        // Guardar el huésped actualizado
        huespedDAO.updateHuesped(huesped);
    }


    public void deleteHuesped() {
        // Mostrar todos los huéspedes
        List<Huesped> huespedes = huespedDAO.getAllHuespedes();

        if (huespedes.isEmpty()) {
            System.out.println("No hay huéspedes registrados.");
            return;
        }

        System.out.println("Lista de huéspedes:");
        for (Huesped huesped : huespedes) {
            System.out.println("ID: " + huesped.getIdHuesped() + ", Nombre: " + huesped.getNombre() + " "
                    + huesped.getApaterno() + " " + huesped.getAmaterno());
        }

        // Solicitar ID del huésped a eliminar
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ID del huésped a eliminar: ");
        int idHuesped = scanner.nextInt();

        // Verificar si el huésped existe
        Huesped huesped = huespedes.stream()
                .filter(h -> h.getIdHuesped() == idHuesped)
                .findFirst()
                .orElse(null);

        if (huesped == null) {
            System.out.println("Huésped no encontrado.");
            return;
        }

        // Confirmación para eliminar
        System.out.print("¿Está seguro que desea eliminar al huésped " + huesped.getNombre() + " (sí/no)? ");
        scanner.nextLine();  // Limpiar el buffer
        String respuesta = scanner.nextLine();

        if (respuesta.equalsIgnoreCase("si")) {
            // Eliminar el huésped
            if (huespedDAO.deleteHuesped(idHuesped)) {
                System.out.println("Huésped eliminado con éxito.");
            } else {
                System.out.println("Error al eliminar el huésped.");
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }

}
