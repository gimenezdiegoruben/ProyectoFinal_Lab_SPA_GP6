/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Persistencias_Conexion;

import Modelos.Tratamiento;
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
 *
 * @author Ger
 */
public class TratamientoData {
      private Connection con = null;

      public TratamientoData() {

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
            ps.setInt(4, tratamiento.getProductos());
            ps.setInt(5, tratamiento.getDuracion());
            ps.setInt(6, tratamiento.getCosto());
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
        String sql = "SELECT nombre, tipo, detalle, productos, duracion, costo, estado FROM tratamiento WHERE codTratam = ?";
        
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
                tratamiento.setDuracion(rs.getInt("duracion"));
                tratamiento.setCosto(rs.getInt("costo"));
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
      
      
}
