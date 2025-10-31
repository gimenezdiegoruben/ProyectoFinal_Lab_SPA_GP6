package ControladoresDeVistas;

import Modelos.Empleado;
import Persistencias_Conexion.EmpleadoData;
import Vistas.Vista_MenuSpa;
import Vistas.VistaEmpleados;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.JOptionPane;
import Vistas.Vista_MenuSpa;

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

    public ControladorEmpleados(Vista_MenuSpa menu, VistaEmpleados vista, EmpleadoData data) {
        this.vista = vista;
        this.empleadoData = data;
        this.menu = menu;
        vista.jtxDocumento.addKeyListener(this);
        vista.jbtBuscar.addActionListener(this);
        vista.jbtSalir.addActionListener(this);
        vista.jbtNuevo.addActionListener(this);
        vista.jbtEliminar.addActionListener(this);
        vista.jbtGuardar.addActionListener(this);
        inactivarCampos();
    }

    public void iniciar() {
        vista.setTitle("Gestión de Empleados");
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
        }
    }

    private void buscarEmpleado() {
        if (vista.jtxDocumento.getText().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Debe ingresar el DNI del empleado.");
            return;
        }

        try {
            int dni = Integer.parseInt(vista.jtxDocumento.getText());
            Empleado empleado = empleadoData.buscarEmpleadoPorDni(dni);

            if (empleado != null) {
                //al enctrontrar empleado carga datos para modificación
                idEmpleado = empleado.getIdEmpleado();
                vista.jtxApellido.setText(empleado.getApellido());
                vista.jtxNombre.setText(empleado.getNombre());

                vista.jtxTelefono.setText(empleado.getTelefono());
                vista.jComboBoxPuesto.setSelectedItem(empleado.getPuesto());
                vista.jtxMatricula.setText(String.valueOf(empleado.getMatricula()));
                vista.jtxEspecialidad.setText(empleado.getEspecialidad());

                vista.jdcFechadeNacimiento.setDate(Date.from(empleado.getFechaNacimiento().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                vista.jCheckBoxEstado.setSelected(empleado.isEstado());

                activarCampos();
                vista.jbtEliminar.setEnabled(true);
                JOptionPane.showMessageDialog(vista, "Empleado encontrado. Listo para modificar.");

            } else {
                // Empleado no encontrado: se permite cargar uno nuevo con ese DNI
                idEmpleado = -1; // Indica nuevo empleado
                limpiarCampos();
                vista.jtxDocumento.setText(String.valueOf(dni)); //Mantien DNI
                activarCampos();
                vista.jbtEliminar.setEnabled(false);
                JOptionPane.showMessageDialog(vista, "Empleado no encontrado. Ingrese datos para dar de alta.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El DNI debe ser un número entero.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al buscar: " + ex.getMessage());
        }
    }

    private void guardarOModificarEmpleado() {
        if (vista.jtxDocumento.getText().isEmpty() || vista.jtxApellido.getText().isEmpty() || vista.jtxNombre.getText().isEmpty() || vista.jdcFechadeNacimiento.getDate() == null) {
            JOptionPane.showMessageDialog(vista, "Todos los campos obligatorios deben estar llenos.");
            return;
        }

        try {
            int dni = Integer.parseInt(vista.jtxDocumento.getText());
            String nombre = vista.jtxNombre.getText();
            String apellido = vista.jtxApellido.getText();
            String telefono = vista.jtxTelefono.getText(); //...
            String puesto = (String) vista.jComboBoxPuesto.getSelectedItem(); //...

            int matricula = 0;
            if (!vista.jtxMatricula.getText().isEmpty()) { //...
                matricula = Integer.parseInt(vista.jtxMatricula.getText());
            }

            String especialidad = vista.jtxEspecialidad.getText(); //...

            // apeo de Date a LocalDate
            LocalDate fechaNacimiento = vista.jdcFechadeNacimiento.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            boolean estado = vista.jCheckBoxEstado.isSelected();

            Empleado empleado = new Empleado(dni, puesto, apellido, nombre, telefono, fechaNacimiento, matricula, especialidad, estado);

            if (idEmpleado > 0) {
                //Modificar (existe idEmpleado)
                empleado.setIdEmpleado(idEmpleado);
                empleadoData.modificarEmpleado(empleado);
            } else {
                // Nuevo (idEmpleado es -1)
                empleadoData.altaEmpleado(empleado);
            }

            limpiarCampos();
            inactivarCampos();
            vista.jtxDocumento.setText("");
            vista.jbtEliminar.setEnabled(false);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Error en el formato de datos (DNI y/o Matrícula deben ser numéricos).");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al guardar el empleado: " + ex.getMessage());
        }
    }

    private void eliminarEmpleado() {
        if (idEmpleado > 0) {
            int confirmacion = JOptionPane.showConfirmDialog(vista, "¿Está seguro de dar de baja (baja lógica) a este empleado?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                empleadoData.eliminarEmpleadoPorId(idEmpleado);
                limpiarCampos();
                inactivarCampos();
                vista.jtxDocumento.setText("");
                vista.jbtEliminar.setEnabled(false);
            }
        } else {
            JOptionPane.showMessageDialog(vista, "No hay un empleado seleccionado para eliminar.");
        }
    }

    public void activarCampos() {
        vista.jtxDocumento.setEnabled(true);
        vista.jtxApellido.setEnabled(true);
        vista.jtxNombre.setEnabled(true);
        vista.jtxTelefono.setEnabled(true);
        vista.jComboBoxPuesto.setEnabled(true);
        vista.jtxMatricula.setEnabled(true);
        vista.jtxEspecialidad.setEnabled(true);
        vista.jdcFechadeNacimiento.setEnabled(true);
        vista.jCheckBoxEstado.setEnabled(true);
        vista.jbtGuardar.setEnabled(true);
    }

    public void inactivarCampos() {
        vista.jbtGuardar.setEnabled(false);
        vista.jbtEliminar.setEnabled(false);
        vista.jtxDocumento.setEnabled(true);
        vista.jtxApellido.setEnabled(false);
        vista.jtxNombre.setEnabled(false);
        vista.jtxTelefono.setEnabled(false);
        vista.jComboBoxPuesto.setEnabled(false);
        vista.jtxMatricula.setEnabled(false);
        vista.jtxEspecialidad.setEnabled(false);
        vista.jdcFechadeNacimiento.setEnabled(false);
        vista.jCheckBoxEstado.setEnabled(false);

        vista.jtxDocumento.requestFocus(); //Enfoca dni para nueva búsqueda
    }

    public void limpiarCampos() {
        vista.jtxDocumento.setText("");
        vista.jtxApellido.setText("");
        vista.jtxNombre.setText("");
        vista.jtxTelefono.setText("");
        vista.jComboBoxPuesto.setSelectedIndex(0); //Esto puede ser necesario si no se usa setItem
        vista.jtxMatricula.setText("");
        vista.jtxEspecialidad.setText("");
        vista.jdcFechadeNacimiento.setDate(null);
        vista.jCheckBoxEstado.setSelected(false); //Dejo en false por defecto
        this.idEmpleado = -1; //Reinicia el id del empleado
        vista.jbtEliminar.setEnabled(false);
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
        // Se recomienda agregar validación para jtxMatricula (solo dígitos)
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
