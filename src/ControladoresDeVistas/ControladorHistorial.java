package ControladoresDeVistas;

import Modelos.Cliente;
import Modelos.Consultorio;
import Modelos.DiaDeSpa;
import Modelos.Empleado;
import Modelos.Instalacion;
import Modelos.Producto;
import Modelos.Sesion;
import Modelos.Tratamiento;
import Persistencias_Conexion.ClienteData;
import Persistencias_Conexion.ConsultorioData;
import Persistencias_Conexion.DiaDeSpaData;
import Persistencias_Conexion.EmpleadoData;
import Persistencias_Conexion.InstalacionData;
import Persistencias_Conexion.ProductoData;
import Persistencias_Conexion.SesionData;
import Persistencias_Conexion.TratamientoData;
import Vistas.VistaHistorial;
import Vistas.Vista_MenuSpa;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */
public class ControladorHistorial implements ActionListener, PropertyChangeListener {

    private final VistaHistorial vista;
    private final ClienteData clienteData;
    private final ConsultorioData consultorioData;
    private final DiaDeSpaData diaDeSpaData;
    private final EmpleadoData empleadoData;
    private final InstalacionData instalacionData;
    private final ProductoData productoData;
    private final SesionData sesionData;
    private final TratamientoData tratamientoData;
    private final Vista_MenuSpa menu;

    private static DefaultTableModel modelo;
    private ButtonGroup grupoLista;

    public ControladorHistorial(VistaHistorial vista, ClienteData clienteData, ConsultorioData consultorioData, DiaDeSpaData diaDeSpaData, EmpleadoData empleadoData, InstalacionData instalacionData, ProductoData productoData, SesionData sesionData, TratamientoData tratamientoData, Vista_MenuSpa menu) {
        this.vista = vista;
        this.clienteData = clienteData;
        this.consultorioData = consultorioData;
        this.diaDeSpaData = diaDeSpaData;
        this.empleadoData = empleadoData;
        this.instalacionData = instalacionData;
        this.productoData = productoData;
        this.sesionData = sesionData;
        this.tratamientoData = tratamientoData;
        this.menu = menu;

        this.modelo = (DefaultTableModel) vista.jtbhistorial.getModel();
        this.vista.jbtSalir.addActionListener(this);
        this.vista.jcbSeccion.addActionListener(this);
        this.vista.rbtnActivo.addActionListener(this);
        this.vista.rbtnTodos.addActionListener(this);
        this.vista.rbtnInactivo.addActionListener(this);
        this.vista.jdcListarFecha.addPropertyChangeListener(this);

        grupoLista = new ButtonGroup();
        grupoLista.add(vista.rbtnActivo);
        grupoLista.add(vista.rbtnTodos);
        grupoLista.add(vista.rbtnInactivo);
    }

    public void iniciar() {
        menu.JDesktopPFondo.add(vista);
        vista.setVisible(true);
        menu.JDesktopPFondo.moveToFront(vista);
        vista.requestFocus();
        cargarComboBox();
        cargarTablaSegunSeccion();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.jcbSeccion) {
            cargarTablaSegunSeccion();
        }
        if (e.getSource() == vista.jbtSalir) {
            vista.dispose();
        }
        if (e.getSource() == vista.rbtnActivo || e.getSource() == vista.rbtnInactivo || e.getSource() == vista.rbtnTodos) {
            if (vista.rbtnActivo.isSelected()) {
                cargarTablaSegunSeccion();
            } else if (vista.rbtnInactivo.isSelected()) {
                cargarTablaSegunSeccion();
            } else if (vista.rbtnTodos.isSelected()) {
                cargarTablaSegunSeccion();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if ("date".equals(pce.getPropertyName())) {

            if (vista.jdcListarFecha.getDate() != null) {
                cargarTablaSegunSeccion();
            }

        }
    }

    public void cargarTablaSegunSeccion() {
        if (vista.jcbSeccion.getSelectedItem().toString().equals("---")) {
            vista.jdcListarFecha.setDate(null);
            vista.jdcListarFecha.setEnabled(false);
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            vista.jtbhistorial.setModel(modelo);

            modelo.setRowCount(0);
        }
        if (vista.jcbSeccion.getSelectedItem().toString().equals("Clientes")) {
            vista.jdcListarFecha.setDate(null);
            vista.jdcListarFecha.setEnabled(false);
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            modelo.addColumn("Nº Cliente");
            modelo.addColumn("DNI");
            modelo.addColumn("Nombre");
            modelo.addColumn("Telefono");
            modelo.addColumn("Edad");
            modelo.addColumn("Afecciones");
            modelo.addColumn("Estado");

            vista.jtbhistorial.setModel(modelo);

            modelo.setRowCount(0);

            List<Cliente> clientes = new ArrayList<>();

            if (vista.rbtnActivo.isSelected()) {
                clientes = clienteData.listarClientesActivos();
            } else if (vista.rbtnInactivo.isSelected()) {
                clientes = clienteData.listarClientesInactivos();
            } else if (vista.rbtnTodos.isSelected()) {
                clientes = clienteData.listarClientes();
            }

            for (Cliente aux : clientes) {
                Object[] fila = new Object[7];
                fila[0] = aux.getCodCli();
                fila[1] = aux.getDni();
                fila[2] = aux.getNombre();
                fila[3] = aux.getTelefono();
                fila[4] = aux.getEdad();
                fila[5] = aux.getAfecciones();
                fila[6] = aux.isEstado() ? "Activo" : "Inactivo";
                modelo.addRow(fila);
            }
        } else if (vista.jcbSeccion.getSelectedItem().toString().equals("Consultorios")) {
            vista.jdcListarFecha.setDate(null);
            vista.jdcListarFecha.setEnabled(false);
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            modelo.addColumn("Nº Consultorio");
            modelo.addColumn("Usos");
            modelo.addColumn("Equipamiento");
            modelo.addColumn("Apto");

            vista.jtbhistorial.setModel(modelo);

            modelo.setRowCount(0);

            List<Consultorio> consultorios = consultorioData.listarConsultorios();

            for (Consultorio aux : consultorios) {
                Object[] fila = new Object[4];
                fila[0] = aux.getNroConsultorio();
                fila[1] = aux.getUsos();
                fila[2] = aux.getEquipamiento();
                fila[3] = aux.getApto();
                modelo.addRow(fila);
            }
        } else if (vista.jcbSeccion.getSelectedItem().toString().equals("Turnos")) {
            vista.jdcListarFecha.setEnabled(true);

            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            modelo.addColumn("Nº Turno");
            modelo.addColumn("Fecha");
            modelo.addColumn("Preferencias");
            modelo.addColumn("Nº Cliente");
            modelo.addColumn("Sesiones");
            modelo.addColumn("Monto");
            modelo.addColumn("Estado");

            vista.jtbhistorial.setModel(modelo);

            modelo.setRowCount(0);

            List<DiaDeSpa> turnos = new ArrayList<>();

            if (vista.rbtnActivo.isSelected()) {
                turnos = diaDeSpaData.listarDiaDeSpaActivos();
            }
            if (vista.rbtnInactivo.isSelected()) {
                turnos = diaDeSpaData.listarDiaDeSpaInactivos();
            }
            if (vista.rbtnTodos.isSelected()) {
                turnos = diaDeSpaData.listarDiaDeSpa();
            }
            if (vista.jdcListarFecha.getDate() != null) {
                Date fechaElegidaDate = vista.jdcListarFecha.getDate();
                LocalDate fechaElegida = fechaElegidaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                for (DiaDeSpa aux : turnos) {
                    LocalDate fechaTurno = aux.getFechayhora().toLocalDate();
                    if (fechaTurno.equals(fechaElegida)) {
                        Object[] fila = new Object[7];
                        fila[0] = aux.getCodPack();
                        fila[1] = aux.getFechayhora();
                        fila[2] = aux.getPreferencias();
                        fila[3] = aux.getCliente().getCodCli();
                        List<Sesion> listarSesiones = sesionData.listarSesionesPorPack(aux.getCodPack());
                        int cantidad = listarSesiones.size();
                        fila[4] = cantidad;
                        fila[5] = aux.getMonto();
                        fila[6] = aux.isEstado() ? "Activo" : "Inactivo";
                        modelo.addRow(fila);
                    }
                }
            } else {
                for (DiaDeSpa aux : turnos) {
                    Object[] fila = new Object[7];
                    fila[0] = aux.getCodPack();
                    fila[1] = aux.getFechayhora();
                    fila[2] = aux.getPreferencias();
                    fila[3] = aux.getCliente().getCodCli();
                    List<Sesion> listarSesiones = sesionData.listarSesionesPorPack(aux.getCodPack());
                    int cantidad = listarSesiones.size();
                    fila[4] = cantidad;
                    fila[5] = aux.getMonto();
                    fila[6] = aux.isEstado() ? "Activo" : "Inactivo";
                    modelo.addRow(fila);
                }
            }

        } else if (vista.jcbSeccion.getSelectedItem().toString().equals("Empleados")) {
            vista.jdcListarFecha.setDate(null);
            vista.jdcListarFecha.setEnabled(false);
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            modelo.addColumn("Nº Empleado");
            modelo.addColumn("DNI");
            modelo.addColumn("Nombre");
            modelo.addColumn("Telefono");
            modelo.addColumn("Nacimiento");
            modelo.addColumn("Puesto");
            modelo.addColumn("Matricula");
            modelo.addColumn("Estado");

            vista.jtbhistorial.setModel(modelo);

            modelo.setRowCount(0);

            List<Empleado> empleados = new ArrayList<>();

            if (vista.rbtnActivo.isSelected()) {
                empleados = empleadoData.listarEmpleados(true, "Todas");
            }
            if (vista.rbtnInactivo.isSelected()) {
                empleados = empleadoData.listarEmpleados(false, "Todas");
            }
            if (vista.rbtnTodos.isSelected()) {
                empleados = empleadoData.listarTodosLosEmpleados();
            }

            for (Empleado aux : empleados) {
                Object[] fila = new Object[8];
                fila[0] = aux.getIdEmpleado();
                fila[1] = aux.getDni();
                String nombrecompleto = aux.getNombre() + " " + aux.getApellido();
                fila[2] = nombrecompleto;
                fila[3] = aux.getTelefono();
                fila[4] = aux.getFechaNacimiento();
                fila[5] = aux.getPuesto();
                fila[6] = aux.getMatricula();
                fila[7] = aux.isEstado() ? "Activo" : "Inactivo";
                modelo.addRow(fila);
            }

        } else if (vista.jcbSeccion.getSelectedItem().toString().equals("Instalaciones")) {
            vista.jdcListarFecha.setDate(null);
            vista.jdcListarFecha.setEnabled(false);
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            modelo.addColumn("Nº Instalacion");
            modelo.addColumn("Nombre");
            modelo.addColumn("Detalle");
            modelo.addColumn("Precio(30Min)");
            modelo.addColumn("Estado");

            vista.jtbhistorial.setModel(modelo);

            modelo.setRowCount(0);

            List<Instalacion> instalaciones = new ArrayList<>();
            if (vista.rbtnActivo.isSelected()) {
                instalaciones = instalacionData.listarTodasInstalacionesActivas();
            }
            if (vista.rbtnInactivo.isSelected()) {
                instalaciones = instalacionData.listarTodasInstalacionesInactivas();
            }
            if (vista.rbtnTodos.isSelected()) {
                instalaciones = instalacionData.listarTodasInstalaciones();
            }

            for (Instalacion aux : instalaciones) {
                Object[] fila = new Object[5];
                fila[0] = aux.getCodInstal();
                fila[1] = aux.getNombre();
                fila[2] = aux.getDetalleUso();
                fila[3] = aux.getPrecio();
                fila[4] = aux.isEstado() ? "Activo" : "Inactivo";
                modelo.addRow(fila);
            }
        } else if (vista.jcbSeccion.getSelectedItem().toString().equals("Productos")) {
            vista.jdcListarFecha.setDate(null);
            vista.jdcListarFecha.setEnabled(false);
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            modelo.addColumn("Nº Producto");
            modelo.addColumn("Descripcion");
            modelo.addColumn("Precio");
            modelo.addColumn("Stock");
            modelo.addColumn("Nº Tratamiento");

            vista.jtbhistorial.setModel(modelo);

            modelo.setRowCount(0);

            List<Producto> productos = productoData.listarProductos();
            for (Producto aux : productos) {
                Object fila[] = new Object[5];
                fila[0] = aux.getCodProducto();
                fila[1] = aux.getDescripcion();
                fila[2] = aux.getPrecio();
                fila[3] = aux.getStock();
                fila[4] = aux.getTratamiento().getCodTratam();
                modelo.addRow(fila);
            }
        } else if (vista.jcbSeccion.getSelectedItem().toString().equals("Sesiones")) {
            vista.jdcListarFecha.setEnabled(true);
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            modelo.addColumn("Nº Sesion");
            modelo.addColumn("Fecha");
            modelo.addColumn("Nº Tratamiento");
            modelo.addColumn("Nº Consultorio");
            modelo.addColumn("Masajista");
            modelo.addColumn("Registrador");
            modelo.addColumn("Nº Instalación");
            modelo.addColumn("Nº Turno");
            modelo.addColumn("Monto");
            modelo.addColumn("Estado");

            vista.jtbhistorial.setModel(modelo);

            modelo.setRowCount(0);

            List<Sesion> sesiones = new ArrayList<>();

            if (vista.rbtnActivo.isSelected()) {
                sesiones = sesionData.listarSesionesActivas();
            }
            if (vista.rbtnInactivo.isSelected()) {
                sesiones = sesionData.listarSesionesInactivas();
            }
            if (vista.rbtnTodos.isSelected()) {
                sesiones = sesionData.listarSesiones();
            }
            if (vista.jdcListarFecha.getDate() != null) {
                Date fechaElegidaDate = vista.jdcListarFecha.getDate();
                LocalDate fechaElegida = fechaElegidaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                for (Sesion aux : sesiones) {
                    LocalDate fechaTurno = aux.getFechaHoraInicio().toLocalDate();
                    if (fechaTurno.equals(fechaElegida)) {
                        Object fila[] = new Object[10];
                        fila[0] = aux.getCodSesion();
                        fila[1] = aux.getFechaHoraInicio();
                        fila[2] = aux.getTratamiento().getCodTratam();
                        fila[3] = aux.getConsultorio().getNroConsultorio();
                        fila[4] = aux.getMasajista();
                        fila[5] = aux.getRegistrador();
                        fila[6] = aux.getInstalacion().getCodInstal();
                        fila[7] = aux.getCodPack();
                        fila[8] = aux.getMonto();
                        fila[9] = aux.isActiva() ? "Activo" : "Inactivo";
                        modelo.addRow(fila);
                    }
                }
            } else {
                for (Sesion aux : sesiones) {
                    Object fila[] = new Object[10];
                    fila[0] = aux.getCodSesion();
                    fila[1] = aux.getFechaHoraInicio();
                    fila[2] = aux.getTratamiento().getCodTratam();
                    fila[3] = aux.getConsultorio().getNroConsultorio();
                    fila[4] = aux.getMasajista();
                    fila[5] = aux.getRegistrador();
                    fila[6] = aux.getInstalacion().getCodInstal();
                    fila[7] = aux.getCodPack();
                    fila[8] = aux.getMonto();
                    fila[9] = aux.isActiva() ? "Activo" : "Inactivo";
                    modelo.addRow(fila);
                }
            }
        } else if (vista.jcbSeccion.getSelectedItem().toString().equals("Tratamientos")) {
            vista.jdcListarFecha.setDate(null);
            vista.jdcListarFecha.setEnabled(false);
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            modelo.addColumn("Nº Tratamiento");
            modelo.addColumn("Nombre");
            modelo.addColumn("Tipo");
            modelo.addColumn("Duracion");
            modelo.addColumn("Costo");
            modelo.addColumn("Estado");

            vista.jtbhistorial.setModel(modelo);

            modelo.setRowCount(0);

            List<Tratamiento> tratamientos = new ArrayList<>();

            if (vista.rbtnActivo.isSelected()) {
                tratamientos = tratamientoData.listarTratameintosActivos();
            }
            if (vista.rbtnInactivo.isSelected()) {
                tratamientos = tratamientoData.listarTratamientosInactivos();
            }
            if (vista.rbtnTodos.isSelected()) {
                tratamientos = tratamientoData.listarTratamientos();
            }

            for (Tratamiento aux : tratamientos) {
                Object fila[] = new Object[6];
                fila[0] = aux.getCodTratam();
                fila[1] = aux.getNombre();
                fila[2] = aux.getTipo();
                fila[3] = aux.getDuracion();
                fila[4] = aux.getCosto();
                fila[5] = aux.isEstado() ? "Activo" : "Inactivo";
                modelo.addRow(fila);
            }
        }
    }

    public void cargarComboBox() {
        ActionListener[] listeners = vista.jcbSeccion.getActionListeners();
        for (ActionListener al : listeners) {
            vista.jcbSeccion.removeActionListener(al);
        }

        vista.jcbSeccion.removeAllItems();
        vista.jcbSeccion.addItem("---");
        vista.jcbSeccion.addItem("Clientes");
        vista.jcbSeccion.addItem("Consultorios");
        vista.jcbSeccion.addItem("Turnos");
        vista.jcbSeccion.addItem("Empleados");
        vista.jcbSeccion.addItem("Instalaciones");
        vista.jcbSeccion.addItem("Productos");
        vista.jcbSeccion.addItem("Sesiones");
        vista.jcbSeccion.addItem("Tratamientos");

        vista.jcbSeccion.setSelectedIndex(0);

        for (ActionListener al : listeners) {
            vista.jcbSeccion.addActionListener(al);
        }
    }
}
