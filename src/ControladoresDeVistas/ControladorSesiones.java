package ControladoresDeVistas;

import Modelos.Sesion;
import Modelos.Empleado;
import Modelos.Consultorio;
import Modelos.Tratamiento;
import Modelos.Instalacion;
import Modelos.Cliente;
import Modelos.DiaDeSpa;
import Persistencias_Conexion.SesionData;
import Persistencias_Conexion.EmpleadoData;
import Persistencias_Conexion.ConsultorioData;
import Persistencias_Conexion.TratamientoData;
import Persistencias_Conexion.InstalacionData;
import Persistencias_Conexion.ClienteData;
import Persistencias_Conexion.DiaDeSpaData;
import Vistas.Vista_MenuSpa;
import Vistas.VistaSesiones;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * @author Grupo 6 
 * Gimenez Diego Ruben 
 * Carlos German Mecias 
 * Giacomelli Tomas
 * Migliozzi Badani Urbani Jose
 */

public class ControladorSesiones implements ActionListener {

    private final VistaSesiones vista;
    private final SesionData sesionData;
    private final EmpleadoData empleadoData;
    private final ConsultorioData consultorioData;
    private final TratamientoData tratamientoData;
    private final InstalacionData instalacionData;
    private final ClienteData clienteData;
    private final DiaDeSpaData diaDeSpaData;
    private final Vista_MenuSpa menu;

    //Variables para guardar selecciones
    private Tratamiento tratamientoSeleccionado;
    private Empleado masajistaSeleccionado;
    private Consultorio consultorioSeleccionado;
    private Instalacion instalacionSeleccionado;
    private Cliente clienteSeleccionado;

    public ControladorSesiones(VistaSesiones vista, SesionData sesionData, EmpleadoData empleadoData,
            ConsultorioData consultorioData, TratamientoData tratamientoData, InstalacionData instalacionData,
            ClienteData clienteData, DiaDeSpaData diaDeSpaData, Vista_MenuSpa menu) {

        this.vista = vista;
        this.sesionData = sesionData;
        this.empleadoData = empleadoData;
        this.consultorioData = consultorioData;
        this.tratamientoData = tratamientoData;
        this.instalacionData = instalacionData;
        this.clienteData = clienteData;
        this.diaDeSpaData = diaDeSpaData;
        this.menu = menu;

        this.vista.jButton_Agregar.addActionListener(this);
        this.vista.jButton_LimpiarCampos.addActionListener(this);
        this.vista.jButton_Salir.addActionListener(this);

        this.vista.jCmb_TipoTratamiento.addActionListener(this);
        this.vista.jCmb_Especialidad.addActionListener(this);
        this.vista.jCmb_Tratamiento.addActionListener(this);
        this.vista.jCmb_Profesional.addActionListener(this);
        this.vista.jCmb_Instalacion.addActionListener(this);
        this.vista.jCmb_Consultorio.addActionListener(this);
    }

    public void iniciar() {
        this.menu.JDesktopPFondo.add(this.vista);
        this.vista.setVisible(true);
        this.menu.JDesktopPFondo.moveToFront(this.vista);

        cargarCombos();
        configurarFechaHoraPorDefecto();
        limpiarCampos();
    }

    private void cargarCombos() {
        cargarEspecialidades();
        cargarTratamientos();
        cargarProfesionales();
        cargarInstalaciones();
        cargarConsultorios();
    }

    private void cargarEspecialidades() {
        this.vista.jCmb_Especialidad.removeAllItems();
        this.vista.jCmb_Especialidad.addItem("Todas");

        List<String> especialidades = this.empleadoData.obtenerEspecialidades();
        for (String especialidad : especialidades) {
            if (especialidad != null && !especialidad.isEmpty()) {
                this.vista.jCmb_Especialidad.addItem(especialidad);
            }
        }
    }

    private void cargarTratamientos() {
        this.vista.jCmb_Tratamiento.removeAllItems();
        String tipoSeleccionado = (String) this.vista.jCmb_TipoTratamiento.getSelectedItem();
        List<Tratamiento> tratamientos = this.tratamientoData.listarTratamientos();

        boolean tratamientosCargados = false;

        for (Tratamiento tratamiento : tratamientos) {
            if (tipoSeleccionado == null || tipoSeleccionado.equals("Todas")
                    || tratamiento.getTipo().equalsIgnoreCase(tipoSeleccionado)) {
                this.vista.jCmb_Tratamiento.addItem(tratamiento.getNombre() + " - $" + tratamiento.getCosto());
                tratamientosCargados = true;
            }
        }

        if (!tratamientosCargados) {
            this.vista.jCmb_Tratamiento.addItem("Ninguno");//Añadimos "Ninguno" si la lista filtrada o total está vacía(ya que controlador y vista tratamientos aún no está hecho)
        }

        if (this.vista.jCmb_Tratamiento.getItemCount() > 0) {
            this.vista.jCmb_Tratamiento.setSelectedIndex(0);
            actualizarTratamientoSeleccionado();
        }
    }

    private void cargarProfesionales() {
        this.vista.jCmb_Profesional.removeAllItems();
        String especialidadSeleccionada = (String) this.vista.jCmb_Especialidad.getSelectedItem();

        List<Empleado> masajistas;
        if (especialidadSeleccionada == null || especialidadSeleccionada.equals("Todas")) {
            masajistas = this.empleadoData.listarEmpleados(true, "Todas");
        } else {
            masajistas = this.empleadoData.listarEmpleados(true, especialidadSeleccionada);
        }

        for (Empleado masajista : masajistas) {
            if (masajista.getMatricula() != null && !masajista.getMatricula().isEmpty()) {
                this.vista.jCmb_Profesional.addItem(masajista.getNombre() + " " + masajista.getApellido()
                        + " (" + masajista.getMatricula() + ")");
            }
        }

        if (this.vista.jCmb_Profesional.getItemCount() > 0) {
            this.vista.jCmb_Profesional.setSelectedIndex(0);
            actualizarMasajistaSeleccionado();
        }
    }

    private void cargarInstalaciones() {
        this.vista.jCmb_Instalacion.removeAllItems();
        this.vista.jCmb_Instalacion.addItem("Ninguna");

        List<Instalacion> instalaciones = this.instalacionData.listarTodasInstalacionesActivas();
        for (Instalacion instalacion : instalaciones) {
            this.vista.jCmb_Instalacion.addItem(instalacion.getNombre() + " - $" + instalacion.getPrecio() + "/30min");
        }

        this.vista.jCmb_Instalacion.setSelectedIndex(0);
        actualizarInstalacionSeleccionada();
    }

    private void cargarConsultorios() {
        this.vista.jCmb_Consultorio.removeAllItems();

        List<Consultorio> consultorios = this.consultorioData.listarConsultorios();
        for (Consultorio consultorio : consultorios) {
            this.vista.jCmb_Consultorio.addItem("Consultorio " + consultorio.getNroConsultorio()
                    + " - " + consultorio.getApto());
        }

        if (this.vista.jCmb_Consultorio.getItemCount() > 0) {
            this.vista.jCmb_Consultorio.setSelectedIndex(0);
            actualizarConsultorioSeleccionado();
        }
    }

    private void configurarFechaHoraPorDefecto() {
        this.vista.jDC_Fecha.setDate(new java.util.Date());

        LocalTime ahora = LocalTime.now();
        String horaSeleccionada = "08:00";

        for (int i = 0; i < this.vista.jCmb_Hora.getItemCount(); i++) {
            String hora = this.vista.jCmb_Hora.getItemAt(i);
            LocalTime horaCombo = LocalTime.parse(hora);
            if (horaCombo.isAfter(ahora) || horaCombo.equals(ahora)) {
                horaSeleccionada = hora;
                break;
            }
        }

        this.vista.jCmb_Hora.setSelectedItem(horaSeleccionada);
    }

    private void actualizarTratamientoSeleccionado() {
        String seleccion = (String) this.vista.jCmb_Tratamiento.getSelectedItem();
        if (seleccion != null) {
            String nombreTratamiento = seleccion.split(" - ")[0];
            List<Tratamiento> tratamientos = this.tratamientoData.listarTratamientos();
            for (Tratamiento tratamiento : tratamientos) {
                if (tratamiento.getNombre().equals(nombreTratamiento)) {
                    this.tratamientoSeleccionado = tratamiento;
                    actualizarMonto();
                    return;
                }
            }
        }
        this.tratamientoSeleccionado = null;
    }

    private void actualizarMasajistaSeleccionado() {
        String seleccion = (String) this.vista.jCmb_Profesional.getSelectedItem();
        if (seleccion != null) {
            String matricula = seleccion.substring(seleccion.lastIndexOf("(") + 1, seleccion.lastIndexOf(")"));
            this.masajistaSeleccionado = this.empleadoData.buscarEmpleadoPorMatricula(matricula);
        } else {
            this.masajistaSeleccionado = null;
        }
    }

    private void actualizarConsultorioSeleccionado() {
        String seleccion = (String) this.vista.jCmb_Consultorio.getSelectedItem();
        if (seleccion != null) {
            int nroConsultorio = Integer.parseInt(seleccion.split(" ")[1].split(" -")[0]);
            this.consultorioSeleccionado = this.consultorioData.buscarConsultorio(nroConsultorio);
        } else {
            this.consultorioSeleccionado = null;
        }
    }

    private void actualizarInstalacionSeleccionada() {
        String seleccion = (String) this.vista.jCmb_Instalacion.getSelectedItem();
        if (seleccion != null && !seleccion.equals("Ninguna")) {
            String nombreInstalacion = seleccion.split(" - ")[0];
            List<Instalacion> instalaciones = this.instalacionData.listarTodasInstalacionesActivas();
            for (Instalacion instalacion : instalaciones) {
                if (instalacion.getNombre().equals(nombreInstalacion)) {
                    this.instalacionSeleccionado = instalacion;
                    actualizarMonto();
                    return;
                }
            }
        }
        this.instalacionSeleccionado = null;
        actualizarMonto();
    }

    private void actualizarMonto() {
        double monto = 0.0;

        if (this.tratamientoSeleccionado != null) {
            monto += this.tratamientoSeleccionado.getCosto();
        }

        if (this.instalacionSeleccionado != null) {
            monto += this.instalacionSeleccionado.getPrecio();
        }

        this.vista.jTextField_MontoSesion.setText(String.format("%.2f", monto));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.vista.jButton_Agregar) {
            agregarSesion();
        } else if (e.getSource() == this.vista.jButton_LimpiarCampos) {
            limpiarCampos();
        } else if (e.getSource() == this.vista.jButton_Salir) {
            this.vista.dispose();
        } else if (e.getSource() == this.vista.jCmb_TipoTratamiento) {
            cargarTratamientos();
        } else if (e.getSource() == this.vista.jCmb_Especialidad) {
            cargarProfesionales();
        } else if (e.getSource() == this.vista.jCmb_Tratamiento) {
            actualizarTratamientoSeleccionado();
        } else if (e.getSource() == this.vista.jCmb_Profesional) {
            actualizarMasajistaSeleccionado();
        } else if (e.getSource() == this.vista.jCmb_Instalacion) {
            actualizarInstalacionSeleccionada();
        } else if (e.getSource() == this.vista.jCmb_Consultorio) {
            actualizarConsultorioSeleccionado();
        }
    }

    private void agregarSesion() {
        if (!validarCampos()) {
            return;
        }

        try {
            LocalDate fecha = this.vista.jDC_Fecha.getDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            String horaStr = (String) this.vista.jCmb_Hora.getSelectedItem();
            LocalTime hora = LocalTime.parse(horaStr);

            LocalDateTime fechaHoraInicio = LocalDateTime.of(fecha, hora);

            LocalDateTime fechaHoraFinal = fechaHoraInicio;
            if (this.tratamientoSeleccionado != null) {
                fechaHoraFinal = fechaHoraInicio.plusMinutes((long) this.tratamientoSeleccionado.getDuracion());
            }

            if (!verificarDisponibilidad(fechaHoraInicio, fechaHoraFinal)) {
                JOptionPane.showMessageDialog(this.vista,
                        "No hay disponibilidad en el horario seleccionado. Por favor, elija otro horario.",
                        "No Disponible", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Empleado registrador = obtenerRegistrador();
            if (registrador == null) {
                JOptionPane.showMessageDialog(this.vista,
                        "No se encontró un recepcionista disponible para registrar la sesión.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Sesion sesion = new Sesion(this.masajistaSeleccionado, registrador, this.consultorioSeleccionado,
                    this.tratamientoSeleccionado, this.instalacionSeleccionado,
                    fechaHoraInicio, fechaHoraFinal, 0);

            this.sesionData.crearSesion(sesion);

            JOptionPane.showMessageDialog(this.vista,
                    "Sesión creada exitosamente.\n"
                    + "Fecha: " + fechaHoraInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n"
                    + "Profesional: " + this.masajistaSeleccionado.getNombre() + " " + this.masajistaSeleccionado.getApellido() + "\n"
                    + "Tratamiento: " + this.tratamientoSeleccionado.getNombre() + "\n"
                    + "Monto: $" + this.vista.jTextField_MontoSesion.getText(),
                    "Sesión Creada", JOptionPane.INFORMATION_MESSAGE);

            limpiarCampos();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this.vista,
                    "Error al crear la sesión: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        if (this.vista.jDC_Fecha.getDate() == null) {
            JOptionPane.showMessageDialog(this.vista, "Seleccione una fecha válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (this.vista.jCmb_Hora.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this.vista, "Seleccione una hora válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (this.tratamientoSeleccionado == null) {
            JOptionPane.showMessageDialog(this.vista, "Seleccione un tratamiento válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (this.masajistaSeleccionado == null) {
            JOptionPane.showMessageDialog(this.vista, "Seleccione un profesional válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (this.consultorioSeleccionado == null) {
            JOptionPane.showMessageDialog(this.vista, "Seleccione un consultorio válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        LocalDate fechaSeleccionada = this.vista.jDC_Fecha.getDate().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        if (fechaSeleccionada.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this.vista, "No puede seleccionar una fecha en el pasado.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean verificarDisponibilidad(LocalDateTime inicio, LocalDateTime fin) {
        List<Empleado> masajistasLibres = this.sesionData.listarMasajistasLibres(inicio, fin);
        boolean masajistaDisponible = false;
        for (Empleado masajista : masajistasLibres) {
            if (masajista.getMatricula().equals(this.masajistaSeleccionado.getMatricula())) {
                masajistaDisponible = true;
                break;
            }
        }

        if (!masajistaDisponible) {
            return false;
        }

        List<Consultorio> consultoriosLibres = this.sesionData.listarConsultoriosLibres(inicio, fin);
        boolean consultorioDisponible = false;
        for (Consultorio consultorio : consultoriosLibres) {
            if (consultorio.getNroConsultorio() == this.consultorioSeleccionado.getNroConsultorio()) {
                consultorioDisponible = true;
                break;
            }
        }

        if (!consultorioDisponible) {
            return false;
        }

        if (this.instalacionSeleccionado != null) {
            List<Instalacion> instalacionesLibres = this.sesionData.listarInstalacionesLibres(inicio, fin);
            boolean instalacionDisponible = false;
            for (Instalacion instalacion : instalacionesLibres) {
                if (instalacion.getCodInstal() == this.instalacionSeleccionado.getCodInstal()) {
                    instalacionDisponible = true;
                    break;
                }
            }
            return instalacionDisponible;
        }

        return true;
    }

    private Empleado obtenerRegistrador() {
        List<Empleado> recepcionistas = this.sesionData.listarRecepcionistas();
        if (!recepcionistas.isEmpty()) {
            return recepcionistas.get(0);
        }
        return null;
    }

    private void limpiarCampos() {
        configurarFechaHoraPorDefecto();
        this.vista.jCmb_TipoTratamiento.setSelectedIndex(0);
        this.vista.jCmb_Especialidad.setSelectedIndex(0);
        this.vista.jCmb_Tratamiento.setSelectedIndex(0);
        this.vista.jCmb_Profesional.setSelectedIndex(0);
        this.vista.jCmb_Instalacion.setSelectedIndex(0);
        this.vista.jCmb_Consultorio.setSelectedIndex(0);
        this.vista.jTextField_MontoSesion.setText("0.00");

        actualizarTratamientoSeleccionado();
        actualizarMasajistaSeleccionado();
        actualizarConsultorioSeleccionado();
        actualizarInstalacionSeleccionada();
    }
}
