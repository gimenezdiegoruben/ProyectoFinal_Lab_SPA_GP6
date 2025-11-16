package Modelos;

import java.util.ArrayList;
import java.util.List;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */

public class Tratamiento {
    private int codTratam = -1;
    private String nombre;
    private String tipo;
    private String detalle;
    private List<String> productos;
    private int duracion;
    private double costo;
    private boolean estado;

    public Tratamiento() {
        this.productos = new ArrayList<>();
    }

    public Tratamiento(String nombre,String tipo, String detalle, List<String> productos, int duracion, double costo, boolean estado) {
        this.nombre = nombre;
        this.tipo=tipo;
        this.detalle = detalle;
        this.productos = (productos != null) ? productos : new ArrayList<>();
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

    public List<String> getProductos() {
        if (productos == null) {
            productos = new ArrayList<>();//Nunca devolvemos null, si está null, devolvemos lista vacía
        }
        return productos;
    }

    public void setProductos(List<String> productos) {
       this.productos = (productos != null) ? productos : new ArrayList<>();//evitamos null
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    /**
     * Devuelve los productos como un único String
     * listo para guardar en la BD: "prod1, prod2, prod3".
     * Se usa desde TratamientoData.
     */
    public String getProductosComoTexto() {
        if (productos == null || productos.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < productos.size(); i++) {
            sb.append(productos.get(i).trim());
            if (i < productos.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /*Reconstruye la lista de productos a partir del texto de la BD.
      Ej: "Crema, Aceite" a ["Crema", "Aceite"] */
    public void setProductosDesdeTexto(String texto) {
        this.productos = new ArrayList<>();
        if (texto == null || texto.trim().isEmpty()) {
            return;
        }
        String[] partes = texto.split(",");
        for (String p : partes) {
            String prod = p.trim();
            if (!prod.isEmpty()) {
                this.productos.add(prod);
            }
        }
    }

    @Override
    public String toString() {
        return "Tratamiento{"+"codTratam=" + codTratam + ", nombre=" + nombre + ", tipo=" + tipo + ", detalle=" + detalle + ", productos=" + getProductosComoTexto() + ", duracion=" + duracion + ", costo=" + costo + ", estado=" + estado + '}';
    }
}