package Persistencias_Conexion;

import Modelos.Consultorio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */
public class ConsultorioData {

    private Connection con = null;

    public ConsultorioData() {

    }

    public void nuevoConsultorio(Consultorio consultorio) {

        String sql = "INSERT INTO consultorio (usos, equipamiento, apto) VALUES (?, ?, ?)";
        PreparedStatement ps = null;
        con = Conexion.getConexion();

        try {

            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, consultorio.getUsos());
            ps.setString(2, consultorio.getEquipamiento());
            ps.setBoolean(3, consultorio.isApto());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    consultorio.setNroConsultorio(rs.getInt(1));
                    System.out.println("Consultorio " + consultorio.getNroConsultorio() + " añadido con éxito.");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al acceder a la base de datos " + ex.getMessage());
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

    public void modificarConsultorio(Consultorio consultorio) {

        String sql = "UPDATE consultorio SET usos = ?, equipamiento = ?, apto = ? WHERE nroConsultorio = ?";
        PreparedStatement ps = null;
        con = Conexion.getConexion();

        try {

            ps = con.prepareStatement(sql);
            ps.setInt(1, consultorio.getUsos());
            ps.setString(2, consultorio.getEquipamiento());
            ps.setBoolean(3, consultorio.isApto());
            ps.setInt(4, consultorio.getNroConsultorio());

            int exito = ps.executeUpdate();
            if (exito == 1) {
                System.out.println("Consultorio modificado con éxito.");
            } else {
                System.out.println("El número de consultorio ingresado no esta vinculado a ningun consultorio.");
            }
        } catch (SQLException ex) {
            System.out.println("Error al acceder a la base de datos " + ex.getMessage());
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

    public Consultorio buscarConsultorio(int nroConsultorio) {

        String sql = "SELECT * FROM consultorio WHERE nroConsultorio = ?";
        PreparedStatement ps = null;
        con = Conexion.getConexion();
        Consultorio consultorio = null;

        try {

            ps = con.prepareStatement(sql);
            ps.setInt(1, nroConsultorio);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                consultorio = new Consultorio();
                consultorio.setNroConsultorio(nroConsultorio);
                consultorio.setUsos(rs.getInt("usos"));
                consultorio.setEquipamiento(rs.getString("equipamiento"));
                consultorio.setApto(rs.getBoolean("apto"));
            } else {
                System.out.println("El número de consultorio ingresado no esta vinculado a ningun consultorio.");
            }
        } catch (SQLException ex) {
            System.out.println("Error al acceder a la base de datos " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error al cerrar el PreparedStatement: " + ex.getMessage());
            }
        }

        return consultorio;
    }

    public List<Consultorio> listarConsultorios() {

        String sql = "SELECT * FROM consultorio WHERE apto = 1";
        PreparedStatement ps = null;
        con = Conexion.getConexion();
        List<Consultorio> consultorios = new ArrayList<>();

        try {

            ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Consultorio consultorio = new Consultorio();
                consultorio.setNroConsultorio(rs.getInt("nroConsultorio"));
                consultorio.setUsos(rs.getInt("usos"));
                consultorio.setEquipamiento(rs.getString("equipamiento"));
                consultorio.setApto(rs.getBoolean("apto"));
                consultorios.add(consultorio);
            }
        } catch (SQLException ex) {
            System.out.println("Error al acceder a la base de datos " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error al cerrar el PreparedStatement: " + ex.getMessage());
            }
        }

        return consultorios;
    }

    public void eliminarConsultorio(int nroConsultorio) {

        String sql = "DELETE FROM consultorio WHERE nroConsultorio = ?";
        PreparedStatement ps = null;
        con = Conexion.getConexion();

        try {

            ps = con.prepareStatement(sql);
            ps.setInt(1, nroConsultorio);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error al acceder a la base de datos " + ex.getMessage());
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
}
