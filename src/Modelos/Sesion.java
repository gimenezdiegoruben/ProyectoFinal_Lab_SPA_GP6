package Modelos;

import java.time.LocalDateTime;

public class Sesion {

    private int codSesion;
    private Empleado masajista;      //lo/la Identificamos por matrícula
    private Empleado registrador;    //identif por id
    private Consultorio consultorio;
    private Tratamiento tratamiento;
    private Instalacion instalacion;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFinal;
    private int codPack;
    private double monto;
    private String notas;
    private boolean estado;

    public Sesion() {
    }

    //Contr p crear nuevas sesiones sin codSesion
    public Sesion(Empleado masajista, Empleado registrador, Consultorio consultorio, Tratamiento tratamiento, Instalacion instalacion, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFinal, int codPack) {
        this.masajista = masajista;
        this.registrador = registrador;
        this.consultorio = consultorio;
        this.tratamiento = tratamiento;
        this.instalacion = instalacion;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFinal = fechaHoraFinal;
        this.codPack = codPack;
        this.estado = true; //sesión activa x def
        this.monto = 0;
        this.notas = "";
    }

    //Constructor completo 
    public Sesion(int codSesion, Empleado masajista, Empleado registrador, Consultorio consultorio, Tratamiento tratamiento, Instalacion instalacion, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFinal, int codPack, boolean estado) {
        this.codSesion = codSesion;
        this.masajista = masajista;
        this.registrador = registrador;
        this.consultorio = consultorio;
        this.tratamiento = tratamiento;
        this.instalacion = instalacion;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFinal = fechaHoraFinal;
        this.codPack = codPack;
        this.estado = estado;
    }

    public int getCodSesion() {
        return codSesion;
    }

    public void setCodSesion(int codSesion) {
        this.codSesion = codSesion;
    }

    public Empleado getMasajista() {
        return masajista;
    }

    public void setMasajista(Empleado masajista) {
        this.masajista = masajista;
    }

    public Empleado getRegistrador() {
        return registrador;
    }

    public void setRegistrador(Empleado registrador) {
        this.registrador = registrador;
    }

    public Consultorio getConsultorio() {
        return consultorio;
    }

    public void setConsultorio(Consultorio consultorio) {
        this.consultorio = consultorio;
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }

    public Instalacion getInstalacion() {
        return instalacion;
    }

    public void setInstalacion(Instalacion instalacion) {
        this.instalacion = instalacion;
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

    public int getCodPack() {
        return codPack;
    }

    public void setCodPack(int codPack) {
        this.codPack = codPack;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public boolean isActiva() {
        return estado;
    }

    public void anular() {
        this.estado = false;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    //Mét para calcular la duración en minutos
    public long getDuracionMinutos() {
        if (fechaHoraInicio != null && fechaHoraFinal != null) {
            return java.time.Duration.between(fechaHoraInicio, fechaHoraFinal).toMinutes();
        }
        return 0;
    }

    @Override
    public String toString() {
        String masajistaInfo = (masajista != null)
                ? masajista.getNombre() + " " + masajista.getApellido() + " (" + masajista.getMatricula() + ")"
                : "No asignado";

        String tratamientoInfo = (tratamiento != null)
                ? tratamiento.getNombre() : "No asignado";

        return "Sesión #" + codSesion
                + " - " + tratamientoInfo
                + " - Masajista: " + masajistaInfo
                + " - " + fechaHoraInicio.toLocalDate()
                + " " + fechaHoraInicio.toLocalTime()
                + (estado ? " (Activa)" : " (Anulada)");
    }

    //Met p información detallada
    public String toDetailedString() {
        return "Sesion{"
                + "codSesion=" + codSesion
                + ", masajista=" + (masajista != null ? masajista.getNombre() + " " + masajista.getApellido() : "N/A")
                + ", registrador=" + (registrador != null ? registrador.getNombre() + " " + registrador.getApellido() : "N/A")
                + ", consultorio=" + (consultorio != null ? consultorio.getNroConsultorio() : "N/A")
                + ", tratamiento=" + (tratamiento != null ? tratamiento.getNombre() : "N/A")
                + ", instalacion=" + (instalacion != null ? instalacion.getNombre() : "N/A")
                + ", fechaHoraInicio=" + fechaHoraInicio
                + ", fechaHoraFinal=" + fechaHoraFinal
                + ", codPack=" + codPack
                + ", estado=" + estado
                + '}';
    }
}
