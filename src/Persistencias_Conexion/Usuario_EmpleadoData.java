package Persistencias_Conexion;

import Modelos.Empleado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Maneja el LOGIN del empleado y devuelve el empleado autenticado.
 *
 * @author GP6
 */
public class Usuario_EmpleadoData {

    private Connection con = null;

    // VARIABLE GLOBAL - Empleado logueado en el sistema
    public static Empleado empleadoLogueado = null;   // NUEVO

    public Usuario_EmpleadoData() {
        con = Conexion.getConexion();
    }

    /**
     * Valida usuario y contraseña. Devuelve objeto Empleado si es válido.
     */
    public Empleado login(String usuario, String pass) {

        String sql = "SELECT * FROM empleado "
                + "WHERE usuario = ? AND pass = ? AND estado = 1";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Empleado emp = new Empleado();

                emp.setIdEmpleado(rs.getInt("idEmpleado"));
                emp.setDni(rs.getInt("dni"));
                emp.setNombre(rs.getString("nombre"));
                emp.setApellido(rs.getString("apellido"));
                emp.setTelefono(rs.getString("telefono"));

                java.sql.Date f = rs.getDate("fechaNacimiento");
                if (f != null) {
                    emp.setFechaNacimiento(f.toLocalDate());   // porque modelo Empleado.java usa LocalDate
                }

                emp.setPuesto(rs.getString("puesto"));
                emp.setMatricula(rs.getString("matricula"));
                emp.setEspecialidad(rs.getString("especialidad"));
                emp.setUsuario(rs.getString("usuario"));
                emp.setPass(rs.getString("pass"));
                emp.setEstado(rs.getBoolean("estado"));

                empleadoLogueado = emp; // GUARDAR SESIÓN
                return emp;
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al validar usuario: " + ex.getMessage());
        }

        return null; // ✔ login inválido
    }

    /**
     * Retorna el empleado logueado actualmente.
     */
    public Empleado getEmpleadoLogueado() {
        return empleadoLogueado;
    }
   
    
    public void cerrarSesion() {
        empleadoLogueado = null;//Limpia la sesión actual.
    }

}
