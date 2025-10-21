/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

/**
 *
 * @author Ger
 */
public class Tratamiento {
    private int codTratam = -1;
    private String nombre;
    private String tipo;
    private String detalle;
    private int productos;
    private int duracion;
    private int costo;
    private boolean estado;

    public Tratamiento() {
    }

    public Tratamiento(String nombre,String tipo, String detalle, int productos, int duracion, int costo, boolean estado) {
        this.nombre = nombre;
        this.tipo=tipo;
        this.detalle = detalle;
        this.productos = productos;
        this.duracion = duracion;
        this.costo = costo;
        this.estado = estado;
    }

    public int getCodTratam() {
        return codTratam;
    }

    public void setCodTratam(int codTratam) {
        this.codTratam = codTratam;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    
    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public int getProductos() {
        return productos;
    }

    public void setProductos(int productos) {
        this.productos = productos;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public int getCosto() {
        return costo;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Tratamiento{" + "codTratam=" + codTratam + ", nombre=" + nombre + ", detalle=" + detalle + ", productos=" + productos + ", duracion=" + duracion + ", costo=" + costo + ", estado=" + estado + '}';
    }
    
    
}
