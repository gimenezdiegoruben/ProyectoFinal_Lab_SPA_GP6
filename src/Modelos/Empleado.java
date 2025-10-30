
package Modelos;

import java.time.LocalDate;

/**
 * @author G
 * 

 */

public class Empleado {
    // Atributos
    private int idEmpleado = -1;
    private int dni;
    private String puesto;
    private String apellido;
    private String nombre;
    private LocalDate fechaNacimiento;
    private boolean estado;

    public Empleado(){
    }
    
    public Empleado(int dni, String puesto, String apellido, String nombre, LocalDate fechaNacimiento, boolean estado) {
        this.dni = dni;
        this.puesto = puesto;
        this.apellido = apellido;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.estado = estado;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}



