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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId; // Necesario para mapear Date a LocalDate
import javax.swing.JOptionPane;

public class EmpleadoData {

    private Connection con = null;

    public EmpleadoData() {

        this.con = Conexion.getConexion();
    }

    /**
     * Da de alta un nuevo empleado en la base de datos.
     * Mapea 0 o valores negativos de matricula a NULL en la BD.
     */
    public void altaEmpleado(Empleado empleado) {
        String sql = "INSERT INTO empleado (dni, nombre, apellido, telefono, fechaNacimiento, puesto, matricula, especialidad, estado) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            ps.setInt(1, empleado.getDni());
            ps.setString(2, empleado.getNombre());
            ps.setString(3, empleado.getApellido());
            ps.setString(4, empleado.getTelefono());
            
            //Mapeo de LocalDate a Date
            LocalDate fechaNac = empleado.getFechaNacimiento();
            ps.setDate(5, Date.valueOf(fechaNac));
            
            ps.setString(6, empleado.getPuesto());
            
            //Si la matrícula es 0 o negativa, se inserta NULL (o se ajusta si la BD no acepta NULL, pero 0 suele ser aceptable si el campo es INT)
            int matricula = empleado.getMatricula();
            if (matricula > 0) {
                ps.setInt(7, matricula); //matricula
            } else {
                ps.setNull(7, java.sql.Types.INTEGER); //Para permitir NULL en la BD si la matrícula es 0 o no se aplica
            }
            
            ps.setString(8, empleado.getEspecialidad());
            ps.setBoolean(9, empleado.isEstado());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                empleado.setIdEmpleado(rs.getInt(1));
            }
            ps.close();
            
            JOptionPane.showMessageDialog(null, "Empleado dado de alta con éxito.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al guardar Empleado: " + ex.getMessage());
        }
    }

    /**
     * Modifica los datos de un empleado existente.
     */
    public void modificarEmpleado(Empleado empleado) {
        String sql = "UPDATE empleado SET dni=?, nombre=?, apellido=?, telefono=?, fechaNacimiento=?, puesto=?, matricula=?, especialidad=?, estado=? "
                   + "WHERE idEmpleado = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setInt(1, empleado.getDni());
            ps.setString(2, empleado.getNombre());
            ps.setString(3, empleado.getApellido());
            ps.setString(4, empleado.getTelefono());
            
            //Mapeo de LocalDate a Date
            LocalDate fechaNac = empleado.getFechaNacimiento();
            ps.setDate(5, Date.valueOf(fechaNac));
            
            ps.setString(6, empleado.getPuesto());
            
            int matricula = empleado.getMatricula();
            if (matricula > 0) {
                ps.setInt(7, matricula);
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            
            ps.setString(8, empleado.getEspecialidad());
            ps.setBoolean(9, empleado.isEstado());
            ps.setInt(10, empleado.getIdEmpleado()); //ID para la condición WHERE

            int filasAfectadas = ps.executeUpdate();
            ps.close();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Datos del Empleado actualizados con éxito.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el Empleado para actualizar.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al modificar Empleado: " + ex.getMessage());
        }
    }

    /**
     * Busca un empleado activo por DNI.
     */
    public Empleado buscarEmpleadoPorDni(int dni) {
        Empleado empleado = null;
        String sql = "SELECT idEmpleado, dni, nombre, apellido, telefono, fechaNacimiento, puesto, matricula, especialidad, estado FROM empleado WHERE dni = ?";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, dni);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int idEmpleado = rs.getInt("idEmpleado");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String telefono = rs.getString("telefono");
                Date fechaNacimientoSql = rs.getDate("fechaNacimiento");
                LocalDate fechaNacimiento = fechaNacimientoSql.toLocalDate();
                String puesto = rs.getString("puesto");
                
                //La matricula puede ser null en la bd, asi que ecesitamos manejarlo
                int matricula = rs.getInt("matricula"); 
                if (rs.wasNull()) {
                    matricula = 0; //usamos 0 o -1 en el modelo si es null en bd
                }
                String especialidad = rs.getString("especialidad");
                boolean estado = rs.getBoolean("estado");

                empleado = new Empleado(idEmpleado, dni, puesto, apellido, nombre, telefono, fechaNacimiento, matricula, especialidad, estado);
            }
            ps.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al buscar empleado: " + ex.getMessage());
        }
        return empleado;
    }
    
    // Realiza una baja lógica (estado = 0) por ID.
    public void eliminarEmpleadoPorId(int id) {
        String sql = "UPDATE empleado SET estado = 0 WHERE idEmpleado = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Se eliminó el empleado con ID " + id + " (baja lógica)");
            } else {
                System.out.println("No se encontró el empleado con ID: " + id + " para dar de baja.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al borrar empleado: " + ex.getMessage());
        }
    }

    //Realiza una baja lógica (estado = 0) por DNI.
    public void eliminarEmpleadoPorDni(int dni) {
        String sql = "UPDATE empleado SET estado = 0 WHERE dni = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, dni);
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Se eliminó el empleado con DNI: " + dni + " (baja lógica)");
            } else {
                System.out.println("No se encontró el empleado con DNI: " + dni + " para dar de baja.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al borrar empleado: " + ex.getMessage());
        }
    }

    // Se recomienda agregar métodos para listar todos, listar activos, etc., para completar el DAO.
}