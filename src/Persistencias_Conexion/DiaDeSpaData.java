package Persistencias_Conexion;

import Modelos.Cliente;
import Modelos.DiaDeSpa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */
public class DiaDeSpaData {
    
    private Connection con = null;
    
    public DiaDeSpaData() {
        
    }
    
    public void nuevoDiaDeSpa(DiaDeSpa diaDeSpa){
        
        String sql = "INSERT INTO dia_de_spa (fechayhoraCompra, preferencias, codCli, monto, estado) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        con = Conexion.getConexion();
        ResultSet rs = null;
        
        try { 
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(diaDeSpa.getFechayhora()));
            ps.setString(2, diaDeSpa.getPreferencias());
            ps.setInt(3, diaDeSpa.getCliente().getCodCli());
            ps.setDouble(4, diaDeSpa.getMonto());
            ps.setBoolean(5, diaDeSpa.isEstado());
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    diaDeSpa.setCodPack(rs.getInt(1));
                    System.out.println("Pack " + diaDeSpa.getCodPack() + " añadido con éxito.");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al acceder a la base de datos " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }
    }
    
    public void modificarDiaDeSpa(DiaDeSpa diaDeSpa){
        
        String sql = "UPDATE dia_de_spa SET fechayhoraCompra = ?, preferencias = ?, codCli = ?, monto = ?, estado = ? WHERE codPack = ?";
        PreparedStatement ps = null;
        con = Conexion.getConexion();
        
        try {
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(diaDeSpa.getFechayhora()));
            ps.setString(2, diaDeSpa.getPreferencias());
            ps.setInt(3, diaDeSpa.getCliente().getCodCli());
            ps.setDouble(4, diaDeSpa.getMonto());
            ps.setBoolean(5, diaDeSpa.isEstado());
            ps.setInt(6, diaDeSpa.getCodPack());
            int exito = ps.executeUpdate();
            
            if (exito == 1) {
                System.out.println("Turno modificado con éxito.");
            } else {
                System.out.println("El número de turno ingresado no esta vinculado a ningun turno.");
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
    
    public void asignarMonto(DiaDeSpa diaDeSpa, double monto) {
        String sql = "UPDATE dia_de_spa SET monto = ? WHERE codPack = ?";
        PreparedStatement ps = null;
        con = Conexion.getConexion();
        
        try {
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, monto);
            ps.setInt(2, diaDeSpa.getCodPack());
            int exito = ps.executeUpdate();
            
            if (exito == 1) {
                System.out.println("Monto agregado con éxito.");
            } else {
                System.out.println("El monto no se ha podido agregar, el número de turno no existe.");
            }
        } catch (SQLException ex) {
            System.out.println("Error al acceder a la base de datos " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            }catch (SQLException ex) {
                    System.out.println("Error al cerrar el PreparedStatement: " + ex.getMessage());
                }
            }
        }
    
    public DiaDeSpa buscarDiaDeSpa(int codPack) {
        String sql = "SELECT * FROM dia_de_spa WHERE codPack = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        con = Conexion.getConexion();
        DiaDeSpa diaDeSpa = null;

        try {

            ps = con.prepareStatement(sql);
            ps.setInt(1, codPack);
            rs = ps.executeQuery();

            if (rs.next()) {
                diaDeSpa = new DiaDeSpa();
                diaDeSpa.setCodPack(codPack);
                diaDeSpa.setFechayhora(rs.getTimestamp("fechayhoraCompra").toLocalDateTime());
                diaDeSpa.setPreferencias(rs.getString("preferencias"));
                Cliente c1 = new Cliente();
                c1.setCodCli(rs.getInt("codCli"));
                diaDeSpa.setCliente(c1);
                diaDeSpa.setMonto(rs.getDouble("monto"));
                diaDeSpa.setEstado(rs.getBoolean("estado"));
                
            } else {
                System.out.println("El número de turno ingresado no esta vinculado a ningun turno.");
            }
        } catch (SQLException ex) {
            System.out.println("Error al acceder a la base de datos " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }
        return diaDeSpa;
    }
    
    public List<DiaDeSpa> listarDiaDeSpa() {

        String sql = "SELECT * FROM dia_de_spa";
        PreparedStatement ps = null;
        ResultSet rs = null;
        con = Conexion.getConexion();
        List<DiaDeSpa> paquetes = new ArrayList<>();

        try {

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                DiaDeSpa diaDeSpa = new DiaDeSpa();
                diaDeSpa.setCodPack(rs.getInt("codPack"));
                diaDeSpa.setFechayhora(rs.getTimestamp("fechayhoraCompra").toLocalDateTime());
                diaDeSpa.setPreferencias(rs.getString("preferencias"));
                Cliente c1 = new Cliente();
                c1.setCodCli(rs.getInt("codCli"));
                diaDeSpa.setCliente(c1);
                diaDeSpa.setMonto(rs.getDouble("monto"));
                diaDeSpa.setEstado(rs.getBoolean("estado"));
                paquetes.add(diaDeSpa);
            }
        } catch (SQLException ex) {
            System.out.println("Error al acceder a la base de datos " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }

        return paquetes;
    }
    public List<DiaDeSpa> listarDiaDeSpaActivos() {

        String sql = "SELECT * FROM dia_de_spa WHERE estado = 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        con = Conexion.getConexion();
        List<DiaDeSpa> paquetes = new ArrayList<>();

        try {

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                DiaDeSpa diaDeSpa = new DiaDeSpa();
                diaDeSpa.setCodPack(rs.getInt("codPack"));
                diaDeSpa.setFechayhora(rs.getTimestamp("fechayhoraCompra").toLocalDateTime());
                diaDeSpa.setPreferencias(rs.getString("preferencias"));
                Cliente c1 = new Cliente();
                c1.setCodCli(rs.getInt("codCli"));
                diaDeSpa.setCliente(c1);
                diaDeSpa.setMonto(rs.getDouble("monto"));
                diaDeSpa.setEstado(rs.getBoolean("estado"));
                paquetes.add(diaDeSpa);
            }
        } catch (SQLException ex) {
            System.out.println("Error al acceder a la base de datos " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }

        return paquetes;
    }
    
    public List<DiaDeSpa> listarDiaDeSpaInactivos() {

        String sql = "SELECT * FROM dia_de_spa WHERE estado = 0";
        PreparedStatement ps = null;
        ResultSet rs = null;
        con = Conexion.getConexion();
        List<DiaDeSpa> paquetes = new ArrayList<>();

        try {

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                DiaDeSpa diaDeSpa = new DiaDeSpa();
                diaDeSpa.setCodPack(rs.getInt("codPack"));
                diaDeSpa.setFechayhora(rs.getTimestamp("fechayhoraCompra").toLocalDateTime());
                diaDeSpa.setPreferencias(rs.getString("preferencias"));
                Cliente c1 = new Cliente();
                c1.setCodCli(rs.getInt("codCli"));
                diaDeSpa.setCliente(c1);
                diaDeSpa.setMonto(rs.getDouble("monto"));
                diaDeSpa.setEstado(rs.getBoolean("estado"));
                paquetes.add(diaDeSpa);
            }
        } catch (SQLException ex) {
            System.out.println("Error al acceder a la base de datos " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }

        return paquetes;
    }

    public void eliminarDiaDeSpa(int codPack) {

        String sql = "DELETE FROM dia_de_spa WHERE codPack = ?";
        PreparedStatement ps = null;
        con = Conexion.getConexion();

        try {

            ps = con.prepareStatement(sql);
            ps.setInt(1, codPack);
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
