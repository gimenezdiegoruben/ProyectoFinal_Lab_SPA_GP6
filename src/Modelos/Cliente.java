package Modelos;

import java.time.LocalDate;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */

public class Cliente {
    
    private int codCli = -1;
    private long dni;
    private String nombre;
    private long telefono;
    private int edad;
    private String afecciones;
    private boolean estado;
    private LocalDate fechaNac;

    public Cliente() {
    }

    public Cliente(long dni, String nombre, long telefono, int edad, String afecciones, boolean estado, LocalDate fechaNac) {
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.edad = edad;
        this.afecciones = afecciones;
        this.estado = estado;
        this.fechaNac = fechaNac;
    }

    public Cliente(long dni, String nombre, long telefono, int edad, String afecciones, boolean estado) {
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.edad = edad;
        this.afecciones = afecciones;
        this.estado = estado;
    }
    
    

    public int getCodCli() {
        return codCli;
    }

    public void setCodCli(int codCli) {
        this.codCli = codCli;
    }

    public long getDni() {
        return dni;
    }

    public void setDni(long dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getTelefono() {
        return telefono;
    }

    public void setTelefono(long telefono) {
        this.telefono = telefono;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getAfecciones() {
        return afecciones;
    }

    public void setAfecciones(String afecciones) {
        this.afecciones = afecciones;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public LocalDate getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(LocalDate fechaNac) {
        this.fechaNac = fechaNac;
    }

    @Override
    public String toString() {
        return "Cliente{" + "codCli=" + codCli + ", dni=" + dni + ", nombre=" + nombre + ", telefono=" + telefono + ", edad=" + edad + ", afecciones=" + afecciones + ", estado=" + estado + '}';
    }
    
    

}
