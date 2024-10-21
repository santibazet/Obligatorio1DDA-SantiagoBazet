package org.example.model;

import java.util.Date;

public class Huesped {

    private int idHuesped;
    private Pais pais;
    private TipoDocumento tipoDocumento;
    private String nombre;
    private String apaterno;
    private String amaterno;
    private String numDocumento;
    private Date fechaNacimiento;
    private String telefono;

    public Huesped () {}

    public Huesped (int idHuesped)
    {
        this.idHuesped = idHuesped;
    }
    // Constructor
    public Huesped(int idHuesped, Pais pais, TipoDocumento tipoDocumento, String nombre, String apaterno, String amaterno, String numDocumento, Date fechaNacimiento, String telefono) {
        this.idHuesped = idHuesped;
        this.pais = pais;
        this.tipoDocumento = tipoDocumento;
        this.nombre = nombre;
        this.apaterno = apaterno;
        this.amaterno = amaterno;
        this.numDocumento = numDocumento;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
    }

    // Para crearlo sin id ya que es autogenerado.
    public Huesped(Pais pais, TipoDocumento tipoDocumento, String nombre, String apaterno, String amaterno, String numDocumento, Date fechaNacimiento, String telefono) {
        this.pais = pais;
        this.tipoDocumento = tipoDocumento;
        this.nombre = nombre;
        this.apaterno = apaterno;
        this.amaterno = amaterno;
        this.numDocumento = numDocumento;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
    }


    // Getters y Setters
    public int getIdHuesped() { return idHuesped; }
    public void setIdHuesped(int idHuesped) { this.idHuesped = idHuesped; }

    public Pais getPais() { return pais; }
    public void setPais(Pais pais) { this.pais = pais; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApaterno() { return apaterno; }
    public void setApaterno(String apaterno) { this.apaterno = apaterno; }

    public String getAmaterno() { return amaterno; }
    public void setAmaterno(String amaterno) { this.amaterno = amaterno; }

    public String getNumDocumento() { return numDocumento; }
    public void setNumDocumento(String numDocumento) { this.numDocumento = numDocumento; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public int getIdPais()
    {
        return getPais().getIdPais();
    }

    public int getIdTipoDocumento() {
        return getTipoDocumento().getIdTipoDocumento();
    }



    @Override
    public String toString() {
        return "-----------------------------------------\n" +
                idHuesped + ". " + nombre + " " + apaterno + " " + amaterno + "\n" +
                "Pais: " + pais.getNombre() + "\n" +
                tipoDocumento.getNombre() + ": " + numDocumento + "\n" +
                "Fecha nacimiento: " + fechaNacimiento + "\n" +
                "Teléfono: " + telefono + "\n" +
                "-----------------------------------------";
    }

}

