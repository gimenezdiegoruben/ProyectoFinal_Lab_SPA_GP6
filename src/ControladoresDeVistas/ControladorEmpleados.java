package ControladoresDeVistas;

import Modelos.Empleado;
import Persistencias_Conexion.EmpleadoData;
import Vistas.Vista_MenuSpa;
import Vistas.VistaEmpleados;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import javax.swing.JOptionPane;

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
    private int idAlumno;

    public ControladorEmpleados( Vista_MenuSpa menu,VistaEmpleados vista, EmpleadoData data) {
        this.vista = vista;
        this.empleadoData = data;
        this.menu = menu;
        vista.jtxDocumento.addKeyListener(this);
        vista.jbtBuscar.addActionListener(this);
        vista.jbtSalir.addActionListener(this);
        vista.jbtNuevo.addActionListener(this);
        vista.jbtEliminar.addActionListener(this);
        vista.jbtGuardar.addActionListener(this);
        vista.jrbEstado.addActionListener(this);
    }

    ControladorEmpleados(VistaEmpleados vista, Empleado data, Vista_MenuSpa menu) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void iniciar() {
        this.menu.JDesktopPFondo.add(vista); // Agrega la vista (JInternalFrame) al JDesktopPane
        this.vista.setVisible(true);
        this.menu.JDesktopPFondo.moveToFront(vista);
        vista.requestFocus();

        // Estado inicial de la vista
        limpiarCampos();
        vista.jbtGuardar.setEnabled(false);
        vista.jbtEliminar.setEnabled(false);
        desactivarCampos(); // Deshabilita los campos al iniciar la vista
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == vista.jbtBuscar) {
            limpiarCampos(); // Limpia campos antes de la búsqueda (REQUISITO)
            desactivarCampos();

            vista.jbtEliminar.setEnabled(false); // Deshabilita eliminar al inicio de la búsqueda
            vista.jbtGuardar.setEnabled(false);
            vista.jtxDocumento.setEnabled(true);

            if (vista.jtxDocumento.getText().equals("") || vista.jtxDocumento.getText().equals("0")) {
                JOptionPane.showMessageDialog(null, "El DNI no puede estar en blanco o ser cero.");
                return; // Salir si el DNI es inválido
            }
            // Pedir 8 digitos para la búsqueda
            String dniTexto = vista.jtxDocumento.getText().trim();

            if (dniTexto.isEmpty() || dniTexto.length() != 8) {
                JOptionPane.showMessageDialog(null, "El DNI debe tener 8 dígitos para realizar la búsqueda.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                vista.jtxDocumento.requestFocus();
                return; // Salir si el DNI es inválido
            }

            try {
                int dni = Integer.parseInt(vista.jtxDocumento.getText());
                Empleado alum = empleadoData.buscarAlumnoPorDni(dni);

                if (alum != null) {
                    vista.jtxNombre.setText(alum.getNombre());
                    vista.jtxApellido.setText(alum.getApellido());
                    vista.jrbEstado.setSelected(alum.isEstado());
                    vista.jdcFechadeNacimiento.setDate(Date.valueOf(alum.getFechaNacimiento()));
                    this.idAlumno = alum.getIdEmpleado();

                    // Habilitamos edición y botones de gestión
                    activarCampos();
                    vista.jbtGuardar.setEnabled(true);
                    vista.jbtEliminar.setEnabled(true); // Mostrar eliminar solo si se encuentra

                } else {
                    // sino encuentra el alumno
                    this.idAlumno = -1; // Marcar como nuevo registro potencial

                    int opcion = JOptionPane.showConfirmDialog(
                            null,
                            "¿Desea agregarlo como nuevo?",
                            "Alumno No Encontrado",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (opcion == JOptionPane.YES_OPTION) {
                        // El DNI se mantiene en el campo para ser usado en el nuevo registro para cargarlo.
                        vista.jbtEliminar.setEnabled(false);
                        vista.jbtGuardar.setEnabled(true);
                        vista.jrbEstado.setSelected(true); // Estado seleccionado activo para nuevo
                        activarCampos(); // Habilita campos para la carga de datos
                        vista.jtxApellido.requestFocus(); // Mover el foco al siguiente campo
                    } else {
                        // Si el usuario no desea agregarlo, limpiamos el DNI para buscar de nuevo.
                        vista.jtxDocumento.setText("");
                        vista.jtxDocumento.requestFocus();
                        this.idAlumno = 0; // Se reinicia el id para asegurarnos
                        desactivarCampos(); // Asegurar que los campos sigan deshabilitados
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "DNI inválido. Debe ser un número entero.");
                vista.jtxDocumento.setText("");
            }
        }

        if (e.getSource() == vista.jbtEliminar) {
            try {
                int dni = Integer.parseInt(vista.jtxDocumento.getText());
                if (this.idAlumno > 0) {
                    int confirmacion = JOptionPane.showConfirmDialog(null, "¿Estás seguro de ELIMINAR al alumno con DNI " + dni + "?", "Confirmación de Eliminación", JOptionPane.YES_NO_OPTION);

                    if (confirmacion == JOptionPane.YES_OPTION) {
                        empleadoData.eliminarAlumno(dni); // Usar DNI si el método está diseñado así
                        JOptionPane.showMessageDialog(null, "Alumno eliminado (baja lógica de estado) con éxito.");
                        limpiarCampos();
                        vista.jbtEliminar.setEnabled(false); // Deshabilitar después que eliminamos
                        desactivarCampos();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No se puede eliminar un alumno que no fue cargado.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Error al procesar el DNI para eliminar.");
            }
        }

        if (e.getSource() == vista.jbtSalir) {
            vista.dispose();
        }

        if (e.getSource() == vista.jbtNuevo) {
            limpiarCampos();
            vista.jbtNuevo.setEnabled(true);
            vista.jbtEliminar.setEnabled(false); // Deshabilitamos eliminar ya que quiere uno nuevo
            vista.jtxDocumento.requestFocus();
            vista.jbtGuardar.setEnabled(true);
            vista.jrbEstado.setSelected(true);
            this.idAlumno = -1; // Marca como nuevo registro
            activarCampos(); // Habilita campos para la carga de datos
        }

        if (e.getSource() == vista.jbtGuardar) {
            // Validaciones de datos
            String dniTexto = vista.jtxDocumento.getText().trim();
            if (dniTexto.isEmpty() || dniTexto.length() != 8) { // 8 digitos p el dni
                JOptionPane.showMessageDialog(null, "El DNI debe tener 8 dígitos.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (vista.jtxNombre.getText().trim().isEmpty() || vista.jtxApellido.getText().trim().isEmpty() || vista.jdcFechadeNacimiento.getDate() == null) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int dni = Integer.parseInt(dniTexto);
                String nombre = vista.jtxNombre.getText().trim();
                String apellido = vista.jtxApellido.getText().trim();
                boolean estado = vista.jrbEstado.isSelected();
                java.util.Date nac = vista.jdcFechadeNacimiento.getDate();
                Instant instant = nac.toInstant();
                LocalDate fecha = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                String puesto = (String) vista.jComboBoxPuesto.getSelectedItem();

                Empleado aGuardar = new Empleado(dni, puesto, apellido, nombre, fecha, estado);

                if (idAlumno == -1) {
                    // GUARDAR NUEVO EMPLEADO
                    // Primero comprobamos si DNI ya existe 
                    if (empleadoData.buscarAlumnoPorDni(dni) != null) {
                        JOptionPane.showMessageDialog(null, "El alumno con DNI " + dni + " ya existe en la base de datos.", "DNI Duplicado", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int confirmacion = JOptionPane.showConfirmDialog(null, "¿Guardar el nuevo alumno con DNI " + dni + "?", "Confirma de Guardado", JOptionPane.YES_NO_OPTION);

                    if (confirmacion == JOptionPane.YES_OPTION) {
                        empleadoData.guardarAlumno(aGuardar); // y sale cartel de linea 50 de AlumnoData guardado con exito
                        limpiarCampos();
                        desactivarCampos();
                    }
                } else {
                    // Modificar alumno existente
                    aGuardar.setIdEmpleado(idAlumno); // Asignar el ID para midificar

                    // Confirmación para modificar
                    int confirmacion = JOptionPane.showConfirmDialog(null, "Los datos del alumno con DNI " + dni + " serán modificados. Confirma modificación?", "Confirmación de Modificación", JOptionPane.YES_NO_OPTION);

                    if (confirmacion == JOptionPane.YES_OPTION) {
                        empleadoData.modificarAlumno(aGuardar);
                        JOptionPane.showMessageDialog(null, "Se ha guardado la modificación con éxito.");
                        limpiarCampos();
                        desactivarCampos();
                        vista.jbtEliminar.setEnabled(false);
                    }
                }
                vista.jbtGuardar.setEnabled(false); // Deshabilitar después de guardar/modificar
            } catch (NumberFormatException ex) {
            }
        }

        // Manejo del estado (Checkbox/RadioButton)
        if (e.getSource() == vista.jrbEstado) {
            // Lógica si necesitas una acción inmediata al cambiar el estado (aparte de la acción de guardar)
        }
    }

    public void activarCampos() {
        // Habilita los campos para que se puedan editar (REQUISITO)
        vista.jtxDocumento.setEnabled(true);
        vista.jtxApellido.setEnabled(true);
        vista.jtxNombre.setEnabled(true);
        vista.jdcFechadeNacimiento.setEnabled(true);
        vista.jrbEstado.setEnabled(true);
    }

    public void desactivarCampos() {
        // Deshabilita los campos después de una acción (limpiar/guardar)
        vista.jtxDocumento.setEnabled(true); // Mantener DNI editable para una nueva búsqueda
        vista.jtxApellido.setEnabled(false);
        vista.jtxNombre.setEnabled(false);
        vista.jdcFechadeNacimiento.setEnabled(false);
        vista.jrbEstado.setEnabled(false);
    }

    public void limpiarCampos() {
        vista.jtxApellido.setText("");
        vista.jtxNombre.setText("");
        vista.jdcFechadeNacimiento.setDate(null);
        vista.jrbEstado.setSelected(false); // Dejar el estado en false por defecto
        this.idAlumno = 0; // Reiniciar el ID del alumno
    }

    // Métodos requeridos por KEYLISTENER
    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {
        if (e.getSource() == vista.jtxDocumento) {
            char caracter = e.getKeyChar();
            // Permite solo dígitos y limita a 8 caracteres (aunque el control fuerte es al guardar)
            if ((caracter < '0' || caracter > '9') && caracter != '\b') { // \b es Backspace
                e.consume();
            }
            // Control de longitud visual (opcional)
            if (vista.jtxDocumento.getText().length() >= 8 && caracter != '\b') {
                e.consume();
            }
        }
    }

    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        // No se usa
    }

    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {
        // No se usa
    }
}
