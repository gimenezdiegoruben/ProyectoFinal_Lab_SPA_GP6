package ControladoresDeVistas;

import Modelos.Consultorio;
import Persistencias_Conexion.ConsultorioData;
import Vistas.VistaConsultorio;
import Vistas.Vista_MenuSpa;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Grupo 6 Gimenez Diego Ruben Carlos German Mecias Giacomelli Tomas
 * Migliozzi Badani Urbani Jose
 *
 */
public class ControladorConsultorio implements ActionListener, FocusListener, KeyListener, ListSelectionListener {

    private final VistaConsultorio vista;
    private final ConsultorioData data;
    private final Vista_MenuSpa menu;

    private boolean buscar = false;
    private boolean seleccion = false;
    private int consultorioSeleccionado;

    public ControladorConsultorio(VistaConsultorio vista, ConsultorioData data, Vista_MenuSpa menu) {
        this.vista = vista;
        this.data = data;
        this.menu = menu;

        vista.jbtSalir.addActionListener(this);
        vista.jbtEliminar.addActionListener(this);
        vista.jbtGuardar.addActionListener(this);
        vista.jbtNuevo.addActionListener(this);
        vista.jlConsultorios.addListSelectionListener(this);
        vista.jtxNroConsultorio.addKeyListener(this);
    }

    public void iniciar() {
        menu.JDesktopPFondo.add(vista);
        vista.setVisible(true);
        menu.JDesktopPFondo.moveToFront(vista);
        vista.requestFocus();

        desactivarCampos();
        vista.jbtGuardar.setEnabled(false);
        vista.jbtEliminar.setEnabled(false);
        vista.jlConsultorios.setModel(new DefaultListModel<>());
        cargarLista();
        cargarComboBox();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == vista.jbtSalir) {
            vista.dispose();
        }

        if (e.getSource() == vista.jbtEliminar) {
            Consultorio c1 = data.buscarConsultorio(consultorioSeleccionado);
            if (c1.getNroConsultorio() == consultorioSeleccionado && c1.getEquipamiento().trim().equalsIgnoreCase(vista.jtxaEquipamiento.getText()) && c1.getApto().trim().equalsIgnoreCase(vista.jcbApto.getSelectedItem().toString())) {
                int confirmacion = JOptionPane.showConfirmDialog(null, "¿Estás seguro de eliminar el Consultorio nro " + c1.getNroConsultorio() + "?", "Confirmación de eliminación", JOptionPane.YES_NO_OPTION);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    data.eliminarConsultorio(Integer.parseInt(vista.jtxNroConsultorio.getText().trim()));
                    JOptionPane.showMessageDialog(null, "Consultorio nro " + c1.getNroConsultorio() + " eliminado con éxito", "Válido", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    vista.jbtEliminar.setEnabled(false);
                }
            } else {
                JOptionPane.showMessageDialog(null, "El consultorio que intentas eliminar no existe o ha sido modificado sin ser guardado", "Error", JOptionPane.ERROR_MESSAGE);
            }
            desactivarCampos();
            cargarLista();
        }

        if (e.getSource() == vista.jbtGuardar) {
            boolean repetido = false;
            if (vista.jtxaEquipamiento.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Indique el equipamiento que tiene el consultorio!!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    if (buscar) {
                        Consultorio c1 = new Consultorio(Integer.parseInt(vista.jtxNroConsultorio.getText().trim()), 0, vista.jtxaEquipamiento.getText().trim(), vista.jcbApto.getSelectedItem().toString());
                        Consultorio c2 = data.buscarConsultorio(consultorioSeleccionado);
                        c1.setNroConsultorio(c2.getNroConsultorio());
                        data.modificarConsultorio(c1);
                        JOptionPane.showMessageDialog(null, "Consultorio nro " + c1.getNroConsultorio() + " modificado con éxito.", "Válido", JOptionPane.INFORMATION_MESSAGE);
                        limpiarCampos();
                        buscar = false;
                        vista.jbtEliminar.setEnabled(false);
                    } else {
                        Consultorio c1 = new Consultorio(Integer.parseInt(vista.jtxNroConsultorio.getText().trim()), 0, vista.jtxaEquipamiento.getText().trim(), vista.jcbApto.getSelectedItem().toString());
                        List<Consultorio> consultorios = data.listarConsultorios();
                        for (Consultorio aux : consultorios) {
                            if (aux.getNroConsultorio() == c1.getNroConsultorio()) {
                                JOptionPane.showMessageDialog(null, "El Consultorio que intentas guardar ya ha sido creado", "Consultorio repetido", JOptionPane.ERROR_MESSAGE);
                                vista.jbtEliminar.setEnabled(false);
                                repetido = true;
                            }
                        }
                        if (!repetido) {
                            data.nuevoConsultorio(c1);
                            JOptionPane.showMessageDialog(null, "Consultorio añadido con éxito.", "Válido", JOptionPane.INFORMATION_MESSAGE);
                            limpiarCampos();
                            vista.jbtEliminar.setEnabled(false);
                            desactivarCampos();
                            cargarLista();
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Número de consultorio incorrecto.", "Error", JOptionPane.INFORMATION_MESSAGE);
                    desactivarCampos();
                    cargarLista();
                }
            }
        }

        if (e.getSource() == vista.jbtNuevo) {
            buscar = false;
            activarCampos();
            limpiarCampos();
            vista.jbtGuardar.setEnabled(true);
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
        if (e.getSource() == vista.jtxNroConsultorio) {
            char caracter = e.getKeyChar();
            if ((caracter < '0' || caracter > '9' && caracter != '\b')) {
                e.consume();
            }
            if (vista.jtxNroConsultorio.getText().length() >= 2 && caracter != '\b') {
                e.consume();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {

        if (!lse.getValueIsAdjusting()) {
            String consultorio = vista.jlConsultorios.getSelectedValue();
            if (consultorio != null) {
                seleccion = true;
                buscar = true;
                List<Consultorio> listaConsultorios = data.listarConsultorios();
                for (Consultorio aux : listaConsultorios) {
                    String consultorioNro = "Consultorio " + aux.getNroConsultorio();
                    if (consultorio.equalsIgnoreCase(consultorioNro)) {
                        consultorioSeleccionado = aux.getNroConsultorio();
                        vista.jtxNroConsultorio.setText(String.valueOf(aux.getNroConsultorio()));
                        vista.jtxaEquipamiento.setText(aux.getEquipamiento());
                        vista.jcbApto.setSelectedItem(aux.getApto());
                        activarCampos();
                        vista.jtxNroConsultorio.setEditable(false);
                        vista.jbtGuardar.setEnabled(true);
                        vista.jbtEliminar.setEnabled(true);
                    }
                }
            }
            seleccion = false;
        }
    }

    public void limpiarCampos() {
        vista.jtxaEquipamiento.setText("");
        vista.jcbApto.setSelectedIndex(0);
        vista.jtxNroConsultorio.setText("");
    }

    public void activarCampos() {
        vista.jtxaEquipamiento.setEnabled(true);
        vista.jtxaEquipamiento.setEditable(true);
        vista.jcbApto.setEnabled(true);
        vista.jtxNroConsultorio.setEnabled(true);
        vista.jtxNroConsultorio.setEditable(true);
    }

    public void desactivarCampos() {
        vista.jtxaEquipamiento.setEnabled(false);
        vista.jcbApto.setEnabled(false);
        vista.jtxNroConsultorio.setEnabled(false);
    }

    public void cargarLista() {
        DefaultListModel<String> modelo = new DefaultListModel<>();
        List<Consultorio> consultorios = data.listarConsultorios();

        for (Consultorio aux : consultorios) {
            modelo.addElement("Consultorio " + aux.getNroConsultorio());
        }

        vista.jlConsultorios.setModel(modelo);
    }

    public void cargarComboBox() {
        vista.jcbApto.removeAllItems();
        vista.jcbApto.addItem("Estética facial");
        vista.jcbApto.addItem("Masajes corporales");
        vista.jcbApto.addItem("Tratamientos corporales");
        vista.jcbApto.addItem("Relajación");
        vista.jcbApto.addItem("Hidroterapia");
        vista.jcbApto.addItem("Fitness");
    }
}
