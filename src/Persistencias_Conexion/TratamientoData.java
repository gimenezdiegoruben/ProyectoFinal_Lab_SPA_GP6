package Persistencias_Conexion;

import Modelos.Tratamiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */
public class TratamientoData {

    private Connection con = null;

    public TratamientoData() {
        con = Conexion.getConexion();
    }

    public void guardarTratamiento(Tratamiento tratamiento) {
        String sql = "INSERT INTO tratamiento (nombre,tipo, detalle, productos, duracion, costo, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = null;
        con = Conexion.getConexion();

        try {
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, tratamiento.getNombre());
            ps.setString(2, tratamiento.getTipo());
            ps.setString(3, tratamiento.getDetalle());
            ps.setString(4, tratamiento.getProductosComoTexto());//guardamos la lista como texto
            ps.setInt(5, tratamiento.getDuracion());
            ps.setDouble(6, tratamiento.getCosto());
            ps.setBoolean(7, tratamiento.isEstado());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {

                ResultSet rs = ps.getGeneratedKeys();

                if (rs.next()) {
                    tratamiento.setCodTratam(rs.getInt(1));
                    JOptionPane.showMessageDialog(null, "Tratamiento '" + tratamiento.getNombre() + "' añadido con éxito.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla Tratamiento: " + ex.getMessage());

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error al cerrar el PreparedStatement o la Conexión: " + ex.getMessage());
            }
        }
    }

    public Tratamiento buscarTratamientoPorCodigo(int codTratam) {
        Tratamiento tratamiento = null;
        String sql = "SELECT * FROM tratamiento WHERE codTratam = ?";

        Connection con = Conexion.getConexion();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, codTratam);
            rs = ps.executeQuery();

            if (rs.next()) {
                tratamiento = new Tratamiento();
                tratamiento.setCodTratam(codTratam);
                tratamiento.setNombre(rs.getString("nombre"));
                tratamiento.setTipo(rs.getString("tipo"));
                tratamiento.setDetalle(rs.getString("detalle"));

                String productosTexto = rs.getString("productos");
                tratamiento.setProductosDesdeTexto(productosTexto);

                tratamiento.setDuracion(rs.getInt("duracion"));
                tratamiento.setCosto(rs.getDouble("costo"));
                tratamiento.setEstado(rs.getBoolean("estado"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla Tratamiento: " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error al cerrar el PreparedStatement o la Conexión: " + ex.getMessage());
            }
        }
        return tratamiento;
    }

    public void modificarTratamiento(Tratamiento tratamiento) {
        String sql = "UPDATE tratamiento SET nombre=?, tipo=?, detalle=?, productos=?, duracion=?, costo=?, estado=? WHERE codTratam=?";

        PreparedStatement ps = null;
        con = Conexion.getConexion();

        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, tratamiento.getNombre());
            ps.setString(2, tratamiento.getTipo());
            ps.setString(3, tratamiento.getDetalle());
            ps.setString(4, tratamiento.getProductosComoTexto());
            ps.setInt(5, (int) tratamiento.getDuracion());
            ps.setDouble(6, tratamiento.getCosto());
            ps.setBoolean(7, tratamiento.isEstado());
            ps.setInt(8, tratamiento.getCodTratam());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Tratamiento modificado con éxito.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el tratamiento para modificar.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al modificar el Tratamiento: " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error al cerrar el PreparedStatement o la Conexión: " + ex.getMessage());
            }
        }
    }

    public void eliminarTratamiento(int codTratam) {

        String sql = "UPDATE tratamiento SET estado = 0 WHERE codTratam = ?";

        PreparedStatement ps = null;
        con = Conexion.getConexion();

        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, codTratam);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Tratamiento dado de baja (inactivo) con éxito.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el tratamiento con ese código.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al dar de baja el Tratamiento: " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error al cerrar el PreparedStatement o la Conexión: " + ex.getMessage());
            }
        }
    }

    public List<Tratamiento> listarTratamientos() {
        List<Tratamiento> tratamientos = new ArrayList<>();
        // Solo lista los tratamientos activos (estado = 1)
        String sql = "SELECT * FROM tratamiento WHERE estado = 1";

        PreparedStatement ps = null;
        ResultSet rs = null;
        con = Conexion.getConexion();

        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Tratamiento tratamiento = new Tratamiento();

                tratamiento.setCodTratam(rs.getInt("codTratam"));
                tratamiento.setNombre(rs.getString("nombre"));
                tratamiento.setTipo(rs.getString("tipo"));
                tratamiento.setDetalle(rs.getString("detalle"));

                String productosTexto = rs.getString("productos");
                tratamiento.setProductosDesdeTexto(productosTexto);

                tratamiento.setDuracion(rs.getInt("duracion"));
                tratamiento.setCosto(rs.getDouble("costo"));
                tratamiento.setEstado(rs.getBoolean("estado"));

                tratamientos.add(tratamiento);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al listar tratamientos: " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error al cerrar el PreparedStatement o la Conexión: " + ex.getMessage());
            }
        }
        return tratamientos;
    }

    public List<Tratamiento> listarTratamientosInactivos() {
        String sql = "SELECT * FROM tratamiento WHERE estado = 0";

        ArrayList<Tratamiento> lista = new ArrayList<>();
        con = Conexion.getConexion();

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Tratamiento t = new Tratamiento();
                t.setCodTratam(rs.getInt("codTratam"));
                t.setNombre(rs.getString("nombre"));
                t.setTipo(rs.getString("tipo"));

                String productosTexto = rs.getString("productos");
                t.setProductosDesdeTexto(productosTexto);

                t.setDuracion(rs.getInt("duracion"));
                t.setCosto(rs.getDouble("costo"));
                t.setEstado(rs.getBoolean("estado"));

                lista.add(t);
            }
            ps.close();
        } catch (SQLException ex) {
            System.err.println("Error al cerrar el PreparedStatement o la Conexión: " + ex.getMessage());
        }
        return lista;
    }

    public List<Tratamiento> listarTratameintosActivos() {
        String sql = "SELECT * FROM tratamieto WHERE estado = 1";

        ArrayList<Tratamiento> lista = new ArrayList<>();
        con = Conexion.getConexion();

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Tratamiento t = new Tratamiento();
                t.setCodTratam(rs.getInt(("codTratam")));
                t.setNombre(rs.getString("nombre"));
                t.setTipo(rs.getString("tipo"));

                String productosTexto = rs.getString("productos");
                t.setProductosDesdeTexto(productosTexto);

                t.setDuracion(rs.getInt("duracion"));
                t.setCosto(rs.getDouble("costo"));
                t.setEstado(true);

                lista.add(t);
            }
            ps.close();
        } catch (SQLException ex) {
            System.err.println("Error al cerrar el PreparedStatement o la Conexión: " + ex.getMessage());
        }
        return lista;
    }

    public void cambiarEstadoTratamiento(int codTratam, boolean nuevo) {
        String sql = "UPDATE tratamiento set estado = ? WHERE codTratam=?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setBoolean(1, nuevo);
            ps.setInt(2, codTratam);

            int f = ps.executeUpdate();

            if (f > 0) {
                String msg = "Tratamiento dado de BAJA correctamente";
                if (nuevo) {
                    msg = "Tratamiento dado de ALTA correctamente";
                }
                JOptionPane.showMessageDialog(null, msg);
            } else {
                JOptionPane.showMessageDialog(null, "No se encontro ningun tratamiento para cambiarle el estado");
            }
        } catch (SQLException ex) {
            System.err.println("Error al cerrar el PreparedStatement o la Conexión: " + ex.getMessage());
        }
    }
}
