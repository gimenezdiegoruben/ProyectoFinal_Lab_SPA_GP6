package ControladoresDeVistas;

import Modelos.Cliente;
import Persistencias_Conexion.ClienteData;
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
import java.util.List;
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
    private final Vista_MenuSpa menu;

    private boolean buscar = false;
    private boolean seleccion = false;
    private long dniOriginal;

    public ControladorCliente(VistaCliente vista, ClienteData data, Vista_MenuSpa menu) {
        this.vista = vista;
        this.data = data;
        this.menu = menu;
        // Eventos
        vista.jbtSalir.addActionListener(this);
        vista.jbtEliminar.addActionListener(this);
        vista.jbtGuardar.addActionListener(this);
        vista.jbtNuevo.addActionListener(this);
        vista.jtxDNI.addKeyListener(this);
        vista.jlClientes.addListSelectionListener(this);
        vista.jtxDNI.addKeyListener(this);
        vista.jtxNombre.addKeyListener(this);
        vista.jtxTelefono.addKeyListener(this);
        vista.jtxaAfecciones.addKeyListener(this);
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
                int dni = Integer.parseInt(vista.jtxDNI.getText().trim());
                Cliente c1 = data.buscarPorDni(dni);
                if (c1 != null) {
                    int confirmacion = JOptionPane.showConfirmDialog(null, "¿Estás seguro de eliminar al Cliente " + c1.getNombre() + "?", "Confirmación de eliminación", JOptionPane.YES_NO_OPTION);
                    if (confirmacion == JOptionPane.YES_OPTION) {
                        data.eliminarCliente(dni);
                        JOptionPane.showMessageDialog(null, "Cliente " + c1.getNombre() + " eliminado con éxito", "Válido", JOptionPane.INFORMATION_MESSAGE);
                        limpiarCampos();
                        vista.jbtEliminar.setEnabled(false);
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
                            Cliente c2 = data.buscarPorDni(dniOriginal);
                            c1.setCodCli(c2.getCodCli());
                            data.modificarCliente(c1);
                            JOptionPane.showMessageDialog(null, "El cliente " + c2.getNombre() + " / DNI: " + c2.getDni() + " Teléfono: " + c2.getTelefono() + " Edad: " + c2.getEdad() + " Afecciones: " + c2.getAfecciones() + " Estado: " + (c2.isEstado() ? "Activo" : "Inactivo")
                                    + " ha sido modificado a: " + c1.getNombre() + " / DNI: " + c1.getDni() + " Teléfono: " + c1.getTelefono() + " Edad: " + c1.getEdad() + " Afecciones: " + c1.getAfecciones() + " Estado: " + (c1.isEstado() ? "Activo" : "Inactivo"));
                            limpiarCampos();
                            buscar = false;
                            vista.jbtEliminar.setEnabled(false);
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
            vista.jlClientes.setModel(new DefaultListModel<>());
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
            if (vista.jtxTelefono.getText().length() >= 10 && vista.jtxTelefono.getText().length() <= 16 && caracter != '\b') {
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
                        dniOriginal = aux.getDni();
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
        List<Cliente> clientes = data.listarClientes();

        for (Cliente aux : clientes) {
            modelo.addElement(aux.getNombre() + " / " + aux.getDni());
        }

        vista.jlClientes.setModel(modelo);
    }
}
