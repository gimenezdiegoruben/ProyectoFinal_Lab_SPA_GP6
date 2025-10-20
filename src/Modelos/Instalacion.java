/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

/**
 *
 * @author Ger
 */
public class Instalacion {
    private int codInstal;
    private String nombre;
    private String detalleUso;
    private double precio;
    private boolean estado;

    public Instalacion() {
    }

    public Instalacion(int codInstal, String nombre, String detalleUso, double precio, boolean estado) {
        this.codInstal = codInstal;
        this.nombre = nombre;
        this.detalleUso = detalleUso;
        this.precio = precio;
        this.estado = estado;
    }

    public int getCodInstal() {
        return codInstal;
    }

    public void setCodInstal(int codInstal) {
        this.codInstal = codInstal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDetalleUso() {
        return detalleUso;
    }

    public void setDetalleUso(String detalleUso) {
        this.detalleUso = detalleUso;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Instalacion{" + "codInstal=" + codInstal + ", nombre=" + nombre + ", detalleUso=" + detalleUso + ", precio=" + precio + ", estado=" + estado + '}';
    }
    
    
}
