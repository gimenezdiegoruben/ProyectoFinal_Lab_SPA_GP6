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
import java.time.LocalDate;
import javax.swing.JOptionPane;

public class EmpleadoData {

    private Connection con = null;

    public EmpleadoData() {
        this.con = Conexion.getConexion();
    }

    /**
     * Da de alta un nuevo empleado en la base de datos. Mapea 0 o valores
     * negativos de matricula a NULL en la BD.
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
            String matricula = empleado.getMatricula();
            if (matricula != null && !matricula.isEmpty()) {
                ps.setString(7, matricula); //matricula
            } else {
                ps.setNull(7, java.sql.Types.VARCHAR); //Para permitir NULL en la BD 
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

    public List<String> obtenerEspecialidades() {
        List<String> especialidades = new ArrayList<>();
        String sql = "SELECT DISTINCT especialidad FROM empleado WHERE especialidad IS NOT NULL AND especialidad <> ''";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                //Asegúrate de que no haya duplicados si el DISTINCT no funcionó por algún motivo de la BD
                if (!especialidades.contains(rs.getString("especialidad"))) {
                    especialidades.add(rs.getString("especialidad"));
                }
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al obtener especialidades: " + ex.getMessage());
        }
        return especialidades;
    }

    //metod listar empleados con filtro de estado y especialidad
    public List<Empleado> listarEmpleados(boolean estado, String especialidad) {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT idEmpleado, dni, puesto, apellido, nombre, telefono, fechaNacimiento, matricula, especialidad, estado "
                + "FROM empleado WHERE estado = ?";

        //Si la especialidad no es "Todas" añade la condición de filtro
        if (especialidad != null && !especialidad.equalsIgnoreCase("Todas") && !especialidad.isEmpty()) {
            sql += " AND especialidad = ?";
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int index = 1;
            ps.setBoolean(index++, estado);

            if (especialidad != null && !especialidad.equalsIgnoreCase("Todas") && !especialidad.isEmpty()) {
                ps.setString(index, especialidad);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Empleado empleado = new Empleado();
                empleado.setIdEmpleado(rs.getInt("idEmpleado"));
                empleado.setDni(rs.getInt("dni"));
                empleado.setPuesto(rs.getString("puesto"));
                empleado.setApellido(rs.getString("apellido"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setTelefono(rs.getString("telefono"));

                Date date = rs.getDate("fechaNacimiento");
                LocalDate fechaNacimiento = date == null ? null : date.toLocalDate();
                empleado.setFechaNacimiento(fechaNacimiento);

                empleado.setMatricula(rs.getString("matricula"));
                empleado.setEspecialidad(rs.getString("especialidad"));
                empleado.setEstado(rs.getBoolean("estado"));

                empleados.add(empleado);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al listar empleados: " + ex.getMessage());
        }
        return empleados;
    }

    public List<Empleado> listarTodosLosEmpleados() {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT idEmpleado, dni, puesto, apellido, nombre, telefono, "
                + "fechaNacimiento, matricula, especialidad, estado "
                + "FROM empleado";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Empleado empleado = new Empleado();
                empleado.setIdEmpleado(rs.getInt("idEmpleado"));
                empleado.setDni(rs.getInt("dni"));
                empleado.setPuesto(rs.getString("puesto"));
                empleado.setApellido(rs.getString("apellido"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setTelefono(rs.getString("telefono"));

                Date date = rs.getDate("fechaNacimiento");
                LocalDate fechaNacimiento = (date != null) ? date.toLocalDate() : null;
                empleado.setFechaNacimiento(fechaNacimiento);

                empleado.setMatricula(rs.getString("matricula"));
                empleado.setEspecialidad(rs.getString("especialidad"));
                empleado.setEstado(rs.getBoolean("estado"));

                empleados.add(empleado);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al listar empleados: " + ex.getMessage());
        }

        return empleados;
    }

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

            String matricula = empleado.getMatricula();
            if (matricula != null && !matricula.isEmpty()) {
                ps.setString(7, matricula);
            } else {
                ps.setNull(7, java.sql.Types.VARCHAR);
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
                String matricula = rs.getString("matricula");
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

    public Empleado buscarEmpleadoPorId(int idEmpleado) {
        Empleado empleado = null;
        String sql = "SELECT idEmpleado, dni, nombre, apellido, telefono, fechaNacimiento, puesto, matricula, especialidad, estado FROM empleado WHERE idEmpleado = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idEmpleado);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("idEmpleado");
                int dni = rs.getInt("dni");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String telefono = rs.getString("telefono");
                Date fechaNacimientoSql = rs.getDate("fechaNacimiento");
                LocalDate fechaNacimiento = fechaNacimientoSql.toLocalDate();
                String puesto = rs.getString("puesto");
                String matricula = rs.getString("matricula");
                String especialidad = rs.getString("especialidad");
                boolean estado = rs.getBoolean("estado");

                empleado = new Empleado(id, dni, puesto, apellido, nombre, telefono, fechaNacimiento, matricula, especialidad, estado);
            }
            ps.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al buscar empleado por ID: " + ex.getMessage());
        }
        return empleado;
    }

    public Empleado buscarEmpleadoPorMatricula(String matricula) {
        Empleado emp = null;
        try {
            String sql = "SELECT * FROM empleado WHERE matricula=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, matricula);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                emp = new Empleado();

                emp.setIdEmpleado(rs.getInt("idEmpleado"));
                emp.setMatricula(matricula);
                emp.setApellido(rs.getString("apellido"));
                emp.setNombre(rs.getString("nombre"));
                emp.setTelefono(rs.getString("telefono"));
                emp.setEspecialidad(rs.getString("especialidad"));

                emp.setEstado(rs.getBoolean("estado"));

            } else {
                JOptionPane.showMessageDialog(null, "Empleado no encontrado");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la Base de Datos al buscar Empleado: " + ex.getMessage());
        }
        return emp;
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

    public void cambiarEstadoEmpleado(int id, boolean nuevoEstado) {
        String sql = "UPDATE empleado SET estado = ? WHERE idEmpleado = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setBoolean(1, nuevoEstado);
            ps.setInt(2, id);
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                String mensaje = nuevoEstado ? "alta" : "baja";
                JOptionPane.showMessageDialog(null, "Se dio de " + mensaje + " al empleado correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el empleado.");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al cambiar estado: " + ex.getMessage());
        }
    }

    public Empleado validarLogin(String usuario, String pass) {

        Empleado empleado = null;

        try {

            String sqlUser = "SELECT * FROM empleado WHERE usuario = ?";//Buscar por usuario
            PreparedStatement psUser = con.prepareStatement(sqlUser);
            psUser.setString(1, usuario);
            ResultSet rsUser = psUser.executeQuery();

            if (!rsUser.next()) {
                return new Empleado(-1); //usuario no existeFlag para usuario inexistente
            }

            //Ahora verificar contraseña
            if (!rsUser.getString("pass").equals(pass)) {
                return new Empleado(-2); //Flag para contraseña incorrecta
            }

            //Verificar estado
            if (rsUser.getInt("estado") == 0) {
                return new Empleado(0); //Flag para usuario deshabilitado
            }

            //Usuario válido cargar datos completos
            empleado = new Empleado();
            empleado.setIdEmpleado(rsUser.getInt("idEmpleado"));
            empleado.setDni(rsUser.getInt("dni"));
            empleado.setNombre(rsUser.getString("nombre"));
            empleado.setApellido(rsUser.getString("apellido"));
            empleado.setPuesto(rsUser.getString("puesto"));
            empleado.setMatricula(rsUser.getString("matricula"));
            empleado.setEspecialidad(rsUser.getString("especialidad"));
            empleado.setEstado(rsUser.getBoolean("estado"));

            psUser.close();

        } catch (SQLException ex) {
            System.out.println("Error en validarLogin: " + ex.getMessage());
        }

        return empleado;
    }
}
