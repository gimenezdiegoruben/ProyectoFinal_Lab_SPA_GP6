package Modelos;

import java.time.LocalDate;

public class Empleado {

    private int idEmpleado = -1;
    private int dni;
    private String puesto;
    private String apellido;
    private String nombre;
    private LocalDate fechaNacimiento;
    private boolean estado;

    //nuevos atrib de modif de bd
    private String telefono;
    private String matricula;       //Usaremos 0 o -1 para representar NULL en la BD
    private String especialidad;
    private String usuario;
    private String pass;

    public Empleado() {
    }

    //Constructor para ALTAS (sin idEmpleado)
    public Empleado(int dni, String puesto, String apellido, String nombre, String telefono,
            LocalDate fechaNacimiento, String matricula, String especialidad, boolean estado) {
        this.dni = dni;
        this.puesto = puesto;
        this.apellido = apellido;
        this.nombre = nombre;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.matricula = matricula;
        this.especialidad = especialidad;
        this.estado = estado;
    }

    // Constructor COMPLETO (con idEmpleado, para búsquedas/modificaciones)
    public Empleado(int idEmpleado, int dni, String puesto, String apellido, String nombre, String telefono, LocalDate fechaNacimiento, String matricula, String especialidad, boolean estado) {
        this.idEmpleado = idEmpleado;
        this.dni = dni;
        this.puesto = puesto;
        this.apellido = apellido;
        this.nombre = nombre;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.matricula = matricula;
        this.especialidad = especialidad;
        this.estado = estado;
    }

    // Getters y Setters
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return dni + " - " + apellido + ", " + nombre;
    }
}
