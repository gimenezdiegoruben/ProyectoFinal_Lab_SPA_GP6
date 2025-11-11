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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
public class ControladorDiaDeSpa implements ActionListener, KeyListener {

    private final VistaDiaDeSpa vista;
    private final DiaDeSpaData diaDeSpaData;
    private final ClienteData clienteData;
    private final Vista_MenuSpa menu;
    private static DefaultTableModel modelo;
    private double montoTotal = 0;

    // Campo para mantener el ID del paquete seleccionado (para Modificar/Eliminar)
    private int codPackSeleccionado = -1;
    private boolean buscar = false;

    public ControladorDiaDeSpa(VistaDiaDeSpa vista, DiaDeSpaData diaDeSpaData, ClienteData clienteData, Vista_MenuSpa menu) {
        this.vista = vista;
        this.diaDeSpaData = diaDeSpaData;
        this.clienteData = clienteData;
        this.menu = menu;

        this.modelo = (DefaultTableModel) vista.tbSesiones.getModel();

        this.vista.jbtBuscar.addActionListener(this);
        this.vista.jbtNuevo.addActionListener(this);
        this.vista.jbtGuardar.addActionListener(this);
        this.vista.jbtEliminar.addActionListener(this);
        this.vista.jbtSalir.addActionListener(this);
        this.vista.btnAgregarSesiones.addActionListener(this);
        this.vista.jbtBuscarTurnos.addActionListener(this);
        this.vista.jtxDNI.addKeyListener(this);

    }

    public void iniciar() {
        menu.JDesktopPFondo.add(vista);
        vista.setVisible(true);
        menu.JDesktopPFondo.moveToFront(vista);
        vista.requestFocus();

        vista.jtxNombre.setEditable(false);
        vista.jtxTelefono.setEditable(false);
        vista.jtxMonto.setEditable(false);
        vista.jbtGuardar.setEnabled(false);
        vista.jbtEliminar.setEnabled(false);
        vista.btnAgregarSesiones.setEnabled(false);
        vista.jbtBuscar.setEnabled(false);
        modificarTabla();
    }

    public void modificarTabla() {
        modelo = new DefaultTableModel() {
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
        vista.tbSesiones.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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
                    } else {
                        JOptionPane.showMessageDialog(null, "Ingrese un cliente válido", "Error", JOptionPane.ERROR_MESSAGE);
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
            vista.btnAgregarSesiones.setEnabled(true);
            vista.jbtBuscar.setEnabled(true);
        }

        if (e.getSource() == vista.jbtGuardar) {
            boolean repetido = false;
            boolean guardado = false;
            if (buscar) {
                if (vista.jtxDNI.getText().trim().length() < 8) {
                    JOptionPane.showMessageDialog(null, "Ingrese un DNI correcto", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    LocalDate fecha = this.vista.jdcFecha.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    String horaString = (String) this.vista.jcboxHora.getSelectedItem();
                    LocalTime hora = LocalTime.parse(horaString);
                    LocalDateTime fechayhora = LocalDateTime.of(fecha, hora);
                    Cliente c1 = clienteData.buscarPorDni(Long.parseLong(vista.jtxDNI.getText().trim()));
                    DiaDeSpa d1 = new DiaDeSpa(fechayhora, vista.jTextAreaPreferencias.getText().trim(), c1, ListaSesiones(), Double.parseDouble(vista.jtxMonto.getText().trim()), vista.jcheckbEstado.isSelected());
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
                        buscar = false;
                        vista.jbtGuardar.setEnabled(false);
                        vista.jbtEliminar.setEnabled(false);
                        vista.btnAgregarSesiones.setEnabled(false);
                        vista.jbtBuscar.setEnabled(false);
                    }
                }
            } else {
                if (vista.jtxDNI.getText().trim().length() < 8) {
                    JOptionPane.showMessageDialog(null, "Ingrese un DNI correcto", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    LocalDate fecha = this.vista.jdcFecha.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    String horaString = (String) this.vista.jcboxHora.getSelectedItem();
                    LocalTime hora = LocalTime.parse(horaString);
                    LocalDateTime fechayhora = LocalDateTime.of(fecha, hora);
                    Cliente c1 = clienteData.buscarPorDni(Long.parseLong(vista.jtxDNI.getText().trim()));
                    DiaDeSpa d1 = new DiaDeSpa(fechayhora, vista.jTextAreaPreferencias.getText().trim(), c1, ListaSesiones(), Double.parseDouble(vista.jtxMonto.getText().trim()), vista.jcheckbEstado.isSelected());
                    List<DiaDeSpa> turnos = diaDeSpaData.listarDiaDeSpa();
                    for (DiaDeSpa aux : turnos) {
                        if (aux.getFechayhora().equals(d1.getFechayhora())
                                && aux.getPreferencias().equals(d1.getPreferencias())
                                && aux.getCliente().getCodCli() == d1.getCliente().getCodCli()
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
                        vista.jbtGuardar.setEnabled(false);
                        vista.jbtEliminar.setEnabled(false);
                        vista.btnAgregarSesiones.setEnabled(false);
                        vista.jbtBuscar.setEnabled(false);
                        guardado = true;
                    }
                }
            }
            if (guardado) {
                desactivarCampos();
            }

        }

        if (e.getSource() == vista.jbtEliminar) {
            DiaDeSpa d1 = diaDeSpaData.buscarDiaDeSpa(codPackSeleccionado);
            if (d1.getCliente().getDni() == Long.parseLong(vista.jtxDNI.getText().trim())
                    && d1.getPreferencias().equals(vista.jTextAreaPreferencias.getText().trim())
                    && d1.getSesiones().equals(ListaSesiones())) {
                int confirmacion = JOptionPane.showConfirmDialog(null, "¿Estás seguro el turno?", "Confirmación de eliminación", JOptionPane.YES_NO_OPTION);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    diaDeSpaData.eliminarDiaDeSpa(codPackSeleccionado);
                    JOptionPane.showMessageDialog(null, "Turno eliminado con éxito", "Válido", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    vista.jbtEliminar.setEnabled(false);
                }
            } else {
                JOptionPane.showMessageDialog(null, "El turno que intentas eliminar o no existe o ha sido modificado sin ser guardado!", "Error", JOptionPane.ERROR_MESSAGE);
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
        
        if (e.getSource() == vista.jbtBuscarTurnos) {
            
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

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
    public void keyReleased(KeyEvent e) {
    }

    public void limpiarCampos() {
        vista.jtxDNI.setText("");
        vista.jdcFecha.setDate(null);
        vista.jcboxHora.setSelectedItem(null);
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
        List<Sesion> sesiones = new ArrayList<>();
        int filas = modelo.getRowCount();

        for (int i = 0; i < filas; i++) {

            Empleado masajista = (Empleado) modelo.getValueAt(i, 0);
            Empleado registrador = (Empleado) modelo.getValueAt(i, 1);
            Consultorio consultorio = (Consultorio) modelo.getValueAt(i, 2);
            Tratamiento tratamiento = (Tratamiento) modelo.getValueAt(i, 3);
            Instalacion instalacion = (Instalacion) modelo.getValueAt(i, 4);
            LocalDateTime horaInicio = (LocalDateTime) modelo.getValueAt(i, 5);
            LocalDateTime horaFin = (LocalDateTime) modelo.getValueAt(i, 6);
            int turno = Integer.parseInt(modelo.getValueAt(i, 7).toString());

            Sesion s1 = new Sesion(masajista, registrador, consultorio, tratamiento, instalacion, horaInicio, horaFin, turno);
            sesiones.add(s1);
        }

        return sesiones;
    }
    
    public static void agregarSesion(Sesion sesion) {
        // Se envian las sesions desde vistaSesiones y se cargan en la tabla
        Object[] fila = new Object[9];
        fila[0] = sesion.getMasajista().getNombre();
        fila[1] = sesion.getRegistrador().getNombre();
        fila[2] = sesion.getConsultorio().getNroConsultorio();
        fila[3] = sesion.getTratamiento().getNombre();
        fila[4] = sesion.getInstalacion().getNombre();
        fila[5] = sesion.getFechaHoraInicio();
        fila[6] = sesion.getFechaHoraFinal();
        fila[7] = sesion.getCodPack();
        fila[8] = sesion.getEstado() ? "Activo" : "Inactivo";
        modelo.addRow(fila);
    }
    
    public void turnoBuscado(DiaDeSpa diaDeSpa) {
        
    }
}
