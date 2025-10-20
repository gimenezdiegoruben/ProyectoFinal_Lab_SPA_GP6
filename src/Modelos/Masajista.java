/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

/**
 *
 * @author Ger
 */
public class Masajista {
    int matricula;
    String nombre;
    int telefono;
    String especialidad;
    boolean estado;

    public Masajista() {
    }

    public Masajista(int matricula, String nombre, int telefono, String especialidad, boolean estado) {
        this.matricula = matricula;
        this.nombre = nombre;
        this.telefono = telefono;
        this.especialidad = especialidad;
        this.estado = estado;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Masajista{" + "matricula=" + matricula + ", nombre=" + nombre + ", telefono=" + telefono + ", especialidad=" + especialidad + ", estado=" + estado + '}';
    }
    
    
}
