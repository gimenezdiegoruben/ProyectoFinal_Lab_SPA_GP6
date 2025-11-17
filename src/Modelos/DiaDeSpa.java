package Modelos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */

public class DiaDeSpa {
    
    private int codPack = -1;
    private LocalDateTime fechayhora;
    private String preferencias;
    private Cliente cliente;
    private List<Sesion> sesiones;
    private double monto;
    private boolean estado;

    public DiaDeSpa() {
    }

    public DiaDeSpa(LocalDateTime fechayhora, String preferencias, Cliente cliente, List<Sesion> sesiones, boolean estado) {
        this.fechayhora = fechayhora;
        this.preferencias = preferencias;
        this.cliente = cliente;
        this.sesiones = sesiones;
        this.estado = estado;
    }

    public int getCodPack() {
        return codPack;
    }

    public void setCodPack(int codPack) {
        this.codPack = codPack;
    }

    public LocalDateTime getFechayhora() {
        return fechayhora;
    }

    public void setFechayhora(LocalDateTime fechayhora) {
        this.fechayhora = fechayhora;
    }

    public String getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(String preferencias) {
        this.preferencias = preferencias;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Sesion> getSesiones() {
        return sesiones;
    }

    public void setSesiones(List<Sesion> sesiones) {
        this.sesiones = sesiones;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    
    
}
