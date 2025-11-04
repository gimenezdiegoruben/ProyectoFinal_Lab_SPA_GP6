package ControladoresDeVistas;

import Modelos.Empleado;
import Persistencias_Conexion.EmpleadoData;
import Vistas.Vista_MenuSpa;
import Vistas.VistaEmpleados;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */
public class ControladorEmpleados implements ActionListener, KeyListener {

    private final VistaEmpleados vista;
    private final EmpleadoData empleadoData;
    private final Vista_MenuSpa menu;
    private int idEmpleado;

    private DefaultTableModel modeloTabla;
    private ButtonGroup grupoEstado;

    public ControladorEmpleados(Vista_MenuSpa menu, VistaEmpleados vista, EmpleadoData data) {
        this.vista = vista;
        this.empleadoData = data;
        this.menu = menu;

        //KeyListener de campos
        vista.jtxDocumento.addKeyListener(this);
        vista.jtxMatricula.addKeyListener(this);
        vista.jtxtNombre.addKeyListener(this);
        vista.jtxtApellido.addKeyListener(this);
        vista.jtxTelefono.addKeyListener(this);

        //ActionListeners
        vista.jbtBuscar.addActionListener(this);
        vista.jbtSalir.addActionListener(this);
        vista.jbtNuevo.addActionListener(this);
        vista.jbtEliminar.addActionListener(this);
        vista.jbtGuardar.addActionListener(this);
        vista.jbtAltaBaja.addActionListener(this);
        vista.rbtnActivo1.addActionListener(this);
        vista.rbtnInactivo1.addActionListener(this);
        vista.cmbEspecialidadBuscar.addActionListener(this);
        vista.jCB_PuestoParaTabla.addActionListener(this);
        vista.jComboBoxPuesto.addActionListener(this);

        //los radiob p la tabla
        grupoEstado = new ButtonGroup();
        grupoEstado.add(vista.rbtnActivo1);
        grupoEstado.add(vista.rbtnInactivo1);
        vista.rbtnActivo1.setSelected(true);

        // Listener para click en tabla
        vista.tbEmpleados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    cargarEmpleadoDesdeTabla();
                }
            }
        });

        configurarTabla();
        cargarEspecialidades();
        configurarComboboxPuestoFiltro();
        inactivarCampos();
        controlarVisibilidadEspecialidad();
        actualizarTabla();
    }

    public void iniciar() {
        vista.setTitle("Gestión de Empleados");
        actualizarTabla();
    }

    private void configurarTabla() {
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; //tabla no editable
            }
        };

        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("DNI");
        modeloTabla.addColumn("Apellido");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Puesto");
        modeloTabla.addColumn("Matrícula");
        modeloTabla.addColumn("Especialidad");
        modeloTabla.addColumn("Teléfono");
        modeloTabla.addColumn("Estado");

        vista.tbEmpleados.setModel(modeloTabla);

        //Ocultar columna ID 
        vista.tbEmpleados.getColumnModel().getColumn(0).setMinWidth(0);
        vista.tbEmpleados.getColumnModel().getColumn(0).setMaxWidth(0);
        vista.tbEmpleados.getColumnModel().getColumn(0).setWidth(0);
    }

    private void configurarComboboxPuestoFiltro() {
        vista.jCB_PuestoParaTabla.removeAllItems();
        vista.jCB_PuestoParaTabla.addItem("Todas"); //iniciada por defecto
        vista.jCB_PuestoParaTabla.addItem("Masajista");
        vista.jCB_PuestoParaTabla.addItem("Estilista");
        vista.jCB_PuestoParaTabla.addItem("Estetisista");
        vista.jCB_PuestoParaTabla.addItem("Recepcionista");
        vista.jCB_PuestoParaTabla.addItem("Técnico");
        vista.jCB_PuestoParaTabla.addItem("Chofer");
        vista.jCB_PuestoParaTabla.addItem("Maestranza");
        vista.jCB_PuestoParaTabla.addItem("Administrativo");
        vista.jCB_PuestoParaTabla.addItem("RRHH");
        vista.jCB_PuestoParaTabla.addItem("Jefe");
    }

    private void cargarEspecialidades() {
        vista.cmbEspecialidadBuscar.removeAllItems();
        vista.cmbEspecialidadBuscar.addItem("Todas"); // Opción por defecto
        List<String> especialidades = empleadoData.obtenerEspecialidades();
        for (String esp : especialidades) {
            vista.cmbEspecialidadBuscar.addItem(esp);
        }
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0); // Limpiar tabla

        boolean estadoBuscar = vista.rbtnActivo1.isSelected();
        String especialidad = (String) vista.cmbEspecialidadBuscar.getSelectedItem();
        String puesto = (String) vista.jCB_PuestoParaTabla.getSelectedItem();

        List<Empleado> empleados = empleadoData.listarEmpleados(estadoBuscar, especialidad);

        // CORRECCIÓN: Filtrar por puesto solo si no es "Todas"
        if (puesto != null && !puesto.equals("Todas") && !puesto.isEmpty()) {
            empleados = empleados.stream()
                    .filter(emp -> puesto.equals(emp.getPuesto()))
                    .collect(Collectors.toList());
        }

        for (Empleado emp : empleados) {
            Object[] fila = new Object[9];
            fila[0] = emp.getIdEmpleado();
            fila[1] = emp.getDni();
            fila[2] = emp.getApellido();
            fila[3] = emp.getNombre();
            fila[4] = emp.getPuesto();
            fila[5] = emp.getMatricula() != null ? emp.getMatricula() : "";
            fila[6] = emp.getEspecialidad() != null ? emp.getEspecialidad() : "";
            fila[7] = emp.getTelefono();
            fila[8] = emp.isEstado() ? "Activo" : "Inactivo";
            modeloTabla.addRow(fila);
        }

        //DEBUG: Mostrar mensaje si no hay resultados
        if (empleados.isEmpty()) {
            System.out.println("No se encontraron empleados con los filtros aplicados.");
        }
    }

    private void cargarEmpleadoDesdeTabla() {
        int filaSeleccionada = vista.tbEmpleados.getSelectedRow();
        if (filaSeleccionada != -1) {
            int id = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
            Empleado empleado = buscarEmpleadoPorId(id);

            if (empleado != null) {
                idEmpleado = empleado.getIdEmpleado();
                vista.jtxDocumento.setText(String.valueOf(empleado.getDni()));
                vista.jtxtApellido.setText(empleado.getApellido());
                vista.jtxtNombre.setText(empleado.getNombre());
                vista.jtxTelefono.setText(empleado.getTelefono());

                vista.jComboBoxPuesto.setSelectedItem(empleado.getPuesto());
                //vista.jCB_PuestoParaTabla.setSelectedItem(empleado.getPuesto());

                vista.jtxMatricula.setText(empleado.getMatricula() != null ? empleado.getMatricula() : "");

                //Manejamos especialidad según el puesto
                if ("Masajista".equals(empleado.getPuesto()) && empleado.getEspecialidad() != null) {
                    vista.jComboBoxEspecialidad.setSelectedItem(empleado.getEspecialidad());
                } else {
                    vista.jComboBoxEspecialidad.setSelectedIndex(-1);
                }

                vista.jdcFechadeNacimiento.setDate(Date.from(empleado.getFechaNacimiento()
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                vista.jCheckBoxEstado.setSelected(empleado.isEstado());

                //Controlamos visibilidad de especialidad según puesto
                controlarVisibilidadEspecialidad();

                activarCampos();
                vista.jbtEliminar.setEnabled(true);
            }
        }
    }

    private void controlarVisibilidadEspecialidad() {
        String puesto = (String) vista.jComboBoxPuesto.getSelectedItem();
        boolean esMasajista = "Masajista".equals(puesto);

        vista.jlEspecialidad.setVisible(esMasajista);
        vista.jComboBoxEspecialidad.setVisible(esMasajista);
    }

    private Empleado buscarEmpleadoPorId(int id) { //Buscar empleado por ID,usado para la corrección)
        //Implementamos usando listarEmpleados y filtrando por ID
        List<Empleado> empleados = empleadoData.listarEmpleados(true, "Todas");
        for (Empleado emp : empleados) {
            if (emp.getIdEmpleado() == id) {
                return emp;
            }
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.jbtSalir) {
            vista.dispose();
        } else if (e.getSource() == vista.jbtNuevo) {
            limpiarCampos();
            activarCampos();
            vista.jtxDocumento.requestFocus();
        } else if (e.getSource() == vista.jbtBuscar) {
            buscarEmpleado();
        } else if (e.getSource() == vista.jbtGuardar) {
            guardarOModificarEmpleado();
        } else if (e.getSource() == vista.jbtEliminar) {
            eliminarEmpleado();
        } else if (e.getSource() == vista.jbtAltaBaja) {
            cambiarEstadoEmpleado();
        } else if (e.getSource() == vista.rbtnActivo1 || e.getSource() == vista.rbtnInactivo1) {
            actualizarTabla();
        } else if (e.getSource() == vista.cmbEspecialidadBuscar) {
            actualizarTabla();
        } else if (e.getSource() == vista.jCB_PuestoParaTabla) { //Filtro por puesto en tabla
            actualizarTabla();
        } else if (e.getSource() == vista.jComboBoxPuesto) { //Control especialidad según puesto seleccionado
            controlarVisibilidadEspecialidad();
        }
    }

    private void buscarEmpleado() {
        if (vista.jtxDocumento.getText().isEmpty() || vista.jtxDocumento.getText().equals("0")) {
            JOptionPane.showMessageDialog(vista, "Debe ingresar el DNI del empleado.");
            return;
        }

        try {
            int dni = Integer.parseInt(vista.jtxDocumento.getText());

            if (String.valueOf(dni).length() != 8) {
                JOptionPane.showMessageDialog(vista, "El DNI debe tener 8 dígitos");
                return;
            }

            Empleado empleado = empleadoData.buscarEmpleadoPorDni(dni);

            if (empleado != null) {
                idEmpleado = empleado.getIdEmpleado();
                vista.jtxtApellido.setText(empleado.getApellido());
                vista.jtxtNombre.setText(empleado.getNombre());
                vista.jtxTelefono.setText(empleado.getTelefono());
                vista.jComboBoxPuesto.setSelectedItem(empleado.getPuesto());
                vista.jtxMatricula.setText(empleado.getMatricula() != null ? empleado.getMatricula() : "");

                //Manejamos especialidad según el puesto
                if ("Masajista".equals(empleado.getPuesto()) && empleado.getEspecialidad() != null) {
                    vista.jComboBoxEspecialidad.setSelectedItem(empleado.getEspecialidad());
                } else {
                    vista.jComboBoxEspecialidad.setSelectedIndex(-1);
                }

                vista.jdcFechadeNacimiento.setDate(Date.from(empleado.getFechaNacimiento()
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                vista.jCheckBoxEstado.setSelected(empleado.isEstado());

                //Controlar visibilidad de especialidad según puesto
                controlarVisibilidadEspecialidad();

                activarCampos();
                vista.jbtEliminar.setEnabled(true);
                JOptionPane.showMessageDialog(vista, "Empleado encontrado. Listo para modificar.");
            } else {
                // Empleado no encontrado
                this.idEmpleado = -1; // Marcar como nuevo registro potencial

                int opcion = JOptionPane.showConfirmDialog(
                        vista,
                        "¿Desea agregarlo como nuevo empleado?",
                        "Empleado No Encontrado",
                        JOptionPane.YES_NO_OPTION
                );

                if (opcion == JOptionPane.YES_OPTION) {
                    // El DNI se mantiene en el campo para ser usado en el nuevo registro
                    vista.jbtEliminar.setEnabled(false);
                    vista.jbtGuardar.setEnabled(true);
                    vista.jCheckBoxEstado.setSelected(true); // Estado seleccionado activo para nuevo
                    activarCampos(); // Habilita campos para la carga de datos
                    vista.jtxtApellido.requestFocus(); // Mover el foco al siguiente campo

                    // Controlar visibilidad de especialidad según puesto por defecto
                    controlarVisibilidadEspecialidad();

                } else {
                    // Si el usuario no desea agregarlo, limpiamos el DNI para buscar de nuevo
                    vista.jtxDocumento.setText("");
                    vista.jtxDocumento.requestFocus();
                    this.idEmpleado = -1; // Se reinicia el id para asegurarnos
                    inactivarCampos(); // Asegurar que los campos sigan deshabilitados
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "DNI inválido. Debe ser un número entero.");
            vista.jtxDocumento.setText("");
            vista.jtxDocumento.requestFocus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al buscar: " + ex.getMessage());
            vista.jtxDocumento.requestFocus();
        }
    }

    private void guardarOModificarEmpleado() {
        if (vista.jtxDocumento.getText().isEmpty() || vista.jtxDocumento.getText().equals("0")) {
            JOptionPane.showMessageDialog(vista, "El DNI no puede estar vacío o ser 0.");
            return;
        }

        if (vista.jtxtApellido.getText().trim().isEmpty() || vista.jtxtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Nombre y apellido son obligatorios.");
            return;
        }

        if (vista.jdcFechadeNacimiento.getDate() == null) {
            JOptionPane.showMessageDialog(vista, "Debe seleccionar una fecha de nacimiento.");
            return;
        }

        try {
            int dni = Integer.parseInt(vista.jtxDocumento.getText());

            if (String.valueOf(dni).length() != 8) {
                JOptionPane.showMessageDialog(vista, "El DNI debe tener exactamente 8 dígitos.");
                return;
            }

            String nombre = vista.jtxtNombre.getText().trim();
            String apellido = vista.jtxtApellido.getText().trim();
            String telefono = vista.jtxTelefono.getText().trim();
            String puesto = (String) vista.jComboBoxPuesto.getSelectedItem();

            if (puesto == null || puesto.isEmpty() || puesto.equals("Seleccione...")) {
                JOptionPane.showMessageDialog(vista, "Debe seleccionar un puesto.");
                vista.jComboBoxPuesto.requestFocus();
                return;
            }

            //Validamos matrícula
            String matricula = vista.jtxMatricula.getText().trim();
            if (!matricula.isEmpty() && !matricula.matches("^[a-zA-Z0-9]+$")) {
                JOptionPane.showMessageDialog(vista, "La matrícula solo puede contener números y letras.");
                return;
            }

            //Obtenemos especialidad según el puesto y validar especialidad si es masajista (OBLIGATORIO)
            String especialidad = null;
            if ("Masajista".equals(puesto)) {
                especialidad = (String) vista.jComboBoxEspecialidad.getSelectedItem();
                if (especialidad == null || especialidad.isEmpty() || especialidad.equals("Seleccione...")) {
                    JOptionPane.showMessageDialog(vista, "Debe seleccionar una especialidad para el masajista.");
                    vista.jComboBoxEspecialidad.requestFocus();
                    return;
                }
            }

            LocalDate fechaNacimiento = vista.jdcFechadeNacimiento.getDate()
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            boolean estado = vista.jCheckBoxEstado.isSelected();

            Empleado empleado = new Empleado(dni, puesto, apellido, nombre, telefono,
                    fechaNacimiento, matricula.isEmpty() ? null : matricula,
                    especialidad, estado);

            if (idEmpleado > 0) {
                empleado.setIdEmpleado(idEmpleado);

                int confirmacion = JOptionPane.showConfirmDialog(vista,
                        "¿Está seguro de modificar los datos del empleado?",
                        "Confirmación",
                        JOptionPane.YES_NO_OPTION);

                if (confirmacion == JOptionPane.YES_OPTION) {
                    empleadoData.modificarEmpleado(empleado);
                    limpiarCampos();
                    inactivarCampos();
                    cargarEspecialidades();
                    actualizarTabla();
                    JOptionPane.showMessageDialog(vista, "Empleado modificado correctamente.");
                }
            } else {
                int confirmacion = JOptionPane.showConfirmDialog(vista,
                        "¿Está seguro de guardar el nuevo empleado?",
                        "Confirmación",
                        JOptionPane.YES_NO_OPTION);

                if (confirmacion == JOptionPane.YES_OPTION) {
                    empleadoData.altaEmpleado(empleado);
                    limpiarCampos();
                    inactivarCampos();
                    cargarEspecialidades();
                    actualizarTabla();
                    JOptionPane.showMessageDialog(vista, "Empleado guardado correctamente.");
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Error en el formato de datos numéricos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al guardar el empleado: " + ex.getMessage());
        }
    }

    private void eliminarEmpleado() {
        if (idEmpleado > 0) {
            int confirmacion = JOptionPane.showConfirmDialog(vista,
                    "¿Está seguro de dar de baja (baja lógica) a este empleado?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                empleadoData.eliminarEmpleadoPorId(idEmpleado);
                limpiarCampos();
                inactivarCampos();
                actualizarTabla();
            }
        } else {
            JOptionPane.showMessageDialog(vista, "No hay un empleado seleccionado para eliminar.");
        }
    }

    private void cambiarEstadoEmpleado() {
        int filaSeleccionada = vista.tbEmpleados.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Debe seleccionar un empleado de la tabla.");
            return;
        }

        int id = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Empleado emp = buscarEmpleadoPorId(id);

        if (emp != null) {
            boolean nuevoEstado = !emp.isEstado();
            String mensaje = nuevoEstado ? "dar de ALTA" : "dar de BAJA";

            int confirmacion = JOptionPane.showConfirmDialog(vista,
                    "¿Está seguro de " + mensaje + " al empleado " + emp.getApellido() + ", " + emp.getNombre() + "?",
                    "Confirmar Cambio de Estado",
                    JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                empleadoData.cambiarEstadoEmpleado(id, nuevoEstado);

                // Si el empleado está cargado en el formulario, actualizar checkbox
                if (idEmpleado == id) {
                    vista.jCheckBoxEstado.setSelected(nuevoEstado);
                }

                actualizarTabla();
                JOptionPane.showMessageDialog(vista, "Estado del empleado actualizado correctamente.");
            }
        }
    }

    // CORRECCIÓN: Método activarCampos corregido
    public void activarCampos() {
        vista.jtxDocumento.setEnabled(true);
        vista.jtxtApellido.setEnabled(true);
        vista.jtxtNombre.setEnabled(true);
        vista.jtxTelefono.setEnabled(true);
        vista.jComboBoxPuesto.setEnabled(true); // CORREGIDO: Habilitar ComboBox puesto
        vista.jtxMatricula.setEnabled(true);
        vista.jComboBoxEspecialidad.setEnabled(true); // CORREGIDO: Habilitar ComboBox especialidad
        vista.jdcFechadeNacimiento.setEnabled(true);
        vista.jCheckBoxEstado.setEnabled(true);
        vista.jbtGuardar.setEnabled(true);

        controlarVisibilidadEspecialidad(); // Aplicar visibilidad
    }

    // CORRECCIÓN: Método inactivarCampos corregido
    public void inactivarCampos() {
        vista.jbtGuardar.setEnabled(false);
        vista.jbtEliminar.setEnabled(false);
        vista.jtxDocumento.setEnabled(true);
        vista.jtxtApellido.setEnabled(false);
        vista.jtxtNombre.setEnabled(false);
        vista.jtxTelefono.setEnabled(false);
        vista.jComboBoxPuesto.setEnabled(false);
        vista.jtxMatricula.setEnabled(false);
        vista.jComboBoxEspecialidad.setEnabled(false);
        vista.jdcFechadeNacimiento.setEnabled(false);
        vista.jCheckBoxEstado.setEnabled(false);

        vista.jtxDocumento.requestFocus(); //Enfoca dni para nueva búsqueda
    }

    public void limpiarCampos() {
        vista.jtxDocumento.setText("");
        vista.jtxtApellido.setText("");
        vista.jtxtNombre.setText("");
        vista.jtxTelefono.setText("");
        vista.jComboBoxPuesto.setSelectedIndex(0);
        vista.jtxMatricula.setText("");
        vista.jComboBoxEspecialidad.setSelectedIndex(-1);
        vista.jdcFechadeNacimiento.setDate(null);
        vista.jCheckBoxEstado.setSelected(true); // CORREGIDO: Por defecto en true (activo)
        this.idEmpleado = -1; //Reinicia el id del empleado
        vista.jbtEliminar.setEnabled(false);
        vista.tbEmpleados.clearSelection();

        controlarVisibilidadEspecialidad(); // Aplicar visibilidad al limpiar
    }

    //Métodos requeridos por KEYLISTENER
    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {
        if (e.getSource() == vista.jtxDocumento) {
            char caracter = e.getKeyChar();
            // Permite solo dígitos y Backspace
            if ((caracter < '0' || caracter > '9') && caracter != '\b') { // \b es Backspace
                e.consume();
            }
            // Control de longitud visual (opcional)
            if (vista.jtxDocumento.getText().length() >= 8 && caracter != '\b') {
                e.consume();
            }
        }

        if (e.getSource() == vista.jtxtNombre) {
            char caracter = e.getKeyChar();
            if (!Character.isLetter(caracter) && caracter != ' ' && caracter != '\b') {
                e.consume();
            }
        }

        if (e.getSource() == vista.jtxtApellido) {
            char caracter = e.getKeyChar();
            if (!Character.isLetter(caracter) && caracter != ' ' && caracter != '\b') {
                e.consume();
            }
        }

        if (e.getSource() == vista.jtxTelefono) {
            char caracter = e.getKeyChar();
            if ((caracter < '0' || caracter > '9') && caracter != '-'
                    && caracter != ' ' && caracter != '\b' && caracter != '+') {
                e.consume();
            }
            if (vista.jtxTelefono.getText().length() >= 20 && caracter != '\b') {
                e.consume();
            }
        }

        if (e.getSource() == vista.jtxMatricula) {
            char caracter = e.getKeyChar();
            if (!Character.isLetterOrDigit(caracter)
                    && caracter != ' ' && caracter != '.' && caracter != '/'
                    && caracter != '°' && caracter != '-' && caracter != '\b') {
                e.consume();
            }
            if (vista.jtxMatricula.getText().length() >= 15 && caracter != '\b') {
                e.consume();
            }
        }
    }

    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        // No implementado
    }

    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {
        // No implementado
    }
}
