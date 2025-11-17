package Modelos;
/** 
    @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
**/
public class Producto {
    private int codProducto = -1;
    private String descripcion;
    private double precio;
    private int stock;
    private Tratamiento tratamiento;

    public Producto(String descripcion, double precio, int stock, Tratamiento tratamiento) {
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.tratamiento = tratamiento;
    }

    public Producto() {
        
    }

    public int getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(int codProducto) {
        this.codProducto = codProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }
    
    
}
