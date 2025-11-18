package ControladoresDeVistas;

import Modelos.Cliente;
import Modelos.Consultorio;
import Modelos.DiaDeSpa;
import Modelos.Empleado;
import Modelos.Instalacion;
import Modelos.Sesion;
import Modelos.Tratamiento;
import Persistencias_Conexion.DiaDeSpaData;
import Persistencias_Conexion.ClienteData;
import Persistencias_Conexion.ConsultorioData;
import Persistencias_Conexion.EmpleadoData;
import Persistencias_Conexion.InstalacionData;
import Persistencias_Conexion.SesionData;
import Persistencias_Conexion.TratamientoData;
import Vistas.VistaCliente;
import Vistas.VistaDiaDeSpa;
import Vistas.VistaSesiones;
import Vistas.Vista_MenuSpa;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import javax.swing.table.DefaultTableModel;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */
public class ControladorDiaDeSpa implements ActionListener, KeyListener, PropertyChangeListener, MouseListener {

    private final VistaDiaDeSpa vista;
    private final DiaDeSpaData diaDeSpaData;
    private final ClienteData clienteData;
    private final SesionData sesionData;
    private final Vista_MenuSpa menu;
    private static DefaultTableModel modelo;
    private ButtonGroup grupoLista;

    private int codPackSeleccionado = -1;
    private boolean buscar = false;

    public ControladorDiaDeSpa(VistaDiaDeSpa vista, DiaDeSpaData diaDeSpaData, ClienteData clienteData, SesionData sesionData, Vista_MenuSpa menu) {
        this.vista = vista;
        this.diaDeSpaData = diaDeSpaData;
        this.clienteData = clienteData;
        this.sesionData = sesionData;
        this.menu = menu;

        this.modelo = (DefaultTableModel) vista.tbSesiones.getModel();

        this.vista.jbtBuscar.addActionListener(this);
        this.vista.jbtNuevo.addActionListener(this);
        this.vista.jbtGuardar.addActionListener(this);
        this.vista.jbtEliminar.addActionListener(this);
        this.vista.jbtSalir.addActionListener(this);
        this.vista.btnAgregarSesiones.addActionListener(this);
        this.vista.rbtnTodos.addActionListener(this);
        this.vista.rbtnActivo.addActionListener(this);
        this.vista.rbtnInactivo.addActionListener(this);
        this.vista.jtxDNI.addKeyListener(this);
        this.vista.jdcListarFecha.addPropertyChangeListener(this);
        this.vista.tbSesiones.addMouseListener(this);

        grupoLista = new ButtonGroup();
        grupoLista.add(vista.rbtnTodos);
        grupoLista.add(vista.rbtnActivo);
        grupoLista.add(vista.rbtnInactivo);
        vista.rbtnTodos.setSelected(true);

    }

    public void iniciar() {
        menu.JDesktopPFondo.add(vista);
        vista.setVisible(true);
        menu.JDesktopPFondo.moveToFront(vista);
        vista.requestFocus();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        vista.jdcFecha.getJCalendar().setMinSelectableDate(cal.getTime());

        vista.jtxNombre.setEditable(false);
        vista.jtxTelefono.setEditable(false);
        vista.jbtGuardar.setEnabled(false);
        vista.jbtEliminar.setEnabled(false);
        modificarTabla();
        cargarComboBox();
    }

    public void modificarTabla() {
        modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelo.addColumn("Nº Turno");
        modelo.addColumn("Fecha y hora");
        modelo.addColumn("Preferencias");
        modelo.addColumn("Cliente");
        modelo.addColumn("Sesiones");
        modelo.addColumn("Monto");
        modelo.addColumn("Estado");

        vista.tbSesiones.setModel(modelo);
        vista.tbSesiones.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        actualizarTabla();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == vista.jbtSalir) {
            vista.dispose();
        }

        if (e.getSource() == vista.jbtBuscar) {
            if (vista.jtxDNI.getText().trim().length() < 8) {
                JOptionPane.showMessageDialog(null, "Ingrese un DNI correcto", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Cliente c1 = clienteData.buscarPorDni(Integer.parseInt(vista.jtxDNI.getText().trim()));
                if (c1 == null) {
                    int confirmacion = JOptionPane.showConfirmDialog(null, "El cliente ingresado no existe, ¿Quieres añadirlo?", "Confirmación de añadir", JOptionPane.YES_NO_OPTION);
                    if (confirmacion == JOptionPane.YES_OPTION) {
                        VistaCliente vista = new VistaCliente();
                        ClienteData data = new ClienteData();
                        ControladorCliente ctrl = new ControladorCliente(vista, data, menu);

                        ctrl.iniciar();
                    }
                } else {
                    vista.jtxNombre.setText(c1.getNombre());
                    vista.jtxTelefono.setText(String.valueOf(c1.getTelefono()));
                }
            }
        }

        if (e.getSource() == vista.jbtNuevo) {
            activarCampos();
            limpiarCampos();
            vista.jbtGuardar.setEnabled(true);
            vista.tbSesiones.clearSelection();
            codPackSeleccionado = -1;
            buscar = false;
        }

        if (e.getSource() == vista.jbtGuardar) {
            boolean repetido = false;
            boolean guardado = false;
            if (vista.jtxDNI.getText().trim().isEmpty() || vista.jdcFecha.getDate() == null || vista.jTextAreaPreferencias.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe llenar todos los campos!!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                if (buscar) {
                    List<Sesion> sesiones = sesionData.listarSesionesPorPack(codPackSeleccionado);
                    if (!sesiones.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No se puede modificar un turno que tenga sesiones cargadas, primero elimine las sesiones vinculadas a este turno", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (vista.jtxDNI.getText().trim().length() < 8) {
                        JOptionPane.showMessageDialog(null, "Ingrese un DNI correcto", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    } else if (vista.jtxDNI.getText().trim().length() == 8) {
                        Cliente c1 = clienteData.buscarPorDni(Long.parseLong(vista.jtxDNI.getText().trim()));
                        if (c1 == null) {
                            return;
                        } else {
                            LocalDate fecha = this.vista.jdcFecha.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                            String horaString = (String) this.vista.jcboxHora.getSelectedItem();
                            LocalTime hora = LocalTime.parse(horaString);
                            LocalDateTime fechayhora = LocalDateTime.of(fecha, hora);
                            DiaDeSpa d1 = new DiaDeSpa(fechayhora, vista.jTextAreaPreferencias.getText().trim(), c1, new ArrayList<>(), vista.jcheckbEstado.isSelected());
                            DiaDeSpa d2 = diaDeSpaData.buscarDiaDeSpa(codPackSeleccionado);
                            if (d1.getFechayhora().equals(d2.getFechayhora())
                                    && d1.getPreferencias().equals(d2.getPreferencias())
                                    && d1.getCliente().getCodCli() == d2.getCliente().getCodCli()
                                    && d1.getSesiones().equals(d2.getSesiones())
                                    && d1.getMonto() == d2.getMonto()
                                    && d1.isEstado() == d2.isEstado()) {
                                JOptionPane.showMessageDialog(null, "No se puede guardar el turno ya que no se han efectuado cambios", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                d1.setCodPack(d2.getCodPack());
                                diaDeSpaData.modificarDiaDeSpa(d1);
                                JOptionPane.showMessageDialog(null, "Turno modificado con éxito", "Válido", JOptionPane.INFORMATION_MESSAGE);
                                limpiarCampos();
                                vista.jtxDNI.setText("");
                                buscar = false;
                                vista.jbtGuardar.setEnabled(false);
                                vista.jbtEliminar.setEnabled(false);
                                cargarTabla();
                                vista.tbSesiones.clearSelection();
                                codPackSeleccionado = -1;
                            }
                        }
                    }
                } else {
                    if (vista.jtxDNI.getText().trim().length() < 8) {
                        JOptionPane.showMessageDialog(null, "Ingrese un DNI correcto", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    } else {
                        LocalDate fecha = this.vista.jdcFecha.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                        String horaString = (String) this.vista.jcboxHora.getSelectedItem();
                        LocalTime hora = LocalTime.parse(horaString, DateTimeFormatter.ofPattern("H:mm"));
                        LocalDateTime fechayhora = LocalDateTime.of(fecha, hora);
                        Cliente c1 = clienteData.buscarPorDni(Long.parseLong(vista.jtxDNI.getText().trim()));
                        System.out.println(c1);
                        DiaDeSpa d1 = new DiaDeSpa(fechayhora, vista.jTextAreaPreferencias.getText().trim(), c1, new ArrayList<>(), vista.jcheckbEstado.isSelected());
                        List<DiaDeSpa> turnos = diaDeSpaData.listarDiaDeSpa();
                        for (DiaDeSpa aux : turnos) {
                            if (aux.getCliente() == null || d1.getCliente() == null) {
                                continue;
                            }
                            Cliente clienteAuxiliar = clienteData.buscarPorId(aux.getCliente().getCodCli());
                            if (aux.getFechayhora().equals(d1.getFechayhora())
                                    && aux.getPreferencias().equals(d1.getPreferencias())
                                    && clienteAuxiliar.getCodCli() == d1.getCliente().getCodCli()
                                    && aux.getSesiones().equals(d1.getSesiones())
                                    && aux.getMonto() == d1.getMonto()
                                    && aux.isEstado() == d1.isEstado()) {
                                JOptionPane.showMessageDialog(null, "El turno que intentas guardar ya existe", "Turno repetido", JOptionPane.ERROR_MESSAGE);
                                repetido = true;
                            }

                        }
                        if (!repetido) {
                            diaDeSpaData.nuevoDiaDeSpa(d1);
                            JOptionPane.showMessageDialog(null, "Turno guardado con éxito", "Válido", JOptionPane.INFORMATION_MESSAGE);
                            limpiarCampos();
                            vista.jtxDNI.setText("");
                            vista.jbtGuardar.setEnabled(false);
                            vista.jbtEliminar.setEnabled(false);
                            guardado = true;
                            vista.tbSesiones.clearSelection();
                            codPackSeleccionado = -1;
                        }
                    }
                }
            }
            if (guardado) {
                desactivarCampos();
                cargarTabla();
            }

        }

        if (e.getSource() == vista.jbtEliminar) {
            List<Sesion> sesiones = sesionData.listarSesionesPorPack(codPackSeleccionado);
            if (!sesiones.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No se puede eliminar un turno que tenga sesiones cargadas", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            DiaDeSpa d1 = diaDeSpaData.buscarDiaDeSpa(codPackSeleccionado);
            Cliente c1 = clienteData.buscarPorId(d1.getCliente().getCodCli());
            if (c1.getDni() == Long.parseLong(vista.jtxDNI.getText().trim())
                    && d1.getPreferencias().equals(vista.jTextAreaPreferencias.getText().trim())
                    && (d1.getSesiones() == null || d1.getSesiones().isEmpty())) {
                int confirmacion = JOptionPane.showConfirmDialog(null, "¿Estás seguro el turno?", "Confirmación de eliminación", JOptionPane.YES_NO_OPTION);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    diaDeSpaData.eliminarDiaDeSpa(codPackSeleccionado);
                    JOptionPane.showMessageDialog(null, "Turno eliminado con éxito", "Válido", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    vista.jtxDNI.setText("");
                    vista.jbtEliminar.setEnabled(false);
                    vista.jbtGuardar.setEnabled(false);
                    cargarTabla();
                    vista.tbSesiones.clearSelection();
                    codPackSeleccionado = -1;
                }
            } else {
                JOptionPane.showMessageDialog(null, "El turno que intentas eliminar tiene sesiones cargadas o ha sido modificado!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == vista.btnAgregarSesiones) {
            VistaSesiones vista = new VistaSesiones();
            SesionData sdata = new SesionData();
            EmpleadoData eData = new EmpleadoData();
            ConsultorioData conData = new ConsultorioData();
            TratamientoData tData = new TratamientoData();
            InstalacionData iData = new InstalacionData();
            ClienteData cliData = new ClienteData();
            DiaDeSpaData dData = new DiaDeSpaData();
            ControladorSesiones ctrl = new ControladorSesiones(vista, sdata, eData, conData, tData, iData, cliData, dData, menu);
            ctrl.iniciar();
        }

        if (e.getSource() == vista.rbtnActivo || e.getSource() == vista.rbtnInactivo || e.getSource() == vista.rbtnTodos) {
            if (vista.rbtnActivo.isSelected()) {
                cargarTabla();
            } else if (vista.rbtnInactivo.isSelected()) {
                cargarTabla();
            } else if (vista.rbtnTodos.isSelected()) {
                cargarTabla();
            }
        }

    }

    @Override
    public void keyTyped(KeyEvent e
    ) {
        if (e.getSource() == vista.jtxDNI) {
            char caracter = e.getKeyChar();
            if ((caracter < '0' || caracter > '9' && caracter != '\b')) {
                e.consume();
            }
            if (vista.jtxDNI.getText().length() >= 8 && caracter != '\b') {
                e.consume();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e
    ) {
    }

    @Override
    public void keyReleased(KeyEvent e
    ) {
    }

    public void limpiarCampos() {
        vista.jtxNombre.setText("");
        vista.jtxTelefono.setText("");
        vista.jdcFecha.setDate(null);
        vista.jcboxHora.setSelectedItem(0);
        vista.jTextAreaPreferencias.setText("");
        vista.jcheckbEstado.setSelected(false);
    }

    public void desactivarCampos() {
        vista.jtxDNI.setEnabled(true);
        vista.jdcFecha.setEnabled(false);
        vista.jcboxHora.setEnabled(false);
        vista.jTextAreaPreferencias.setEnabled(false);
        vista.jcheckbEstado.setEnabled(false);
    }

    public void activarCampos() {
        vista.jtxDNI.setEnabled(true);
        vista.jdcFecha.setEnabled(true);
        vista.jcboxHora.setEnabled(true);
        vista.jTextAreaPreferencias.setEnabled(true);
        vista.jcheckbEstado.setEnabled(true);
    }

    public List<Sesion> ListaSesiones() {
        List<Sesion> sesiones = sesionData.listarSesionesPorPack(codPackSeleccionado);
        return sesiones;
    }

    public void actualizarTabla() {
        modelo.setRowCount(0);
        List<DiaDeSpa> turnos = diaDeSpaData.listarDiaDeSpa();
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

    public void cargarTabla() {
        modelo.setRowCount(0);
        List<DiaDeSpa> turnos = new ArrayList<>();

        if (vista.rbtnInactivo.isSelected()) {
            turnos = diaDeSpaData.listarDiaDeSpaInactivos();
        } else if (vista.rbtnActivo.isSelected()) {
            turnos = diaDeSpaData.listarDiaDeSpaActivos();
        } else {
            turnos = diaDeSpaData.listarDiaDeSpa();
        }
        if (vista.jdcListarFecha.getDate() != null) {
            Date fechaElegidaDate = vista.jdcListarFecha.getDate();
            LocalDate fechaElegida = fechaElegidaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            for (DiaDeSpa aux : turnos) {
                double montoTotal = 0;
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
                    for (Sesion sesionAux : listarSesiones) {
                        montoTotal = montoTotal + sesionAux.getMonto();
                    }
                    fila[5] = montoTotal;
                    fila[6] = aux.isEstado() ? "Activo" : "Inactivo";
                    modelo.addRow(fila);
                }
            }
        } else {
            for (DiaDeSpa aux : turnos) {
                double montoTotal = 0;
                Object[] fila = new Object[7];
                fila[0] = aux.getCodPack();
                fila[1] = aux.getFechayhora();
                fila[2] = aux.getPreferencias();
                fila[3] = aux.getCliente().getCodCli();
                List<Sesion> listarSesiones = sesionData.listarSesionesPorPack(aux.getCodPack());
                int cantidad = listarSesiones.size();
                fila[4] = cantidad;
                for (Sesion sesionAux : listarSesiones) {
                    montoTotal = montoTotal + sesionAux.getMonto();
                }
                fila[5] = montoTotal;
                fila[6] = aux.isEstado() ? "Activo" : "Inactivo";
                modelo.addRow(fila);
            }
        }
    }

    public void cargarComboBox() {
        vista.jcboxHora.removeAllItems();
        vista.jcboxHora.addItem("08:00");
        vista.jcboxHora.addItem("08:30");
        vista.jcboxHora.addItem("09:00");
        vista.jcboxHora.addItem("09:30");
        vista.jcboxHora.addItem("10:00");
        vista.jcboxHora.addItem("10:30");
        vista.jcboxHora.addItem("11:00");
        vista.jcboxHora.addItem("11:30");
        vista.jcboxHora.addItem("12:00");
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if ("date".equals(pce.getPropertyName())) {

            if (vista.jdcListarFecha.getDate() != null) {
                cargarTabla();
            }

        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {

    }

    @Override
    public void mousePressed(MouseEvent me) {
        if (me.getSource() == vista.tbSesiones) {
            int fila = vista.tbSesiones.getSelectedRow();
            if (fila != -1) {
                Cliente c1 = clienteData.buscarPorId(Integer.parseInt(vista.tbSesiones.getValueAt(fila, 3).toString()));
                vista.jtxDNI.setText(String.valueOf(c1.getDni()));
                vista.jtxNombre.setText(c1.getNombre());
                vista.jtxTelefono.setText(String.valueOf(c1.getTelefono()));
                if (vista.tbSesiones.getValueAt(fila, 6).toString().equals("Activo")) {
                    vista.jcheckbEstado.setSelected(true);
                } else {
                    vista.jcheckbEstado.setSelected(false);
                }
                LocalDateTime fechayhora = (LocalDateTime) vista.tbSesiones.getValueAt(fila, 1);
                LocalDate fechaLD = fechayhora.toLocalDate();
                Date fecha = Date.from(fechaLD.atStartOfDay(ZoneId.systemDefault()).toInstant());
                vista.jdcFecha.setDate(fecha);
                LocalTime hora = fechayhora.toLocalTime();
                String horaStr = hora.format(DateTimeFormatter.ofPattern("HH:mm"));
                for (int i = 0; i < vista.jcboxHora.getItemCount(); i++) {
                    if (vista.jcboxHora.getItemAt(i).equals(horaStr)) {
                        vista.jcboxHora.setSelectedIndex(i);
                        break;
                    }
                }
                vista.jTextAreaPreferencias.setText(vista.tbSesiones.getValueAt(fila, 2).toString());
                codPackSeleccionado = Integer.parseInt(vista.tbSesiones.getValueAt(fila, 0).toString());
                vista.jbtEliminar.setEnabled(true);
                vista.jbtGuardar.setEnabled(true);
                activarCampos();
                buscar = true;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {

    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mouseExited(MouseEvent me) {

    }
}
