package ControladoresDeVistas;

import Vistas.VistaLogin;
import Vistas.Vista_MenuSpa;
import Persistencias_Conexion.Usuario_EmpleadoData;
import Modelos.Empleado;
import javax.swing.JOptionPane;

public class ControladorLogin {

    private VistaLogin vista;
    private Usuario_EmpleadoData data;

    public ControladorLogin(VistaLogin vista) {
        this.vista = vista;
        this.data = new Usuario_EmpleadoData();

        this.vista.jButtonLogin.addActionListener(e -> login());
        this.vista.btnSalir.addActionListener(e -> System.exit(0));
    }

    public void iniciar() {
        vista.setVisible(true);
    }

    private void login() {
        String usuario = vista.txtUsuario.getText().trim();
        String pass = new String(vista.jPasswordFieldPass.getPassword()).trim();

        if (usuario.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Complete todos los campos.");
            return;
        }

        Empleado empleado = data.login(usuario, pass);

        if (empleado == null) {
            JOptionPane.showMessageDialog(vista,
                    "Usuario o contraseña incorrectos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
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
