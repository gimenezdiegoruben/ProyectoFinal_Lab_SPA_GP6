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
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
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
    private DiaDeSpa diaActual;

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
        this.vista.jButton_Modificar_Guardar.addActionListener(this);
        this.vista.jButton_Anular_Sesión.addActionListener(this);
        this.vista.jButton_BuscarPack.addActionListener(this);
        vista.btnActualizar.addActionListener(this);

        this.vista.jCmb_TipoTratamiento.addActionListener(this);
        this.vista.jCmb_Especialidad.addActionListener(this);
        this.vista.jCmb_Tratamiento.addActionListener(this);
        this.vista.jCmb_Profesional.addActionListener(this);
        this.vista.jCmb_Instalacion.addActionListener(this);
        this.vista.jCmb_Consultorio.addActionListener(this);

        this.vista.jRadioButtonTodasSes.addActionListener(this);
        this.vista.jRadioButtonSesActivas.addActionListener(this);
        this.vista.jRadioButtonSesInactivas.addActionListener(this);
    }

    public void iniciar() {
        this.menu.JDesktopPFondo.add(this.vista);
        this.vista.setVisible(true);
        this.menu.JDesktopPFondo.moveToFront(this.vista);

        cargarCombos();
        configurarFechaHoraPorDefecto();
        configurarTabla();
        cargarSesionesEnTabla();
        limpiarCampos();

        javax.swing.ButtonGroup grupoEstado = new javax.swing.ButtonGroup();
        grupoEstado.add(vista.jRadioButtonTodasSes);
        grupoEstado.add(vista.jRadioButtonSesActivas);
        grupoEstado.add(vista.jRadioButtonSesInactivas);

        vista.jRadioButtonTodasSes.setSelected(true);
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
            this.vista.jCmb_Tratamiento.addItem(tratamiento.getNombre() + " - $" + tratamiento.getCosto());
            tratamientosCargados = true;
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
            this.vista.jCmb_Instalacion.addItem(
                    instalacion.getNombre() + " - $" + instalacion.getPrecio() + "/30min");
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

        for (int i = 0; i < this.vista.jCmb_HoraInicio.getItemCount(); i++) {
            String hora = this.vista.jCmb_HoraInicio.getItemAt(i);
            LocalTime horaCombo = LocalTime.parse(hora);
            if (horaCombo.isAfter(ahora) || horaCombo.equals(ahora)) {
                horaSeleccionada = hora;
                break;
            }
        }
        this.vista.jCmb_HoraInicio.setSelectedItem(horaSeleccionada);
    }

    private void cargarSesionesEnTabla() {

        DefaultTableModel modelo = (DefaultTableModel) vista.tbSesiones.getModel();
        modelo.setRowCount(0);

        List<Sesion> sesiones = sesionData.listarSesiones();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

        for (Sesion s : sesiones) {

            String fechaHora = s.getFechaHoraInicio() != null
                    ? s.getFechaHoraInicio().format(formatoFecha)
                    : "";

            String horaIni = s.getFechaHoraInicio() != null
                    ? s.getFechaHoraInicio().format(formatoHora)
                    : "";

            String horaFin = s.getFechaHoraFinal() != null
                    ? s.getFechaHoraFinal().format(formatoHora)
                    : "";

            String masajista = "";
            if (s.getMasajista() != null) {
                if (s.getMasajista().getNombre() != null) {
                    masajista = s.getMasajista().getNombre() + " " + s.getMasajista().getApellido();
                } else {
                    masajista = s.getMasajista().getMatricula();
                }
            }

            String tratamiento = s.getTratamiento() != null ? s.getTratamiento().getNombre() : "";
            String tipo = s.getTratamiento() != null ? s.getTratamiento().getTipo() : "";
            String especialidad = s.getMasajista() != null ? s.getMasajista().getEspecialidad() : "";

            String instalacion = (s.getInstalacion() != null)
                    ? s.getInstalacion().getNombre()
                    : "Ninguna";

            String consultorio = (s.getConsultorio() != null)
                    ? "Consultorio " + s.getConsultorio().getNroConsultorio()
                    : "";

            modelo.addRow(new Object[]{
                s.getCodSesion(), // ID
                s.isActiva(), // Estado
                fechaHora, // Fecha/Hora
                masajista, // Profesional
                tratamiento, // Tratamiento
                horaIni, // Hora inicio
                horaFin, // Hora fin
                tipo, // Tipo
                especialidad, // Especialidad
                instalacion, // Instalación
                consultorio, // Consultorio
                String.format("%.2f", s.getMonto()), // Monto
                s.getNotas() != null ? s.getNotas() : "" // Notas
            });
        }
    }

    public void modificarTabla() {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelo.addColumn("Masajista");
        modelo.addColumn("Registrador");
        modelo.addColumn("Consultorio");
        modelo.addColumn("Tratamiento");
        modelo.addColumn("Instalación");
        modelo.addColumn("Hora inicio");
        modelo.addColumn("Hora fin");
        modelo.addColumn("Turno");
        modelo.addColumn("Estado");

        vista.tbSesiones.setModel(modelo);

    }

    private void actualizarTratamientoSeleccionado() {
        String seleccion = (String) this.vista.jCmb_Tratamiento.getSelectedItem();

        if (seleccion != null) {
            String nombreTratamiento = seleccion.split(" - ")[0];

            for (Tratamiento t : this.tratamientoData.listarTratamientos()) {
                if (t.getNombre().equals(nombreTratamiento)) {
                    this.tratamientoSeleccionado = t;
                    actualizarMonto();
                    return;
                }
            }
        }
        this.tratamientoSeleccionado = null;
        actualizarMonto();

    }

    private void actualizarMasajistaSeleccionado() {
        String seleccion = (String) this.vista.jCmb_Profesional.getSelectedItem();

        if (seleccion != null && seleccion.contains("(") && seleccion.contains(")")) {
            String matricula = seleccion.substring(
                    seleccion.lastIndexOf("(") + 1,
                    seleccion.lastIndexOf(")")
            );
            this.masajistaSeleccionado = this.empleadoData.buscarEmpleadoPorMatricula(matricula);
        } else {
            this.masajistaSeleccionado = null;
        }
    }

    private void actualizarConsultorioSeleccionado() {
        String seleccion = (String) this.vista.jCmb_Consultorio.getSelectedItem();

        if (seleccion != null) {
            int nro = Integer.parseInt(seleccion.split(" ")[1]);
            this.consultorioSeleccionado = this.consultorioData.buscarConsultorio(nro);
        } else {
            this.consultorioSeleccionado = null;
        }
    }

    private void actualizarInstalacionSeleccionada() {
        String seleccion = (String) this.vista.jCmb_Instalacion.getSelectedItem();

        if (seleccion != null && !seleccion.equals("Ninguna")) {
            String nombreInst = seleccion.split(" - ")[0];

            for (Instalacion inst : this.instalacionData.listarTodasInstalacionesActivas()) {
                if (inst.getNombre().equals(nombreInst)) {
                    this.instalacionSeleccionado = inst;
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
        } else if (e.getSource() == vista.jButton_Modificar_Guardar) {
            guardarCambiosEnTabla();//Guarda cambios hechos en las celdas de la tabla
        } else if (e.getSource() == vista.jButton_Anular_Sesión) {
            anularSesionSeleccionada();//Anula la sesión que se seleccionó
        } else if (e.getSource() == this.vista.jButton_BuscarPack) {
            buscarDiaDeSpaPorCodigo();
        } else if (e.getSource() == vista.btnActualizar) {
            cargarCombos();
            configurarFechaHoraPorDefecto();
            configurarTabla();
            cargarSesionesEnTabla();
            limpiarCampos();
        }

        if (e.getSource() == vista.jRadioButtonTodasSes
                || e.getSource() == vista.jRadioButtonSesActivas
                || e.getSource() == vista.jRadioButtonSesInactivas) {

            cargarSesionesFiltradas();
            return;
        }
    }

    private void cargarSesionesFiltradas() {

        DefaultTableModel modelo = (DefaultTableModel) vista.tbSesiones.getModel();
        modelo.setRowCount(0);

        List<Sesion> lista;

        if (this.diaActual != null) {

            int codPack = this.diaActual.getCodPack();
            lista = sesionData.listarSesionesPorPack(codPack);

            if (vista.jRadioButtonSesActivas.isSelected()) {
                lista.removeIf(s -> !s.isActiva());
            } else if (vista.jRadioButtonSesInactivas.isSelected()) {
                lista.removeIf(Sesion::isActiva);
            }

        } else {//sin pack selecc

            if (vista.jRadioButtonSesActivas.isSelected()) {
                lista = sesionData.listarSesionesActivas();
            } else if (vista.jRadioButtonSesInactivas.isSelected()) {
                lista = sesionData.listarSesionesInactivas();
            } else {
                lista = sesionData.listarSesiones();
            }
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Sesion s : lista) {

            String fechaHora = "";
            if (s.getFechaHoraInicio() != null) {
                fechaHora = s.getFechaHoraInicio().format(formato);
            }

            String masajista = "";
            if (s.getMasajista() != null) {
                if (s.getMasajista().getNombre() != null) {
                    masajista = s.getMasajista().getNombre() + " " + s.getMasajista().getApellido();
                } else {
                    masajista = s.getMasajista().getMatricula();
                }
            }

            String tratamiento = "";
            if (s.getTratamiento() != null) {
                tratamiento = s.getTratamiento().getNombre();
            }

            modelo.addRow(new Object[]{
                s.getCodSesion(),
                s.isActiva(),
                fechaHora,
                masajista,
                tratamiento,
                String.format("%.2f", s.getMonto()),
                s.getNotas() != null ? s.getNotas() : ""
            });
        }
    }

    private void agregarSesion() {

        if (!validarCampos()) {
            return;
        }

        //Verificar que haya un Día de Spa (pack) seleccionado
        if (this.diaActual == null) {
            JOptionPane.showMessageDialog(this.vista,
                    "Primero busque y seleccione un Día de Spa (pack) válido antes de agregar sesiones.",
                    "Pack no seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            //Fecha y hora desde la vista
            LocalDate fecha = this.vista.jDC_Fecha.getDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            String horaStr = (String) this.vista.jCmb_HoraInicio.getSelectedItem();
            LocalTime hora = LocalTime.parse(horaStr);

            LocalDateTime fechaHoraInicio = LocalDateTime.of(fecha, hora);

            long minutos = (long) this.tratamientoSeleccionado.getDuracion();
            LocalDateTime fechaHoraFinal = fechaHoraInicio.plusMinutes(minutos);

            double montoSesion = 0.0;
            if (this.tratamientoSeleccionado != null) {
                montoSesion += this.tratamientoSeleccionado.getCosto();
            }

            if (this.instalacionSeleccionado != null) {
                montoSesion += this.instalacionSeleccionado.getPrecio();
            }

            if (this.tratamientoSeleccionado != null) {
                fechaHoraFinal = fechaHoraInicio.plusMinutes((long) this.tratamientoSeleccionado.getDuracion());
            }

            //Verificar disponibilidad
            if (!verificarDisponibilidad(fechaHoraInicio, fechaHoraFinal)) {
                JOptionPane.showMessageDialog(this.vista,
                        "No hay disponibilidad en el horario seleccionado. Por favor, elija otro horario.",
                        //"Masajista, consultorio o instalación NO disponibles."
                        "No Disponible", JOptionPane.WARNING_MESSAGE);
                return;
            }

            //Registrador (recepción)
            Empleado registrador = obtenerRegistrador();
            if (registrador == null) {
                JOptionPane.showMessageDialog(this.vista,
                        "No se encontró un recepcionista disponible para registrar la sesión.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Toma el codPack del Día de Spa actual
            int codPack = this.diaActual.getCodPack();

            //Crear la sesión vinculada a ese pack
            Sesion sesionNueva = new Sesion(
                    this.masajistaSeleccionado,
                    registrador,
                    this.consultorioSeleccionado,
                    this.tratamientoSeleccionado,
                    this.instalacionSeleccionado,
                    fechaHoraInicio,
                    fechaHoraFinal,
                    codPack
            );

            sesionNueva.setMonto(montoSesion); //seteamos monto
            sesionNueva.setNotas(this.vista.jTextArea_Notas.getText().trim()); //seteamos también notas

            this.sesionData.crearSesion(sesionNueva);// Guardar en BD

            //Mensaje cuando es creada la sesión
            JOptionPane.showMessageDialog(this.vista,
                    "Sesión creada exitosamente.\n"
                    + "Fecha: " + fechaHoraInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n"
                    + "Profesional: " + this.masajistaSeleccionado.getNombre() + " " + this.masajistaSeleccionado.getApellido() + "\n"
                    + "Tratamiento: " + this.tratamientoSeleccionado.getNombre() + "\n"
                    + "Monto: $" + this.vista.jTextField_MontoSesion.getText(),
                    "Sesión Creada", JOptionPane.INFORMATION_MESSAGE);

            limpiarCampos();

            //Recargar la tabla pero SOLO con las sesiones de ese pack
            cargarSesionesEnTablaPorPack(codPack);

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

        if (this.vista.jCmb_HoraInicio.getSelectedItem() == null) {
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

        if (!"Ninguna".equals(this.vista.jCmb_Instalacion.getSelectedItem())
                && this.instalacionSeleccionado == null) {
            JOptionPane.showMessageDialog(this.vista, "Seleccione una instalación válida.");
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

        //Masajista
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
        //Consultorios
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
        //Instalación
        if (this.instalacionSeleccionado != null) {

            List<Instalacion> instLibres = this.sesionData.listarInstalacionesLibres(inicio, fin);
            boolean okInstalacion = false;

            for (Instalacion i : instLibres) {
                if (i.getCodInstal() == this.instalacionSeleccionado.getCodInstal()) {
                    okInstalacion = true;
                    break;
                }
            }

            return okInstalacion;
        }

        return true;//no cortar si no hay instalación
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

        if (vista.jCmb_TipoTratamiento.getItemCount() > 0) {
            vista.jCmb_TipoTratamiento.setSelectedIndex(0);
        }

        if (vista.jCmb_Especialidad.getItemCount() > 0) {
            vista.jCmb_Especialidad.setSelectedIndex(0);
        }

        if (vista.jCmb_Tratamiento.getItemCount() > 0) {
            vista.jCmb_Tratamiento.setSelectedIndex(0);
        }

        if (vista.jCmb_Profesional.getItemCount() > 0) {
            vista.jCmb_Profesional.setSelectedIndex(0);
        }
        if (vista.jCmb_Instalacion.getItemCount() > 0) {
            vista.jCmb_Instalacion.setSelectedIndex(0);
        }

        if (vista.jCmb_Consultorio.getItemCount() > 0) {
            vista.jCmb_Consultorio.setSelectedIndex(0);
        }

        this.vista.jTextField_MontoSesion.setText("0.00");
        this.vista.jTextArea_Notas.setText("");

        actualizarTratamientoSeleccionado();
        actualizarMasajistaSeleccionado();
        actualizarConsultorioSeleccionado();
        actualizarInstalacionSeleccionada();
    }

    public void configurarTabla() {

        String[] columnas = {
            "ID", "Estado", "Fecha/Hora", "Masajista", "Tratamiento",
            "Hora Inicio", "Hora Fin", "Tipo", "Especialidad",
            "Instalación", "Consultorio", "Monto", "Notas"
        };

        DefaultTableModel modeloEditable = new DefaultTableModel(new Object[][]{}, columnas) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 12; //Solo Estado y Notas editables
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) {
                    return Boolean.class;
                }
                return String.class;
            }
        };

        vista.tbSesiones.setModel(modeloEditable);
        vista.tbSesiones.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    public void agregarFilaSesion(Object[] datos) {
        DefaultTableModel modelo = (DefaultTableModel) vista.tbSesiones.getModel();
        modelo.addRow(datos);
    }

    public void limpiarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tbSesiones.getModel();
        modelo.setRowCount(0);
    }

    private void guardarCambiosEnTabla() {//modificar sesión

        DefaultTableModel modelo = (DefaultTableModel) vista.tbSesiones.getModel();

        for (int i = 0; i < modelo.getRowCount(); i++) {
            try {
                //obtenemos el id de la sesión
                int idSesion = Integer.parseInt(modelo.getValueAt(i, 0).toString());

                //Obtener los valores editados
                Boolean nuevoEstado = (Boolean) modelo.getValueAt(i, 1); //Estado
                String nuevoComentario = (String) modelo.getValueAt(i, 6); //Notas/comentario/observaciones

                //Buscar la sesión original completa para no perder las FK
                Sesion sesionActualizar = sesionData.buscarSesionPorCodigo(idSesion);

                if (sesionActualizar != null) {//Aplica los cambios
                    // Nota: Tu clase Sesion debe tener un campo de Comentarios/Notas para este ejemplo
                    //ssesionActualizar.setComentarios(nuevoComentario); 
                    sesionActualizar.setEstado(nuevoEstado); //(Activo/Anulado)
                    sesionActualizar.setNotas(nuevoComentario);
                    //actualizar el cambio en la BD
                    sesionData.actualizarSesion(sesionActualizar);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista,
                        "Error al procesar fila " + (i + 1) + ": " + ex.getMessage(),
                        "Error de al guardar", JOptionPane.ERROR_MESSAGE);
            }
        }
        JOptionPane.showMessageDialog(vista,
                "Cambios de la tabla guardados exitosamente en la base de datos.");
    }

    private void anularSesionSeleccionada() {

        int filaSeleccionada = vista.tbSesiones.getSelectedRow();

        if (filaSeleccionada == -1) {
            DefaultTableModel modelo = (DefaultTableModel) vista.tbSesiones.getModel();
            int idSesion = Integer.parseInt(modelo.getValueAt(filaSeleccionada, 0).toString());

            int confirm = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Desea anular la sesión " + idSesion + "?",
                    "Confirmar anulación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                sesionData.anularSesion(idSesion);
                // actualizamos la tabla
                modelo.setValueAt(false, filaSeleccionada, 1); //Column Estado
            } else {
                JOptionPane.showMessageDialog(vista, "Seleccione una fila de la tabla para anular la sesión.");
                return;
            }
        }
    }

    private void limpiarTablaSesiones() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tbSesiones.getModel();
        modelo.setRowCount(0);
    }

    private void cargarSesionesEnTablaPorPack(int codPack) {

        DefaultTableModel modelo = (DefaultTableModel) vista.tbSesiones.getModel();
        modelo.setRowCount(0);

        List<Sesion> sesiones = sesionData.listarSesionesPorPack(codPack);

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

        for (Sesion s : sesiones) {

            String fechaHora = s.getFechaHoraInicio().format(formatoFecha);
            String horaIni = s.getFechaHoraInicio().format(formatoHora);
            String horaFin = s.getFechaHoraFinal().format(formatoHora);

            String masajista = "";
            if (s.getMasajista() != null) {
                if (s.getMasajista().getNombre() != null) {
                    masajista = s.getMasajista().getNombre() + " " + s.getMasajista().getApellido();
                } else {
                    masajista = s.getMasajista().getMatricula();
                }
            }

            String tratamiento = s.getTratamiento() != null ? s.getTratamiento().getNombre() : "";
            String tipo = s.getTratamiento() != null ? s.getTratamiento().getTipo() : "";
            String especialidad = s.getMasajista() != null ? s.getMasajista().getEspecialidad() : "";
            String instalacion = (s.getInstalacion() != null) ? s.getInstalacion().getNombre() : "Ninguna";

            String consultorio = (s.getConsultorio() != null)
                    ? "Consultorio " + s.getConsultorio().getNroConsultorio()
                    : "";

            modelo.addRow(new Object[]{
                s.getCodSesion(),
                s.isActiva(),
                fechaHora,
                masajista,
                tratamiento,
                horaIni,
                horaFin,
                tipo,
                especialidad,
                instalacion,
                consultorio,
                String.format("%.2f", s.getMonto()),
                s.getNotas() != null ? s.getNotas() : ""
            });
        }
    }

    private void buscarDiaDeSpaPorCodigo() {
        String textoCodPack = vista.jTextField_NumPack.getText().trim();

        if (textoCodPack.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese un número de pack.");
            return;
        }

        int codPack;
        try {
            codPack = Integer.parseInt(textoCodPack);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El código de pack debe ser numérico.");
            return;
        }

        DiaDeSpa dia = diaDeSpaData.buscarDiaDeSpa(codPack);

        if (dia == null || !dia.isEstado()) {
            JOptionPane.showMessageDialog(vista,
                    "No se encontró un Día de Spa activo con ese código.",
                    "Pack inexistente", JOptionPane.WARNING_MESSAGE);

            this.diaActual = null;

            mostrarTablaInicial();
            limpiarCampos();

        } else {
            this.diaActual = dia;
            cargarSesionesEnTablaPorPack(codPack);
        }
    }

    private void mostrarTablaInicial() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tbSesiones.getModel();
        modelo.setRowCount(0);

        // Opcional: fila indicativa
        modelo.addRow(new Object[]{
            "-", // ID
            false, // Estado
            "-", // Fecha/Hora
            "Sin datos", // Masajista
            "Busque un pack", // Tratamiento
            "0.00", // Monto
            "" // Notas
        });
    }
}
