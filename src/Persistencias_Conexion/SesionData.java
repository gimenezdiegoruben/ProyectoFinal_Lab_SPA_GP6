/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Persistencias_Conexion;

import Modelos.Instalacion;
import Modelos.Masajista;
import Modelos.Sesion;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SesionData {
    private Connection con = null;

    public SesionData() {
        
        con = Conexion.getConexion();
    }
    public void crearSesion(Sesion sesion){
        String sql = "INSERT INTO sesion(codTratam , fechaHoraIniciio ,fechaHoraFinal,nroConsultorio , matricula , codInstal , codPack,estado)"
                + "VALUES (? ,?, ?, ?, ? ,?,?,?)";
        
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql ,Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, sesion.getTratamiento().getCodTratam());
            ps.setTimestamp(2,Timestamp.valueOf(sesion.getFechaHoraInicio()));
            ps.setTimestamp(3, Timestamp.valueOf(sesion.getFechaHoraFinal()));
            ps.setInt(4,sesion.getConsultorio().getNroConsultorio());
            ps.setInt(5, sesion.getMasajista().getMatricula());
            ps.setInt(6,sesion.getConsultorio().getNroConsultorio());
            ps.setInt(7, sesion.getCodPack());
            ps.setBoolean(8,sesion.getEstado());
            
            ps.executeUpdate();
            ResultSet rs= ps.getGeneratedKeys();
            if(rs.next()){
                sesion.setCodSesion(rs.getInt(1));
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar la tabla sesion");
        }
        
    }
    public void modificarSesion(Sesion sesion){
        String sql = "UPDATE sesion SET fechaHoraInicio = ?,fechaHoraFinal= ?, nroConsultorio=?,matricula=?,codInstal=?,codPack=?,estado=?"
                + "WHERE codSesion";
            
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(sesion.getFechaHoraInicio()));
            ps.setTimestamp(2, Timestamp.valueOf((sesion.getFechaHoraFinal())));
            ps.setInt(3, sesion.getConsultorio().getNroConsultorio());
            ps.setInt(4, sesion.getMasajista().getMatricula());
            ps.setInt(5,sesion.getConsultorio().getNroConsultorio());
            ps.setInt(6, sesion.getCodPack().getCodPack());
            ps.setBoolean(7,sesion.getEstado());
            
            int exito = ps.executeUpdate();
            if(exito > 0){
                JOptionPane.showMessageDialog(null, "sesion modificado");
            }
            
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla sesion"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
        
    }
    public void eliminarSesion(int codSesion){
        String sql = "DELETE FROM sesion WHERE codSesion=? ";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, codSesion);
            
            int exito = ps.executeUpdate();
            if(exito > 0 ){
                JOptionPane.showMessageDialog(null, "Se elimino correctamente la sesion");
            }else{
                JOptionPane.showMessageDialog(null, "No se encontro una sesion con ese codigo");
            }
            ps.close();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar sesion"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
        }
    public List<Sesion> listarSesiones(){
        ArrayList<Sesion> lista=new ArrayList<>();
        
        String sql="SELECT * FROM sesion";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                
                Sesion sesi=new Sesion();
                sesi.setCodSesion(rs.getInt("codSesion"));
                sesi.setTratamiento((rs.getInt("codTratam")));
                sesi.setFechaHoraInicio(rs.getTimestamp("fechaHoraInicio").toLocalDateTime());
                sesi.setFechaHoraFinal(rs.getTimestamp("fechaHoraFinal").toLocalDateTime());
                sesi.setConsultorio(rs.getInt("nroConsultorio"));
                sesi.setMatricula(rs.getInt("matricula"));
                sesi.setCodInstal(rs.getInt("codInstal"));
                sesi.setCodPack(rs.getInt("codPack"));
                sesi.setEstado(rs.getBoolean("estado"));
                
                lista.add(sesi);
            }
            
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla sesion"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
        return lista;
    }
    public void anularSesion(int codSesion){
        String sql="UPDATE sesion SET estado = 0 WHERE codSesion=?";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, codSesion);
            
            int exito = ps.executeUpdate();
            if(exito > 0){
                JOptionPane.showMessageDialog(null, "Sesion anulada Correctamente");     
            }else{
                JOptionPane.showMessageDialog(null, "No se encontro una sesion con ese codigo");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla sesion"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
    }
    public void asignarMasajista(int codSesion, int matricula)
    {
        String sql="UPDATE sesion SET matricula = ? WHERE codSesion = ?";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, matricula);
            ps.setInt(2, codSesion);
            
            int exito = ps.executeUpdate();
            
            if(exito > 0 ){
                JOptionPane.showMessageDialog(null, "Masajista asignado correctamente a la sesion");
            }else{
                JOptionPane.showMessageDialog(null, "No se encontro el codigo de la sesion");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla sesion"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
    }
    public List<Masajista> listarMasajistasLibres(LocalDateTime desde,LocalDateTime hasta){
        ArrayList<Masajista> lista = new ArrayList<>();
        
        String sql = " SELECT * \n" +
"        FROM empleado \n" +
"        WHERE estado = 1\n" +
"        AND matricula NOT IN (\n" +
"            SELECT matricula \n" +
"            FROM sesion \n" +
"            WHERE estado = 1\n" +
"            AND (\n" +
"                (fechaHoraInicio < ? AND fechaHoraFinal > ?)  -- solapado\n" +
"                OR (fechaHoraInicio BETWEEN ? AND ?)\n" +
"                OR (fechaHoraFinal BETWEEN ? AND ?)\n" +
"            )\n" +
"        )"; 
    
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(hasta));
            ps.setTimestamp(2, Timestamp.valueOf(desde));
            ps.setTimestamp(3, Timestamp.valueOf(desde));
            ps.setTimestamp(4, Timestamp.valueOf(hasta));
            ps.setTimestamp(5, Timestamp.valueOf(desde));
            ps.setTimestamp(6, Timestamp.valueOf(hasta));
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Masajista m = new Masajista();
                m.setMatricula(rs.getInt("matricula"));
                m.setNombre(rs.getString("nombre"));
                m.setTelefono(rs.getInt("telefono"));
                m.setEspecialidad(rs.getString("especialidad"));
                m.setEstado(rs.getBoolean("estado"));
                lista.add(m);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla sesion"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
    
        return lista;
    }     
    public List<Instalacion> listarInstalacionesLibres(LocalDateTime desde ,LocalDateTime hasta){
        ArrayList<Instalacion> lista = new ArrayList<>();
        
        String sql = "SELECT * \n" +
"FROM instalacion\n" +
"WHERE estado = 1\n" +
"AND codInstal NOT IN (\n" +
"    SELECT codInstal \n" +
"    FROM sesion\n" +
"    WHERE estado = 1\n" +
"    AND (\n" +
"        (fechaHoraInicio < ? AND fechaHoraFinal > ?) \n" +
"        OR (fechaHoraInicio BETWEEN ? AND ?) \n" +
"        OR (fechaHoraFinal BETWEEN ? AND ?)\n" +
"    )\n" +
")";
    
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setTimestamp(1,Timestamp.valueOf(hasta));
            ps.setTimestamp(2,Timestamp.valueOf(desde));
            ps.setTimestamp(3,Timestamp.valueOf(hasta));
            ps.setTimestamp(4,Timestamp.valueOf(desde));
            ps.setTimestamp(5,Timestamp.valueOf(hasta));
            ps.setTimestamp(6,Timestamp.valueOf(desde));
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Instalacion ins = new Instalacion();
                ins.setCodInstal(rs.getInt("codInstal"));
                ins.setNombre(rs.getString("nombre"));
                ins.setDetalleUso(rs.getString("detalleUso"));
                ins.setPrecio(rs.getDouble("precio30min"));
                ins.setEstado(rs.getBoolean("estado"));
                lista.add(ins);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla sesion"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
        return lista;
    }
}
