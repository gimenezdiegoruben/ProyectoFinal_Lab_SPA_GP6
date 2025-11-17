package ControladoresDeVistas;

import Modelos.Tratamiento;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import Vistas.VistasTratamiento;
import Persistencias_Conexion.TratamientoData;
import Vistas.Vista_MenuSpa;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;  
import javax.swing.ButtonGroup;

public class ControladorTratamiento implements ActionListener, KeyListener {

    private final VistasTratamiento vista;
    private final TratamientoData data;
    private final Vista_MenuSpa menu;
    private List<Tratamiento> tratamientos;
    private Tratamiento tratamientoSeleccionado;
    private DefaultTableModel modeloTabla;
    private ButtonGroup grupoFiltro;

    public ControladorTratamiento(VistasTratamiento vista, TratamientoData data, Vista_MenuSpa menu) {
        this.vista = vista;
        this.data = data;
        this.menu = menu;
        this.tratamientos = new ArrayList<>();
        
         modeloTabla = new DefaultTableModel(
                new Object[]{"Código", "Nombre", "Tipo", "Detalle","Productos", "Duración", "Costo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;//todas las celdas no editables
            }
        };
        vista.tblProducto.setModel(modeloTabla);//modelamso la tabla

        //Agrupamos radio buttons de filtro
        grupoFiltro = new ButtonGroup();
        grupoFiltro.add(vista.rbtnTodos);
        grupoFiltro.add(vista.rbtnActivo);
        grupoFiltro.add(vista.rbtnInactivo);

        //Eventos de los radio buttons
        vista.rbtnTodos.addActionListener(this);
        vista.rbtnActivo.addActionListener(this);
        vista.rbtnInactivo.addActionListener(this);
        
        //Filtro inicial: TODOS (o el que quieras)
        vista.rbtnTodos.setSelected(true); 
        
        cargarTabla(); 

        vista.btnGuardarTrat.addActionListener(this);
        vista.btnEliminar.addActionListener(this);
        vista.btnSalir.addActionListener(this);
        vista.btnNuevoTrat.addActionListener(this);

        vista.txtPrecio.addActionListener(this);
        vista.comboEspecialidad.addActionListener(this);
        vista.comboProducto.addActionListener(this);
        vista.txtNombre.addKeyListener(this);
        vista.txtPrecio.addKeyListener(this);

        vista.tblProducto.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {//al hacer 1 clic
                    cargarTratamientoDesdeFilaSeleccionada();
                }
            }
        });
        
        desactivarCampos();
    }

    public void iniciar() {
        menu.JDesktopPFondo.add(vista);
        vista.setVisible(true);
        menu.JDesktopPFondo.moveToFront(vista);
        vista.requestFocus();

        desactivarCampos();
    }

    public void desactivarCampos() {
        vista.txtNombre.setEnabled(false);
        vista.txtDetalle.setEnabled(false);
        vista.txtPrecio.setEnabled(false);
        vista.comboEspecialidad.setEnabled(false);
        vista.comboProducto.setEnabled(false);
        vista.txtDuracion.setEnabled(false);
        vista.jCheckBoxEstado.setEnabled(false);  
    }

    public void activarCampos() {
        vista.txtNombre.setEnabled(true);
        vista.txtDetalle.setEnabled(true);
        vista.txtPrecio.setEnabled(true);
        vista.comboEspecialidad.setEnabled(true);
        vista.comboProducto.setEnabled(true);
        vista.txtDuracion.setEnabled(true);
        vista.jCheckBoxEstado.setEnabled(true);    
    }

    public void despejarCampos() {
        vista.txtNombre.setText("");
        vista.txtDetalle.setText("");
        vista.txtPrecio.setText("");
        vista.txtDuracion.setText("");
        vista.comboProducto.setSelectedIndex(-1);
        vista.comboEspecialidad.setSelectedIndex(-1);
        vista.jCheckBoxEstado.setSelected(true); 
        tratamientoSeleccionado = null;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnGuardarTrat) {
            guardarTratamiento();
        }
       
        if (e.getSource() == vista.btnEliminar) {
            eliminarTratamiento();
        }
        
        if (e.getSource() == vista.btnSalir) {
            vista.dispose();
        }
       
        if (e.getSource() == vista.comboEspecialidad) {

        }
        if (e.getSource() == vista.comboProducto) {

        }
        if (e.getSource() == vista.btnNuevoTrat) {
            despejarCampos();
            activarCampos();
            vista.txtNombre.requestFocus();
        }
        
        if (e.getSource() == vista.rbtnActivo
                || e.getSource() == vista.rbtnInactivo
                || e.getSource() == vista.rbtnTodos) {
            actualizarTablaSegunFiltro();
        }
    }
    
    private void actualizarTablaSegunFiltro() {
        if (vista.rbtnActivo.isSelected()) {
            cargarTablaActivos();
        } else if (vista.rbtnInactivo.isSelected()) {
            cargarTablaInactivos();
        } else { 
            cargarTablaTodos();//rbtnTodos
        }
    }
    
    //Este es el que llaman guardar/eliminar, etc.
    private void cargarTabla() {
        actualizarTablaSegunFiltro();
    }

    private void cargarTablaActivos() {
        List<Tratamiento> lista = data.listarTratamientos();//activos estado 1
        cargarTablaDesdeLista(lista);
    }

    private void cargarTablaInactivos() {
        List<Tratamiento> lista = data.listarTratamientosInactivos(); //inactivos
        cargarTablaDesdeLista(lista);
    }

    private void cargarTablaTodos() {
        List<Tratamiento> lista = new ArrayList<>();
        lista.addAll(data.listarTratamientos()); //activos
        lista.addAll(data.listarTratamientosInactivos());
        cargarTablaDesdeLista(lista);
    }

    // Método para armar las filas de la tabla
    private void cargarTablaDesdeLista(List<Tratamiento> lista) {
        modeloTabla.setRowCount(0);
        this.tratamientos = lista;

        for (Tratamiento t : lista) {
            modeloTabla.addRow(new Object[]{
                t.getCodTratam(),
                t.getNombre(),
                t.getTipo(),
                t.getDetalle(),
                t.getProductos().isEmpty() ? "" : t.getProductos().get(0),
                t.getDuracion(),
                t.getCosto(),
                t.isEstado() ? "Activo" : "Inactivo"
            });
        }
    }
        
    private void cargarTratamientoDesdeFilaSeleccionada() {
        int fila = vista.tblProducto.getSelectedRow();
        if (fila == -1) {
            return; //nada seleccionado
        }


        Object valorId = modeloTabla.getValueAt(fila, 0);//1er column codTrat
        if (valorId == null) {
            return;
        }

        int codTratam;
        try {
            codTratam = Integer.parseInt(valorId.toString());
        } catch (NumberFormatException ex) {
            return;
        }

        Tratamiento t = data.buscarTratamientoPorCodigo(codTratam);
        if (t == null) {
            JOptionPane.showMessageDialog(vista,
                    "No se encontró el tratamiento seleccionado en la base de datos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        tratamientoSeleccionado = t;//actualizamos

        //Cargar datos en campos del formulario 
        vista.txtNombre.setText(t.getNombre());
        vista.txtDetalle.setText(t.getDetalle());
        vista.txtDuracion.setText(String.valueOf(t.getDuracion()));
        vista.txtPrecio.setText(String.valueOf((int) t.getCosto()));

        // Tipo , especialidad
        if (t.getTipo() != null) {
            vista.comboEspecialidad.setSelectedItem(t.getTipo());
        } else {
            vista.comboEspecialidad.setSelectedIndex(-1);
        }

        //Combo ´producto carga primer producto de la lista si existe
        if (t.getProductos() != null && !t.getProductos().isEmpty()) {
            String prod = t.getProductos().get(0);
            vista.comboProducto.setSelectedItem(prod);
        } else {
            vista.comboProducto.setSelectedIndex(-1);
        }
        
        vista.jCheckBoxEstado.setSelected(t.isEstado());

        activarCampos();
    }

    private void guardarTratamiento() {

        String nombre = vista.txtNombre.getText().trim();
        String tipo = (String) vista.comboEspecialidad.getSelectedItem();
        String detalle = vista.txtDetalle.getText().trim();
        String producto = (String) vista.comboProducto.getSelectedItem();
        String duraciontxt = vista.txtDuracion.getText().trim();
        String precioTxt = vista.txtPrecio.getText().trim();
        boolean estadoForm = vista.jCheckBoxEstado.isSelected();

        if (nombre.isEmpty() || tipo == null || detalle.isEmpty() || precioTxt.isEmpty() || duraciontxt.isEmpty()) {
            JOptionPane.showMessageDialog(null, "complete todos los campos.");
            return;
        }
        double costo;
        int duracion;

        try {
            costo = Double.parseDouble(precioTxt);
            duracion = Integer.parseInt(duraciontxt);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Duracion y precio deben ser un numeros");
            return;
        }

        if (tratamientoSeleccionado == null) {
            // Nueva alta
            Tratamiento t = new Tratamiento();
            t.setNombre(nombre);
            t.setTipo(tipo);
            t.setDetalle(detalle);
            t.setDuracion(duracion);
            t.setCosto(costo);
            t.setEstado(estadoForm);

            List<String> listaProductos = new ArrayList<>();
            if (producto != null && !producto.trim().isEmpty()) {
                listaProductos.add(producto);
            }
            t.setProductos(listaProductos);

            data.guardarTratamiento(t);//insert en bd
            JOptionPane.showMessageDialog(vista,
                    "Tratamiento guardado correctamente.");

        } else {
            //Si hay un tratamiento seleccionado, primero verificamos si hay cambios
            boolean cambio = false;

            if (!nombre.equals(tratamientoSeleccionado.getNombre())) cambio = true;
            if (!tipo.equals(tratamientoSeleccionado.getTipo())) cambio = true;
            if (!detalle.equals(tratamientoSeleccionado.getDetalle())) cambio = true;
            if (duracion != tratamientoSeleccionado.getDuracion()) cambio = true;
            if (Double.compare(costo, tratamientoSeleccionado.getCosto()) != 0) cambio = true;
            if (estadoForm != tratamientoSeleccionado.isEstado()) cambio = true;
            
            String productoOriginal = "";
            if (tratamientoSeleccionado.getProductos() != null
                    && !tratamientoSeleccionado.getProductos().isEmpty()) {
                productoOriginal = tratamientoSeleccionado.getProductos().get(0);
            }
            String productoNuevo = (producto == null) ? "" : producto;
            if (!productoNuevo.equals(productoOriginal)) cambio = true;

            if (!cambio) {
                JOptionPane.showMessageDialog(vista,
                        "No hay cambios para guardar.",
                        "Sin cambios",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int opcion = JOptionPane.showConfirmDialog(
                    vista,
                    "Se detectaron cambios en el tratamiento.\n¿Desea guardar las modificaciones?",
                    "Confirmar modificaciones",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }

            // Aplicar cambios sobre el objeto seleccionado
            tratamientoSeleccionado.setNombre(nombre);
            tratamientoSeleccionado.setTipo(tipo);
            tratamientoSeleccionado.setDetalle(detalle);
            tratamientoSeleccionado.setDuracion(duracion);
            tratamientoSeleccionado.setCosto(costo);
            tratamientoSeleccionado.setEstado(estadoForm);

            List<String> listaProductos = new ArrayList<>();
            if (producto != null && !producto.trim().isEmpty()) {
                listaProductos.add(producto);
            }
            tratamientoSeleccionado.setProductos(listaProductos);

            data.modificarTratamiento(tratamientoSeleccionado);//(UPDATE)
            JOptionPane.showMessageDialog(vista,
                    "Tratamiento modificado correctamente.");
        }

        despejarCampos();
        desactivarCampos();
        cargarTabla();
    }

    private void eliminarTratamiento() {
        if (tratamientoSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Primero busque un tratamiento para eliminar.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                vista,
                "¿Está seguro de eliminar (dar de baja) el tratamiento '" + tratamientoSeleccionado.getNombre() + "'?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            data.eliminarTratamiento(tratamientoSeleccionado.getCodTratam());
            JOptionPane.showMessageDialog(vista, "Tratamiento eliminado (dado de baja) correctamente.");

            despejarCampos();
            desactivarCampos();
            tratamientoSeleccionado = null;
            cargarTabla();
        }
    }

    private void cambiarEstadoTratamiento() {
        if (tratamientoSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Primero seleccione un tratamiento para cambiar su estado.");
            return;
        }

        boolean estadoActual = tratamientoSeleccionado.isEstado();
        boolean nuevoEstado = !estadoActual;

        data.cambiarEstadoTratamiento(tratamientoSeleccionado.getCodTratam(), nuevoEstado);
        tratamientoSeleccionado.setEstado(nuevoEstado);

        String msg = nuevoEstado ? "Tratamiento dado de ALTA." : "Tratamiento dado de BAJA.";
        JOptionPane.showMessageDialog(vista, msg);
        
        cargarTabla();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
