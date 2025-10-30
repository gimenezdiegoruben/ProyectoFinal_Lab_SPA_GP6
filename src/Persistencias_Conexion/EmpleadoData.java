
package Persistencias_Conexion;

import Modelos.Empleado;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * @author Grupo10 
 * 
 * Altamirano Karina 
 * Gianfranco Antonacci Matías 
 * Bequis Marcos Ezequiel 
 * Dave Natalia 
 * Quiroga Dorzan Alejo
 */

public class EmpleadoData {
    
    private Connection con = null;
    private EmpleadoData EmplData;
    
    public EmpleadoData() {
        this.con = Conexion.getConexion();
        EmplData = new EmpleadoData();
    }
    
    public void altaEmpleado(Empleado empleado) {
        String sql = "INSERT INTO `empleado`(`dni`, `puesto`) VALUES (?, ?)";

        try {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, empleado.getDni());
            ps.setString(2, empleado.getPuesto());
            
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                empleado.setIdEmpleado(rs.getInt(1));
            }
            ps.close();
            
            //JOptionPane.showMessageDialog(null, "Empleado dado de alta con éxito.", "", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No se pudo conectar con la tabla de empleados", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void guardarAlumno(Empleado empleado) {

        String sql = "INSERT INTO alumno (dni, apellido, nombre, fechaNacimiento, estado) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        con = Conexion.getConexion();

        try {
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, empleado.getDni());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getNombre());
            ps.setDate(4, Date.valueOf(empleado.getFechaNacimiento())); // Pasamos de LocalDate a Date
            ps.setBoolean(5, empleado.isEstado());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                // Obtener y asignar el ID autogenerado
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    empleado.setIdEmpleado(rs.getInt(1));
                    JOptionPane.showMessageDialog(null, "Alumno " + empleado.getNombre() + " añadido con éxito");
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla alumno: " + ex.getMessage());

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error al cerrar el PreparedStatement: " + ex.getMessage());
            }
        }
    }

    public Empleado buscarAlumnoPorDni(int dni) {
        con = Conexion.getConexion();
        Empleado alumno = null;
        String sql = "SELECT * FROM alumno WHERE dni=?";
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, dni);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                alumno = new Empleado();
                alumno.setIdEmpleado(rs.getInt("idAlumno"));
                alumno.setDni(rs.getInt("dni"));
                alumno.setApellido(rs.getString("apellido"));
                alumno.setNombre(rs.getString("nombre"));
                alumno.setFechaNacimiento(rs.getDate("fechaNacimiento").toLocalDate());
                alumno.setEstado(rs.getBoolean("estado"));
            } else {
                JOptionPane.showMessageDialog(null, "El alumno con DNI " + dni + " no existe.");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla Alumno " + ex.getMessage());
        }
        return alumno;
    }

    public List<Empleado> listarAlumnos() {
        con = Conexion.getConexion();
        List<Empleado> alumnos = new ArrayList<>();
        try {
            String sql = "SELECT * FROM alumno WHERE estado = 1";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Empleado alumno = new Empleado();
                alumno.setIdEmpleado(rs.getInt("idAlumno"));
                alumno.setDni(rs.getInt("dni"));
                alumno.setApellido(rs.getString("apellido"));
                alumno.setNombre(rs.getString("nombre"));
                alumno.setFechaNacimiento(rs.getDate("fechaNacimiento").toLocalDate());
                alumno.setEstado(rs.getBoolean("estado"));
                alumnos.add(alumno);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error, no se pudo acceder a la tabla Alumno " + ex.getMessage());
        }
        return alumnos;
    }

    public void modificarAlumno(Empleado empleado) {
        con = Conexion.getConexion();
        String sql = "UPDATE alumno SET dni = ?, apellido = ?, nombre = ?, fechaNacimiento = ?, estado = ? WHERE idAlumno = ?";
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, empleado.getDni());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getNombre());
            ps.setDate(4, Date.valueOf(empleado.getFechaNacimiento()));
            ps.setBoolean(5, empleado.isEstado());
            ps.setInt(6, empleado.getIdEmpleado());
            int exito = ps.executeUpdate();
            if (exito == 1) {
                JOptionPane.showMessageDialog(null, "Modificado exitosamente");
            } else {
                JOptionPane.showMessageDialog(null, "El alumno no existe");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No hubo modificaciones. Error al acceder a la tabla Alumno " + ex.getMessage());
        }
    }

    public void eliminarAlumno(int id) {
        con = Conexion.getConexion();
        String sql = "UPDATE alumno SET estado = 0 WHERE idAlumno = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Se eliminó  el alumno con ID" + id + "(baja lógica)");
            } else {
                System.out.println("No se encontró el alumno con ID: " + id + " para dar de baja.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al borrar alumno. No se puede acceder a la tabla Alumno");
        }
    }

    public void eliminarAlumnoPorDni(int dni) {
        con = Conexion.getConexion();
        String sql = "UPDATE alumno SET estado = 0 WHERE dni = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, dni);
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Se eliminó  el alumno con DNI: " + dni + " (baja lógica)");
            } else {
                System.out.println("No se encontró el alumno con DNI: " + dni + " para dar de baja.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al borrar alumno. No se puede acceder a la tabla Alumno");
        }
    }
}
