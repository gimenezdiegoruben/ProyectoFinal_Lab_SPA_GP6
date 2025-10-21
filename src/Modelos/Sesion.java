/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

import java.time.*;

/**
 *
 * @author Ger
 */
public class Sesion {
    private int codSesion = -1;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFinal;
    private int codTtatam;
    private int nroConsultorio;
    private int matricula;
    private int codInstal;
    private int codPack;
    private LocalDateTime estado;

    public Sesion() {
    }

    public Sesion(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFinal, int codTtatam, int nroConsultorio, int matricula, int codInstal, int codPack, LocalDateTime estado) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFinal = fechaHoraFinal;
        this.codTtatam = codTtatam;
        this.nroConsultorio = nroConsultorio;
        this.matricula = matricula;
        this.codInstal = codInstal;
        this.codPack = codPack;
        this.estado = estado;
    }

    public int getCodSesion() {
        return codSesion;
    }

    public void setCodSesion(int codSesion) {
        this.codSesion = codSesion;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFinal() {
        return fechaHoraFinal;
    }

    public void setFechaHoraFinal(LocalDateTime fechaHoraFinal) {
        this.fechaHoraFinal = fechaHoraFinal;
    }

    public int getCodTtatam() {
        return codTtatam;
    }

    public void setCodTtatam(int codTtatam) {
        this.codTtatam = codTtatam;
    }

    public int getNroConsultorio() {
        return nroConsultorio;
    }

    public void setNroConsultorio(int nroConsultorio) {
        this.nroConsultorio = nroConsultorio;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public int getCodInstal() {
        return codInstal;
    }

    public void setCodInstal(int codInstal) {
        this.codInstal = codInstal;
    }

    public int getCodPack() {
        return codPack;
    }

    public void setCodPack(int codPack) {
        this.codPack = codPack;
    }

    public LocalDateTime getEstado() {
        return estado;
    }

    public void setEstado(LocalDateTime estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Sesion{" + "codSesion=" + codSesion + ", fechaHoraInicio=" + fechaHoraInicio + ", fechaHoraFinal=" + fechaHoraFinal + ", codTtatam=" + codTtatam + ", nroConsultorio=" + nroConsultorio + ", matricula=" + matricula + ", codInstal=" + codInstal + ", codPack=" + codPack + ", estado=" + estado + '}';
    }
    
    
}
