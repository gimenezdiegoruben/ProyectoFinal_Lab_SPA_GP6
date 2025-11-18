package ControladoresDeVistas;

import Modelos.Producto;
import Modelos.Tratamiento;
import Persistencias_Conexion.ProductoData;
import Persistencias_Conexion.TratamientoData;
import Vistas.VistaProductos;
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

/**
 * @author Grupo 6 Gimenez Diego Ruben Carlos German Mecias Giacomelli Tomas
 * Migliozzi Badani Urbani Jose
 *
 */
public class ControladorProducto implements ActionListener, KeyListener, MouseListener {

    private VistaProductos vista;
    private ProductoData data;
    private TratamientoData tratamientoData;
    private Vista_MenuSpa menu;
    private static DefaultTableModel modelo;
    private boolean buscar;
    private boolean verificado;
    private int codProductoSeleccionado;
    private int codTratamientoSeleccionado;

    public ControladorProducto(VistaProductos vista, ProductoData data, TratamientoData tratamientoData, Vista_MenuSpa menu) {
        this.vista = vista;
        this.data = data;
        this.tratamientoData = tratamientoData;
        this.menu = menu;

        vista.jbtEliminar.addActionListener(this);
        vista.jbtGuardar.addActionListener(this);
        vista.jbtNuevo.addActionListener(this);
        vista.jbtSalir.addActionListener(this);
        vista.jbtVerificar.addActionListener(this);
        vista.jtStock.addKeyListener(this);
        this.vista.jtbProductos.addMouseListener(this);

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
        actualizarTabla();
    }

    public void desactivarCampos() {
        vista.jtDescripcion.setEnabled(false);
        vista.jtNroTratamiento.setEnabled(false);
        vista.jtPrecio.setEnabled(false);
        vista.jtStock.setEnabled(false);
    }

    public void activarCampos() {
        vista.jtDescripcion.setEnabled(true);
        vista.jtNroTratamiento.setEnabled(true);
        vista.jtPrecio.setEnabled(true);
        vista.jtStock.setEnabled(true);
    }

    public void limpiarCampos() {
        vista.jtDescripcion.setText("");
        vista.jtNroTratamiento.setText("");
        vista.jtPrecio.setText("");
        vista.jtStock.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.jbtNuevo) {
            nuevoProducto();
        } else if (e.getSource() == vista.jbtVerificar) {
            verificarProducto();
        } else if (e.getSource() == vista.jbtGuardar) {
            guardarProducto();
        } else if (e.getSource() == vista.jbtEliminar) {
            eliminarProducto();
        } else if (e.getSource() == vista.jbtSalir) {
            vista.dispose();
        }
    }

    public void nuevoProducto() {
        buscar = false;
        activarCampos();
        limpiarCampos();
        vista.jbtGuardar.setEnabled(true);
        vista.jbtEliminar.setEnabled(true);
    }

    public void verificarProducto() {
        try {
            if (vista.jtNroTratamiento.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese un número de tratamiento antes de verificar!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                int codTratam = Integer.parseInt(vista.jtNroTratamiento.getText().trim());
                Tratamiento t1 = tratamientoData.buscarTratamientoPorCodigo(codTratam);
                JOptionPane.showMessageDialog(null, "Número de tratamiento verificado con éxito, complete los demás campos.", "Válido", JOptionPane.INFORMATION_MESSAGE);
                if (t1 == null) {
                    JOptionPane.showMessageDialog(null, "El número de tratamiento que has ingresado no existe", "Error", JOptionPane.ERROR_MESSAGE);
                    verificado = false;
                } else {
                    verificado = true;
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Se ingreso un número inválido en Nro Tratamiento!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void guardarProducto() {
        if (vista.jtNroTratamiento.getText().trim().isEmpty() || vista.jtDescripcion.getText().trim().isEmpty() || vista.jtStock.getText().trim().isEmpty() || vista.jtStock.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe rellenar todos los campos!!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                if (buscar) {
                    int codTratamientoNuevo = Integer.parseInt(vista.jtNroTratamiento.getText().trim());
                    Tratamiento t1 = tratamientoData.buscarTratamientoPorCodigo(codTratamientoNuevo);
                    if (t1 == null){
                        JOptionPane.showMessageDialog(null, "El número de tratamiento no existe", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Producto p1 = new Producto(vista.jtDescripcion.getText().trim(), Double.parseDouble(vista.jtPrecio.getText().trim()), Integer.parseInt(vista.jtStock.getText().trim()), t1);
                    Producto p2 = data.buscarProductoPorCod(codProductoSeleccionado);
                    if (p1.getDescripcion().equals(p2.getDescripcion()) && p1.getPrecio() == p2.getPrecio() && p1.getStock() == p2.getStock() && Integer.parseInt(vista.jtNroTratamiento.getText().trim()) == p2.getTratamiento().getCodTratam()) {
                        JOptionPane.showMessageDialog(null, "Haz algún cambio antes de guardar!", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        List<Producto> productos = data.listarProductos();
                        for (Producto aux : productos) {
                            if (aux.getDescripcion().equals(p1.getDescripcion()) && aux.getPrecio() == p1.getPrecio() && aux.getStock() == p1.getStock() && aux.getTratamiento().getCodTratam() == Integer.parseInt(vista.jtNroTratamiento.getText().trim())){
                                JOptionPane.showMessageDialog(null, "El producto que intentas guardar ya está cargada en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        p1.setCodProducto(p2.getCodProducto());
                        data.modificarProducto(p1);
                        JOptionPane.showMessageDialog(null, "Producto modificado con éxito");
                        limpiarCampos();
                        buscar = false;
                        vista.jbtGuardar.setEnabled(false);
                        vista.jbtEliminar.setEnabled(false);
                        actualizarTabla();
                    }
                } else {
                    if (!verificado) {
                        JOptionPane.showMessageDialog(null, "Ingrese un número en N° Tratamiento y verifique antes de guardar un producto!", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        Tratamiento t1 = tratamientoData.buscarTratamientoPorCodigo(Integer.parseInt(vista.jtNroTratamiento.getText()));
                        Producto p1 = new Producto(vista.jtDescripcion.getText().trim(), Double.parseDouble(vista.jtPrecio.getText().trim()), Integer.parseInt(vista.jtStock.getText().trim()), t1);
                        List<Producto> productos = data.listarProductos();
                        for (Producto aux : productos) {
                            if (aux.getDescripcion().equals(p1.getDescripcion()) && aux.getPrecio() == p1.getPrecio() && aux.getStock() == p1.getStock() && aux.getTratamiento().getCodTratam() == p1.getTratamiento().getCodTratam()) {
                                JOptionPane.showMessageDialog(null, "El producto que intentas guardar ya está cargada en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        data.guardarProducto(p1);
                        JOptionPane.showMessageDialog(null, "Producto agregado con éxito.", "Válido", JOptionPane.INFORMATION_MESSAGE);
                        limpiarCampos();
                        vista.jbtGuardar.setEnabled(false);
                        vista.jbtEliminar.setEnabled(false);
                        actualizarTabla();
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Debe ingresar un número válido en los campos 'Precio' y 'Stock'", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void eliminarProducto() {
        Producto p1 = data.buscarProductoPorCod(codProductoSeleccionado);
        if (p1 != null) {
            int confirmacion = JOptionPane.showConfirmDialog(null, "¿Estás seguro de eliminar el Producto " + p1.getDescripcion() + "?", "Confirmación de eliminación", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                data.eliminarProducto(codProductoSeleccionado);
                JOptionPane.showMessageDialog(null, "Producto " + p1.getDescripcion() + " eliminado con éxito.", "Válido", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                vista.jbtEliminar.setEnabled(false);
                vista.jbtGuardar.setEnabled(false);
                actualizarTabla();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ha ocurrido algo inesperado", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void cargarTabla() {
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

        vista.jtbProductos.setModel(modelo);
        vista.jtbProductos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        actualizarTabla();

    }

    public void actualizarTabla() {
        modelo.setRowCount(0);
        List<Producto> productos = data.listarProductos();
        for (Producto aux : productos) {
            Object fila[] = new Object[5];
            fila[0] = aux.getCodProducto();
            fila[1] = aux.getDescripcion();
            fila[2] = aux.getPrecio();
            fila[3] = aux.getStock();
            fila[4] = aux.getTratamiento().getCodTratam();
            modelo.addRow(fila);
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {

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
        if (me.getSource() == vista.jtbProductos) {
            int fila = vista.jtbProductos.getSelectedRow();
            if (fila != -1) {
                Producto p1 = data.buscarProductoPorCod(Integer.parseInt(vista.jtbProductos.getValueAt(fila, 0).toString()));
                vista.jtNroTratamiento.setText(String.valueOf(p1.getTratamiento().getCodTratam()));
                vista.jtDescripcion.setText(p1.getDescripcion());
                vista.jtPrecio.setText(String.valueOf(p1.getPrecio()));
                vista.jtStock.setText(String.valueOf(p1.getStock()));

                vista.jbtEliminar.setEnabled(true);
                vista.jbtGuardar.setEnabled(true);
                activarCampos();
                buscar = true;
                codProductoSeleccionado = p1.getCodProducto();
                codTratamientoSeleccionado = p1.getTratamiento().getCodTratam();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mouseExited(MouseEvent me) {

    }

}
