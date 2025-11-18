package ControladoresDeVistas;

import Modelos.Cliente;
import Modelos.DiaDeSpa;
import Persistencias_Conexion.ClienteData;
import Persistencias_Conexion.DiaDeSpaData;
import Vistas.VistaCliente;
import Vistas.Vista_MenuSpa;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */
public class ControladorCliente implements ActionListener, FocusListener, KeyListener, ListSelectionListener {

    private final VistaCliente vista;
    private final ClienteData data;
    private final DiaDeSpaData diaDeSpaData;
    private final Vista_MenuSpa menu;

    private boolean buscar = false;
    private boolean seleccion = false;
    private int codCliOriginal;
    private ButtonGroup grupoLista;

    public ControladorCliente(VistaCliente vista, ClienteData data, DiaDeSpaData diaDeSpaData, Vista_MenuSpa menu) {
        this.vista = vista;
        this.data = data;
        this.diaDeSpaData = diaDeSpaData;
        this.menu = menu;
        // Eventos
        vista.jbtSalir.addActionListener(this);
        vista.jbtEliminar.addActionListener(this);
        vista.jbtGuardar.addActionListener(this);
        vista.jbtNuevo.addActionListener(this);
        vista.rbtnActivo.addActionListener(this);
        vista.rbtnInactivo.addActionListener(this);
        vista.rbtnTodos.addActionListener(this);
        vista.jtxDNI.addKeyListener(this);
        vista.jlClientes.addListSelectionListener(this);
        vista.jtxDNI.addKeyListener(this);
        vista.jtxNombre.addKeyListener(this);
        vista.jtxTelefono.addKeyListener(this);
        vista.jtxaAfecciones.addKeyListener(this);

        grupoLista = new ButtonGroup();
        grupoLista.add(vista.rbtnActivo);
        grupoLista.add(vista.rbtnInactivo);
        grupoLista.add(vista.rbtnTodos);
        vista.rbtnTodos.setSelected(true);

    }

    public void iniciar() {
        menu.JDesktopPFondo.add(vista);
        vista.setVisible(true);
        menu.JDesktopPFondo.moveToFront(vista);
        vista.requestFocus();

        desactivarCampos();
        vista.jbtGuardar.setEnabled(false);
        vista.jbtEliminar.setEnabled(false);
        vista.jtxDNI.setEnabled(true);
        vista.jlClientes.setModel(new DefaultListModel<>());
        cargarLista();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {

        if (e.getSource() == vista.jbtSalir) {
            vista.dispose();
        }

        if (e.getSource() == vista.jbtEliminar) {
            try {
                List<DiaDeSpa> turnos = diaDeSpaData.listarDiaDeSpaPorCliente(codCliOriginal);
                if (!turnos.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No se puede eliminar un cliente que haya sacado turnos", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int dni = Integer.parseInt(vista.jtxDNI.getText().trim());
                Cliente c1 = data.buscarPorId(codCliOriginal);
                if (c1 != null) {
                    int confirmacion = JOptionPane.showConfirmDialog(null, "¿Estás seguro de eliminar al Cliente " + c1.getNombre() + "?", "Confirmación de eliminación", JOptionPane.YES_NO_OPTION);
                    if (confirmacion == JOptionPane.YES_OPTION) {
                        data.eliminarCliente(dni);
                        JOptionPane.showMessageDialog(null, "Cliente " + c1.getNombre() + " eliminado con éxito", "Válido", JOptionPane.INFORMATION_MESSAGE);
                        limpiarCampos();
                        vista.jbtEliminar.setEnabled(false);
                        vista.jbtGuardar.setEnabled(false);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Ingrese un DNI válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
            cargarLista();
        }

        if (e.getSource() == vista.jbtGuardar) {
            boolean activo = vista.jcheckbEstado.isSelected();
            boolean repetido = false;
            boolean guardadoExitoso = false;
            if (vista.jtxDNI.getText().trim().isEmpty() || vista.jtxNombre.getText().trim().isEmpty() || vista.jtxTelefono.getText().trim().isEmpty() || vista.jdcFechaNac.getDate() == null || vista.jtxaAfecciones.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe llenar todos los campos!!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    if (buscar) {
                        List<DiaDeSpa> turnos = diaDeSpaData.listarDiaDeSpaPorCliente(codCliOriginal);
                        if (!turnos.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "No se puede modificar un cliente que haya sacado turnos", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        Date fechaNacimiento = vista.jdcFechaNac.getCalendar().getTime();
                        LocalDate nacimiento = fechaNacimiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate hoy = LocalDate.now();
                        int edad = Period.between(nacimiento, hoy).getYears();
                        java.util.Date nac = vista.jdcFechaNac.getDate();
                        Instant instant = nac.toInstant();
                        LocalDate fecha = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                        if (vista.jtxDNI.getText().trim().length() < 8) {
                            JOptionPane.showMessageDialog(null, "Ingrese un DNI correcto", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            Cliente c1 = new Cliente(Long.parseLong(vista.jtxDNI.getText().trim()), vista.jtxNombre.getText().trim(), Long.parseLong(vista.jtxTelefono.getText().trim()), edad, vista.jtxaAfecciones.getText().trim(), vista.jcheckbEstado.isSelected(), fecha);
                            Cliente c2 = data.buscarPorId(codCliOriginal);
                            if (c1.getDni() == c2.getDni() && c1.getEdad() == c2.getEdad() && c1.getFechaNac().equals(c2.getFechaNac()) && c1.getNombre().equals(c2.getNombre()) && c1.getTelefono() == c2.getTelefono() && c1.getAfecciones().equals(c2.getAfecciones()) && c1.isEstado() == c2.isEstado()) {
                                JOptionPane.showMessageDialog(null, "No se puede guardar ya que no se han efectuado cambios", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                c1.setCodCli(c2.getCodCli());
                                data.modificarCliente(c1);
                                JOptionPane.showMessageDialog(null, "El cliente " + c1.getNombre() + "ha sido modificado con exito");
                                limpiarCampos();
                                buscar = false;
                                vista.jbtEliminar.setEnabled(false);
                                vista.jbtGuardar.setEnabled(false);
                                cargarLista();
                            }
                        }
                    } else {
                        Date fechaNacimiento = vista.jdcFechaNac.getCalendar().getTime();
                        LocalDate nacimiento = fechaNacimiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate hoy = LocalDate.now();
                        int edad = Period.between(nacimiento, hoy).getYears();
                        java.util.Date nac = vista.jdcFechaNac.getDate();
                        Instant instant = nac.toInstant();
                        LocalDate fecha = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                        if (vista.jtxDNI.getText().trim().length() < 8) {
                            JOptionPane.showMessageDialog(null, "Ingrese un DNI correcto", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            Cliente c1 = new Cliente(Long.parseLong(vista.jtxDNI.getText().trim()), vista.jtxNombre.getText().trim(), Long.parseLong(vista.jtxTelefono.getText().trim()), edad, vista.jtxaAfecciones.getText().trim(), vista.jcheckbEstado.isSelected(), fecha);
                            List<Cliente> listaClientes = data.listarClientes();
                            for (Cliente aux : listaClientes) {
                                if (aux.getNombre().equals(c1.getNombre()) && aux.getTelefono() == c1.getTelefono() && aux.getEdad() == c1.getEdad() && aux.getAfecciones().equals(c1.getAfecciones())) {
                                    JOptionPane.showMessageDialog(null, "El cliente que intentas guardar ya ha sido guardado", "Cliente repetido", JOptionPane.ERROR_MESSAGE);
                                    limpiarCampos();
                                    vista.jbtEliminar.setEnabled(false);
                                    repetido = true;
                                }
                            }
                            if (!repetido) {
                                data.guardarCliente(c1);
                                JOptionPane.showMessageDialog(null, "Cliente " + c1.getNombre() + "añadido exitosamente.", "Válido", JOptionPane.INFORMATION_MESSAGE);
                                limpiarCampos();
                                vista.jbtEliminar.setEnabled(false);
                                vista.jbtGuardar.setEnabled(false);
                                guardadoExitoso = true;
                            }
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Debe ingresar un número en el campo DNI y Teléfono!", "Error", JOptionPane.ERROR_MESSAGE);
                }

                if (guardadoExitoso) {
                    desactivarCampos();
                    vista.jtxDNI.setEnabled(true);
                    vista.jbtGuardar.setEnabled(false);
                    vista.jbtEliminar.setEnabled(false);
                    cargarLista();
                }
            }
            cargarLista();
        }

        if (e.getSource() == vista.jbtNuevo) {
            buscar = false;
            activarCampos();
            limpiarCampos();
            vista.jbtGuardar.setEnabled(true);
            vista.jbtEliminar.setEnabled(false);
            vista.jlClientes.setModel(new DefaultListModel<>());
            cargarLista();
        }

        if (e.getSource() == vista.rbtnActivo || e.getSource() == vista.rbtnInactivo || e.getSource() == vista.rbtnTodos) {
            if (vista.rbtnActivo.isSelected()) {
                cargarLista();
            } else if (vista.rbtnInactivo.isSelected()) {
                cargarLista();
            } else if (vista.rbtnTodos.isSelected()) {
                cargarLista();
            }
        }
    }

    @Override
    public void focusGained(FocusEvent fe) {
    }

    @Override
    public void focusLost(FocusEvent fe) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getSource() == vista.jtxDNI) {
            char caracter = e.getKeyChar();
            if ((caracter < '0' || caracter > '9' && caracter != '\b')) {
                e.consume();
            }
            if (vista.jtxDNI.getText().length() >= 8 && caracter != '\b') {
                e.consume();
            }
        }

        if (e.getSource() == vista.jtxNombre) {
            char caracter = e.getKeyChar();
            if (!Character.isLetter(caracter) && caracter != ' ' && caracter != '\b') {
                e.consume();
            }
        }

        if (e.getSource() == vista.jtxTelefono) {
            char caracter = e.getKeyChar();
            if ((caracter < '0' || caracter > '9' && caracter != '\b')) {
                e.consume();
            }
            if (vista.jtxTelefono.getText().length() >= 16 && caracter != '\b') {
                e.consume();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == vista.jtxDNI) {
            DefaultListModel<String> modelo = new DefaultListModel<>();
            List<Cliente> clientes = data.listarClientes();
            String dni = vista.jtxDNI.getText().trim();
            for (Cliente aux : clientes) {
                String dniCliente = String.valueOf(aux.getDni());
                if (dniCliente.contains(dni)) {
                    modelo.addElement(aux.getNombre() + " / " + aux.getDni());
                }
            }
            vista.jlClientes.setModel(modelo);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if (!lse.getValueIsAdjusting()) {
            String clienteSeleccionado = vista.jlClientes.getSelectedValue();
            if (clienteSeleccionado != null) {
                seleccion = true;
                buscar = true;
                List<Cliente> clientes = data.listarClientes();
                for (Cliente aux : clientes) {
                    String nombreDni = aux.getNombre() + " / " + aux.getDni();
                    if (clienteSeleccionado.equalsIgnoreCase(nombreDni.trim())) {
                        codCliOriginal = aux.getCodCli();
                        System.out.println(codCliOriginal);
                        vista.jtxDNI.setText(String.valueOf(aux.getDni()));
                        vista.jtxNombre.setText(aux.getNombre());
                        vista.jtxTelefono.setText(String.valueOf(aux.getTelefono()));
                        vista.jdcFechaNac.setDate(java.util.Date.from(aux.getFechaNac().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        vista.jtxaAfecciones.setText(aux.getAfecciones());
                        if (aux.isEstado()) {
                            vista.jcheckbEstado.setSelected(true);
                        } else {
                            vista.jcheckbEstado.setSelected(false);
                        }
                        activarCampos();
                        vista.jbtGuardar.setEnabled(true);
                        vista.jbtEliminar.setEnabled(true);
                    }
                }
                seleccion = false;
            }
        }
    }

    public void limpiarCampos() {
        vista.jtxDNI.setText("");
        vista.jtxNombre.setText("");
        vista.jtxTelefono.setText("");
        vista.jdcFechaNac.setDate(null);
        vista.jtxaAfecciones.setText("");
        vista.jcheckbEstado.setSelected(false);
    }

    public void activarCampos() {
        vista.jtxDNI.setEnabled(true);
        vista.jtxNombre.setEnabled(true);
        vista.jtxTelefono.setEnabled(true);
        vista.jdcFechaNac.setEnabled(true);
        vista.jtxaAfecciones.setEnabled(true);
    }

    public void desactivarCampos() {
        vista.jtxDNI.setEnabled(false);
        vista.jtxNombre.setEnabled(false);
        vista.jtxTelefono.setEnabled(false);
        vista.jdcFechaNac.setEnabled(false);
        vista.jtxaAfecciones.setEnabled(false);
    }

    public void cargarLista() {
        DefaultListModel<String> modelo = new DefaultListModel<>();
        List<Cliente> clientes = new ArrayList<>();
        
        if (vista.rbtnActivo.isSelected()) {
            clientes = data.listarClientesActivos();
        }
        if (vista.rbtnInactivo.isSelected()) {
            clientes = data.listarClientesInactivos();
        }
        if (vista.rbtnTodos.isSelected()) {
            clientes = data.listarClientes();
        }
        
        for (Cliente aux : clientes) {
            modelo.addElement(aux.getNombre() + " / " + aux.getDni());
        }

        vista.jlClientes.setModel(modelo);
    }
}
