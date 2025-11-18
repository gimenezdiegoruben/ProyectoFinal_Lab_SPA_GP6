package Persistencias_Conexion;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */
import Modelos.Instalacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class InstalacionData {

    private Connection con = null;

    public InstalacionData() {
        con = Conexion.getConexion();
    }

    public void crearInstalacion(Instalacion instalacion) {
        String sql = "INSERT INTO instalacion (nombre, detalleUso, precio30min, estado) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, instalacion.getNombre());
            ps.setString(2, instalacion.getDetalleUso());
            ps.setDouble(3, instalacion.getPrecio());
            ps.setBoolean(4, instalacion.isEstado());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                instalacion.setCodInstal(rs.getInt(1));
                JOptionPane.showMessageDialog(null, "Instalación agregada con código: " + instalacion.getCodInstal());
            }
            ps.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla instalacion (crear): " + ex.getMessage());
        }
    }

    public Instalacion buscarInstalacionPorCod(int codInstal) {
        Instalacion instal = null;
        String sql = "SELECT codInstal, nombre, detalleUso, precio30min, estado FROM instalacion WHERE codInstal = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, codInstal);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                instal = new Instalacion();
                instal.setCodInstal(rs.getInt("codInstal"));
                instal.setNombre(rs.getString("nombre"));
                instal.setDetalleUso(rs.getString("detalleUso"));
                instal.setPrecio(rs.getDouble("precio30min"));
                instal.setEstado(rs.getBoolean("estado"));
            }
            ps.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al buscar Instalación por código: " + ex.getMessage());
        }
        return instal;
    }

    public List<Instalacion> listarTodasInstalaciones() {
        List<Instalacion> instalaciones = new ArrayList<>();
        String sql = "SELECT codInstal, nombre, detalleUso, precio30min, estado FROM instalacion";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Instalacion instal = new Instalacion();
                instal.setCodInstal(rs.getInt("codInstal"));
                instal.setNombre(rs.getString("nombre"));
                instal.setDetalleUso(rs.getString("detalleUso"));
                instal.setPrecio(rs.getDouble("precio30min"));
                instal.setEstado(rs.getBoolean("estado"));

                instalaciones.add(instal);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al listar Instalaciones: " + ex.getMessage());
        }
        return instalaciones;
    }

    public List<Instalacion> listarTodasInstalacionesActivas() {
        List<Instalacion> instalaciones = new ArrayList<>();
        String sql = "SELECT codInstal, nombre, detalleUso, precio30min, estado FROM instalacion WHERE estado = 1";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Instalacion instal = new Instalacion();
                instal.setCodInstal(rs.getInt("codInstal"));
                instal.setNombre(rs.getString("nombre"));
                instal.setDetalleUso(rs.getString("detalleUso"));
                instal.setPrecio(rs.getDouble("precio30min"));
                instal.setEstado(rs.getBoolean("estado"));

                instalaciones.add(instal);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al listar Instalaciones: " + ex.getMessage());
        }
        return instalaciones;
    }

    public List<Instalacion> listarTodasInstalacionesInactivas() {
        List<Instalacion> instalaciones = new ArrayList<>();
        String sql = "SELECT codInstal, nombre, detalleUso, precio30min, estado FROM instalacion WHERE estado = 0";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Instalacion instal = new Instalacion();
                instal.setCodInstal(rs.getInt("codInstal"));
                instal.setNombre(rs.getString("nombre"));
                instal.setDetalleUso(rs.getString("detalleUso"));
                instal.setPrecio(rs.getDouble("precio30min"));
                instal.setEstado(rs.getBoolean("estado"));

                instalaciones.add(instal);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al listar Instalaciones: " + ex.getMessage());
        }
        return instalaciones;
    }

    public void modificarInstalacion(Instalacion instalacion) {
        String sql = "UPDATE instalacion SET nombre = ?, detalleUso = ?, precio30min = ?, estado = ? WHERE codInstal = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, instalacion.getNombre());
            ps.setString(2, instalacion.getDetalleUso());
            ps.setDouble(3, instalacion.getPrecio());
            ps.setBoolean(4, instalacion.isEstado());
            ps.setInt(5, instalacion.getCodInstal());

            int exito = ps.executeUpdate();

            if (exito > 0) {
                JOptionPane.showMessageDialog(null, "Instalación modificada con éxito.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró la Instalación para modificar.");
            }
            ps.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla instalacion (modificar): " + ex.getMessage());
        }
    }

    public void darDeBajaInstalacion(int codInstal) {
        String sql = "UPDATE instalacion SET estado = 0 WHERE codInstal = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, codInstal);

            int exito = ps.executeUpdate();

            if (exito > 0) {
                JOptionPane.showMessageDialog(null, "Instalación dada de baja correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró la Instalación con ese código.");
            }
            ps.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla instalacion (baja): " + ex.getMessage());
        }
    }

    public void eliminarInstalacion(int codInstal) {
        String sql = "DELETE FROM instalacion WHERE codInstal = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, codInstal);

            int exito = ps.executeUpdate();

            if (exito > 0) {
                JOptionPane.showMessageDialog(null, "Instalación eliminada permanentemente.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró la Instalación para eliminar.");
            }
            ps.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla instalacion (eliminar): " + ex.getMessage());
        }
    }
}
