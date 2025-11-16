package Persistencias_Conexion;

import Modelos.Cliente;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */

public class ClienteData {
    
    private Connection con = null;
    
    public ClienteData(){
        
        con = Conexion.getConexion();
    }
    public void guardarCliente(Cliente cliente){
        
        String sql = "INSERT INTO cliente(dni,nombre ,telefono ,edad ,afecciones ,estado, fechaNac) VALUES(? , ? , ? , ? , ? , ?, ?)";
        try {
        
        PreparedStatement ps= con.prepareStatement(sql ,Statement.RETURN_GENERATED_KEYS);
        ps.setLong(1, cliente.getDni());
        ps.setString(2, cliente.getNombre());
        ps.setLong(3, cliente.getTelefono());
        ps.setInt(4,cliente.getEdad());
        ps.setString(5, cliente.getAfecciones());
        ps.setBoolean(6, cliente.isEstado());
        ps.setDate(7, Date.valueOf(cliente.getFechaNac()));
        ps.executeUpdate();
        
        ResultSet rs= ps.getGeneratedKeys();
        if(rs.next()){
            cliente.setCodCli(rs.getInt(1));
            JOptionPane.showMessageDialog(null, "Cliente guardado");
        }
        ps.close();
        
        }catch(SQLException er) {
            //Error 1062 para la clave unica DNI
            if(er.getErrorCode() == 1062){
                JOptionPane.showMessageDialog(null, "Error: ya existe un cliente con el DNI ingresado:");
            }else{
                
            
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla cliente"+ er.getMessage());
            }
       }
    }

    public void modificarCliente(Cliente cliente){
        String sql = "UPDATE cliente SET dni = ?, nombre = ?,telefono = ? , edad = ?, afecciones = ?,estado = ?, fechaNac = ?"
                    + "WHERE codCli = ?";
            
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, cliente.getDni());
            ps.setString(2, cliente.getNombre());
            ps.setLong(3, cliente.getTelefono());
            ps.setInt(4, cliente.getEdad());
            ps.setString(5, cliente.getAfecciones());
            ps.setBoolean(6, cliente.isEstado());
            ps.setDate(7, Date.valueOf(cliente.getFechaNac()));
            ps.setInt(8 ,cliente.getCodCli());
            int exito=ps.executeUpdate();
            
            if(exito == 1){
                JOptionPane.showMessageDialog(null, "Cliente modificado");
            }
            
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla cliente"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
        
    }
    public void eliminarCliente(long dni){
        String sql = "DELETE FROM cliente WHERE dni = ? ";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, dni);
            int exito = ps.executeUpdate();//devuelve int
            if(exito == 1){
                 JOptionPane.showMessageDialog(null,"Cliente eliminado");
            }else{
                JOptionPane.showMessageDialog(null,"No se encontro ningun cliente con ese dni");
            }
            ps.close();
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Error al acceder a la tabla cliente"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
       }
    }
    public Cliente buscarPorId(int id){
        String sql = "SELECT dni, nombre, telefono , edad, afecciones, estado, fechaNac FROM cliente WHERE codCli = ?";
        Cliente cliente = null;    
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs= ps.executeQuery();
            if(rs.next()){
                 cliente = new Cliente();
                 cliente.setCodCli(id);
                 cliente.setDni(rs.getLong("dni"));
                 cliente.setNombre(rs.getString("nombre"));
                 cliente.setTelefono(rs.getLong("telefono"));
                 cliente.setEdad(rs.getInt("edad"));
                 cliente.setAfecciones(rs.getString("afecciones"));
                 cliente.setEstado(rs.getBoolean("estado"));
                 cliente.setFechaNac(rs.getDate("fechaNac").toLocalDate());
            }else {
                JOptionPane.showMessageDialog(null, "No existe un cliente con ese id");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla cliente"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
        return cliente;    
    }
    public Cliente buscarPorDni(long dni){
        String sql = "SELECT codCli, nombre, telefono , edad, afecciones, estado, fechaNac FROM cliente WHERE dni = ?";
        Cliente cliente = null;    
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, dni);
            ResultSet rs= ps.executeQuery();
            if(rs.next()){
                 cliente = new Cliente();
                 cliente.setCodCli(rs.getInt("codCli"));
                 cliente.setDni(dni);
                 cliente.setNombre(rs.getString("nombre"));
                 cliente.setTelefono(rs.getLong("telefono"));
                 cliente.setEdad(rs.getInt("edad"));
                 cliente.setAfecciones(rs.getString("afecciones"));
                 cliente.setEstado(rs.getInt("estado") == 1);
                 cliente.setFechaNac(rs.getDate("fechaNac").toLocalDate());
            }else {
                JOptionPane.showMessageDialog(null, "No existe un cliente con ese dni");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla cliente"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
        return cliente;
    }
    
    public List<Cliente> listarClientesActivos(){
        String sql = "SELECT codCli, dni ,nombre, telefono , edad, afecciones, estado, fechaNac FROM cliente WHERE estado = 1";
        ArrayList<Cliente> clientes = new ArrayList<>();    
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            ResultSet rs= ps.executeQuery();
            while(rs.next()){
                 Cliente cliente = new Cliente();
                 cliente.setCodCli(rs.getInt("codCli"));
                 cliente.setDni(rs.getLong("dni"));
                 cliente.setNombre(rs.getString("nombre"));
                 cliente.setTelefono(rs.getLong("telefono"));
                 cliente.setEdad(rs.getInt("edad"));
                 cliente.setAfecciones(rs.getString("afecciones"));
                 cliente.setEstado(true);
                 cliente.setFechaNac(rs.getDate("fechaNac").toLocalDate());
                 
                 clientes.add(cliente);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla cliente"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
        return clientes;
    }
    
    public List<Cliente> listarClientesInactivos(){
        String sql = "SELECT codCli, dni ,nombre, telefono , edad, afecciones, estado, fechaNac FROM cliente WHERE estado = 0";
        ArrayList<Cliente> clientes = new ArrayList<>();    
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            ResultSet rs= ps.executeQuery();
            while(rs.next()){
                 Cliente cliente = new Cliente();
                 cliente.setCodCli(rs.getInt("codCli"));
                 cliente.setDni(rs.getLong("dni"));
                 cliente.setNombre(rs.getString("nombre"));
                 cliente.setTelefono(rs.getLong("telefono"));
                 cliente.setEdad(rs.getInt("edad"));
                 cliente.setAfecciones(rs.getString("afecciones"));
                 cliente.setEstado(false);
                 cliente.setFechaNac(rs.getDate("fechaNac").toLocalDate());
                 
                 clientes.add(cliente);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla cliente"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
        return clientes;
    }
    
    public List<Cliente> listarClientes(){
        String sql = "SELECT codCli, dni ,nombre, telefono , edad, afecciones, estado, fechaNac FROM cliente";
        ArrayList<Cliente> clientes = new ArrayList<>();    
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            ResultSet rs= ps.executeQuery();
            while(rs.next()){
                 Cliente cliente = new Cliente();
                 cliente.setCodCli(rs.getInt("codCli"));
                 cliente.setDni(rs.getLong("dni"));
                 cliente.setNombre(rs.getString("nombre"));
                 cliente.setTelefono(rs.getLong("telefono"));
                 cliente.setEdad(rs.getInt("edad"));
                 cliente.setAfecciones(rs.getString("afecciones"));
                 cliente.setEstado(rs.getInt("estado") == 1);
                 cliente.setFechaNac(rs.getDate("fechaNac").toLocalDate());
                 
                 clientes.add(cliente);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla cliente"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
        return clientes;
    }
}