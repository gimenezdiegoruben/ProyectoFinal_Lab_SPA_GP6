/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

import java.time.LocalDate;

/**
 *
 * @author Ger
 */
public class DiaDeSpa {
    
    private int codPack = -1;
    private LocalDate fechayhora;
    private String preferencias;
    private int codCli;
    private int sesiones;
    private double monto;
    private boolean estado;

    public DiaDeSpa() {
    }

    public DiaDeSpa(LocalDate fechayhora, String preferencias, int codCli, int sesiones, double monto, boolean estado) {
        this.fechayhora = fechayhora;
        this.preferencias = preferencias;
        this.codCli = codCli;
        this.sesiones = sesiones;
        this.monto = monto;
        this.estado = estado;
    }

    public int getCodPack() {
        return codPack;
    }

    public void setCodPack(int codPack) {
        this.codPack = codPack;
    }

    public LocalDate getFechayhora() {
        return fechayhora;
    }

    public void setFechayhora(LocalDate fechayhora) {
        this.fechayhora = fechayhora;
    }

    public String getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(String preferencias) {
        this.preferencias = preferencias;
    }

    public int getCodCli() {
        return codCli;
    }

    public void setCodCli(int codCli) {
        this.codCli = codCli;
    }

    public int getSesiones() {
        return sesiones;
    }

    public void setSesiones(int sesiones) {
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
