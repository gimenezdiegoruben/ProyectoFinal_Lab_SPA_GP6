package ControladoresDeVistas;

import Vistas.VistaLogin;
import Vistas.Vista_MenuSpa;
import Persistencias_Conexion.Usuario_EmpleadoData;
import Modelos.Empleado;
import Persistencias_Conexion.EmpleadoData;
import javax.swing.JOptionPane;

public class ControladorLogin {

    private VistaLogin vista;
    private EmpleadoData data;

    public ControladorLogin(VistaLogin vista) {
        this.vista = vista;
        this.data = new EmpleadoData();

        this.vista.jButtonLogin.addActionListener(e -> login());
        this.vista.btnSalir.addActionListener(e -> System.exit(0));
        this.vista.jButtonlimpiar.addActionListener(e -> limpiarCamposLogin());
    }

    public void iniciar() {
        vista.setVisible(true);
    }

    public void limpiarCamposLogin() {
        vista.txtUsuario.setText("");
        vista.jPasswordFieldPass.setText("");
        vista.txtUsuario.requestFocus();
    }

    private void login() {
        String usuario = vista.txtUsuario.getText().trim();
        String pass = new String(vista.jPasswordFieldPass.getPassword()).trim();

        if (usuario.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Complete todos los campos.");
            return;
        }

        Empleado empleado = data.validarLogin(usuario, pass);

        if (empleado == null) {
            JOptionPane.showMessageDialog(vista,
                    "Error al intentar acceder a la BD. Consulte al administrador.",
                    "Acceso denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (empleado.getIdEmpleado() == -1) {
            JOptionPane.showMessageDialog(vista,
                    "El usuario ingresado no existe.",
                    "Usuario no encontrado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (empleado.getIdEmpleado() == -2) {
            JOptionPane.showMessageDialog(vista,
                    "La contraseña es incorrecta.",
                    "Error de autenticación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (empleado.getIdEmpleado() == 0) {
            JOptionPane.showMessageDialog(vista,
                    "El usuario está deshabilitado y no puede iniciar sesión.",
                    "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(vista,
                "Bienvenido " + empleado.getNombre() + " " + empleado.getApellido());

        vista.dispose();

        //luego de logear recien permite abrir menú principal
        Vista_MenuSpa menu = new Vista_MenuSpa();
        ControladorApp_MenuSpa control = new ControladorApp_MenuSpa(menu, empleado);
        control.iniciar();
    }
}
