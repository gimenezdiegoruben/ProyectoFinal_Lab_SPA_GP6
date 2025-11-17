package ControladoresDeVistas;

import Modelos.Instalacion;
import Modelos.Sesion;
import Persistencias_Conexion.InstalacionData;
import Persistencias_Conexion.SesionData;
import Vistas.VistaInstalacion;
import Vistas.Vista_MenuSpa;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
public class ControladorInstalacion implements ActionListener, KeyListener, MouseListener {

    private VistaInstalacion vista;
    private InstalacionData instalacionData;
    private SesionData sesionData;
    private Vista_MenuSpa menu;
    private static DefaultTableModel modelo;
    private boolean buscar;
    private int codInstalacionSeleccionado;

    public ControladorInstalacion(VistaInstalacion vista, InstalacionData data, SesionData sesionData, Vista_MenuSpa menu) {
        this.vista = vista;
        this.instalacionData = data;
        this.sesionData = sesionData;
        this.menu = menu;
        iniciarListenerBotones();
        vista.jtPrecio.addKeyListener(this);
    }

    public void iniciar() {
        menu.JDesktopPFondo.add(vista);
        vista.setVisible(true);
        menu.JDesktopPFondo.moveToFront(vista);
        vista.requestFocus();
        
        desactivarCampos();
        vista.jbtGuardar.setEnabled(false);
        vista.jbtEliminar.setEnabled(false);
        cargarTabla();
    }

    private void iniciarListenerBotones() {
        vista.jbtGuardar.addActionListener(this);
        vista.jbtNuevo.addActionListener(this);
        vista.jbtSalir.addActionListener(this);
        vista.jbtEliminar.addActionListener(this);
        this.vista.jtbInstalaciones.addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.jbtNuevo) {
            nuevaInstalacion();
        } else if (e.getSource() == vista.jbtGuardar) {
            guardarInstalacion();
        } else if (e.getSource() == vista.jbtEliminar) {
            eliminarInstalacion();
        } else if (e.getSource() == vista.jbtSalir) {
            vista.dispose();
        }
    }

    private void nuevaInstalacion() {
        buscar = false;
        activarCampos();
        limpiarCampos();
        vista.jbtGuardar.setEnabled(true);
        vista.jbtEliminar.setEnabled(true);
    }

    private void guardarInstalacion() {
        if (vista.jtNombre.getText().trim().isEmpty() || vista.jtDetalle.getText().trim().isEmpty() || vista.jtPrecio.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe rellenar todos los campos!!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                if (buscar) {
                    Instalacion i1 = new Instalacion(vista.jtNombre.getText().trim(), vista.jtDetalle.getText().trim(), Double.parseDouble(vista.jtPrecio.getText().trim()), vista.jchbEstado.isSelected());
                    Instalacion i2 = instalacionData.buscarInstalacionPorCod(codInstalacionSeleccionado);
                    if (i1.getNombre().equals(i2.getNombre()) && i1.getDetalleUso().equals(i2.getDetalleUso()) && i1.getPrecio() == i2.getPrecio() && i1.isEstado() == i2.isEstado()) {
                        JOptionPane.showMessageDialog(null, "Haz algún cambio antes de guardar!", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        List<Instalacion> instalaciones = instalacionData.listarTodasInstalaciones();
                        for (Instalacion aux : instalaciones) {
                            if (aux.getNombre().equals(i1.getNombre()) && aux.getDetalleUso().equals(i1.getDetalleUso()) && aux.getPrecio() == i1.getPrecio() && aux.isEstado() == i1.isEstado()) {
                                JOptionPane.showMessageDialog(null, "La instalacion que intentas guardar ya está cargada en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        i1.setCodInstal(i2.getCodInstal());
                        instalacionData.modificarInstalacion(i1);
                        JOptionPane.showMessageDialog(null, "Instalacion " + i1.getNombre() + " modificada con éxito");
                        limpiarCampos();
                        buscar = false;
                        vista.jbtEliminar.setEnabled(false);
                        vista.jbtGuardar.setEnabled(false);
                        actualizarTabla();
                    }
                } else {
                    Instalacion i1 = new Instalacion(vista.jtNombre.getText().trim(), vista.jtDetalle.getText().trim(), Double.parseDouble(vista.jtPrecio.getText().trim()), vista.jchbEstado.isSelected());
                    List<Instalacion> instalaciones = instalacionData.listarTodasInstalaciones();
                    for (Instalacion aux : instalaciones) {
                        if (aux.getNombre().equals(i1.getNombre()) && aux.getDetalleUso().equals(i1.getDetalleUso()) && aux.getPrecio() == i1.getPrecio() && aux.isEstado() == i1.isEstado()) {
                            JOptionPane.showMessageDialog(null, "La instalacion que intentas guardar ya está cargada en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    instalacionData.crearInstalacion(i1);
                    JOptionPane.showMessageDialog(null, "Instalación cargada con éxito", "Válido", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    vista.jbtEliminar.setEnabled(false);
                    vista.jbtGuardar.setEnabled(false);
                    actualizarTabla();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Debe ingresar un número válido en el campo 'Precio'", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    private void eliminarInstalacion() {
        Instalacion i1 = instalacionData.buscarInstalacionPorCod(codInstalacionSeleccionado);
        List <Sesion> sesiones = sesionData.listarSesionesPorCodInstal(codInstalacionSeleccionado);
        if (!sesiones.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No se puede eliminar la instalación, ya que tiene sesiones vinculadas", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (i1 != null) {
            int confirmacion = JOptionPane.showConfirmDialog(null, "¿Estás seguro de eliminar la instalación " + i1.getNombre() + "?", "Confirmación de eliminación", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                instalacionData.eliminarInstalacion(codInstalacionSeleccionado);
                JOptionPane.showMessageDialog(null,"Instalacion " + i1.getNombre() + " eliminada con éxito.", "Válido", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                vista.jbtEliminar.setEnabled(false);
                vista.jbtGuardar.setEnabled(false);
                actualizarTabla();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ha ocurrido algo inesperado", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getSource() == vista.jtPrecio) {
            char caracter = e.getKeyChar();
            if ((caracter < '0' || caracter > '9' && caracter != '\b')) {
                e.consume();
            }
            if (vista.jtPrecio.getText().length() >= 8 && caracter != '\b') {
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
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (me.getSource() == vista.jtbInstalaciones) {
            int fila = vista.jtbInstalaciones.getSelectedRow();
            if (fila != 1) {
                Instalacion i1 = instalacionData.buscarInstalacionPorCod(Integer.parseInt(vista.jtbInstalaciones.getValueAt(fila, 0).toString()));
                vista.jtNombre.setText(i1.getNombre());
                vista.jtDetalle.setText(i1.getDetalleUso());
                vista.jtPrecio.setText(String.valueOf(i1.getPrecio()));
                vista.jchbEstado.setSelected(i1.isEstado());
                
                vista.jbtEliminar.setEnabled(true);
                vista.jbtGuardar.setEnabled(true);
                activarCampos();
                buscar = true;
                codInstalacionSeleccionado = i1.getCodInstal();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {

    }

    public void cargarTabla() {
        modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelo.addColumn("Nº Instalación");
        modelo.addColumn("Nombre");
        modelo.addColumn("Detalle de uso");
        modelo.addColumn("Precio(30min)");
        modelo.addColumn("Estado");

        vista.jtbInstalaciones.setModel(modelo);
        vista.jtbInstalaciones.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        actualizarTabla();

    }

    public void limpiarCampos() {
        vista.jtNombre.setText("");
        vista.jtDetalle.setText("");
        vista.jtPrecio.setText("");
        vista.jchbEstado.setSelected(false);
    }

    public void activarCampos() {
        vista.jtNombre.setEnabled(true);
        vista.jtDetalle.setEnabled(true);
        vista.jtPrecio.setEnabled(true);
        vista.jchbEstado.setEnabled(true);
    }

    public void desactivarCampos() {
        vista.jtNombre.setEnabled(false);
        vista.jtDetalle.setEnabled(false);
        vista.jtPrecio.setEnabled(false);
        vista.jchbEstado.setEnabled(false);
    }
    
        
    public void actualizarTabla() {
        modelo.setRowCount(0);
        List<Instalacion> instalaciones = instalacionData.listarTodasInstalaciones();
        for (Instalacion aux : instalaciones) {
            Object fila[] = new Object[5];
            fila[0] = aux.getCodInstal();
            fila[1] = aux.getNombre();
            fila[2] = aux.getDetalleUso();
            fila[3] = aux.getPrecio();
            fila[4] = aux.isEstado() ? "Activo" : "Inactivo";
            modelo.addRow(fila);
        }
    }
}
