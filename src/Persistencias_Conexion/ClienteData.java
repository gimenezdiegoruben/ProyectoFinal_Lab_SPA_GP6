package Persistencias_Conexion;

import Modelos.Cliente;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        
        String sql = "INSERT INTO cliente(dni,nombre ,telefono ,edad ,afecciones ,estado) VALUES(? , ? , ? , ? , ? , ?)";
        try {
        
        PreparedStatement ps= con.prepareStatement(sql ,Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, cliente.getDni());
        ps.setString(2, cliente.getNombre());
        ps.setInt(3, cliente.getTelefono());
        ps.setInt(4,cliente.getEdad());
        ps.setString(5, cliente.getAfecciones());
        ps.setBoolean(6, cliente.isEstado());
        ps.executeUpdate();
        
        ResultSet rs= ps.getGeneratedKeys();
        if(rs.next()){
            cliente.setCodCli(rs.getInt(1));
            JOptionPane.showMessageDialog(null, "Alumno guardado");
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
        String sql = "UPDATE cliente SET nombre = ?,telefono = ? , edad = ?, afecciones = ?,estado = ? "
                    + "WHERE dni = ?";
            
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, cliente.getNombre());
            ps.setInt(2, cliente.getTelefono());
            ps.setInt(3, cliente.getEdad());
            ps.setString(4, cliente.getAfecciones());
            ps.setBoolean(5, cliente.isEstado());
            ps.setInt(6,cliente.getDni());
            int exito=ps.executeUpdate();
            
            if(exito == 1){
                JOptionPane.showMessageDialog(null, "Cliente modificado");
            }
            
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla cliente"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
        
    }
    public void eliminarCliente(int dni){
        String sql = "UPDATE cliente SET estado = 0 WHERE dni = ? ";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, dni);
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
        String sql = "SELECT dni, nombre, telefono , edad, afecciones, estado FROM cliente WHERE codCli = ? AND estado = 1";
        Cliente cliente = null;    
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs= ps.executeQuery();
            if(rs.next()){
                 cliente = new Cliente();
                 cliente.setCodCli(id);
                 cliente.setDni(rs.getInt("dni"));
                 cliente.setNombre(rs.getString("nombre"));
                 cliente.setTelefono(rs.getInt("telefono"));
                 cliente.setEdad(rs.getInt("edad"));
                 cliente.setAfecciones(rs.getString("afecciones"));
                 cliente.setEstado(true);
            }else {
                JOptionPane.showMessageDialog(null, "No existe un cliente cone ese id");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla cliente"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
        return cliente;    
    }
    public Cliente buscarPorDni(int dni){
        String sql = "SELECT codCli, nombre, telefono , edad, afecciones, estado FROM cliente WHERE dni = ? AND estado = 1";
        Cliente cliente = null;    
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, dni);
            ResultSet rs= ps.executeQuery();
            if(rs.next()){
                 cliente = new Cliente();
                 cliente.setCodCli(rs.getInt("codCli"));
                 cliente.setDni(dni);
                 cliente.setNombre(rs.getString("nombre"));
                 cliente.setTelefono(rs.getInt("telefono"));
                 cliente.setEdad(rs.getInt("edad"));
                 cliente.setAfecciones(rs.getString("afecciones"));
                 cliente.setEstado(true);
            }else {
                JOptionPane.showMessageDialog(null, "No existe un cliente cone ese dni");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla cliente"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
        return cliente;
    }
    public List<Cliente> listarClientes(){
        String sql = "SELECT codCli, dni ,nombre, telefono , edad, afecciones, estado FROM cliente WHERE estado = 1";
        ArrayList<Cliente> clientes = new ArrayList<>();    
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            ResultSet rs= ps.executeQuery();
            while(rs.next()){
                 Cliente cliente = new Cliente();
                 cliente.setCodCli(rs.getInt("codCli"));
                 cliente.setDni(rs.getInt("dni"));
                 cliente.setNombre(rs.getString("nombre"));
                 cliente.setTelefono(rs.getInt("telefono"));
                 cliente.setEdad(rs.getInt("edad"));
                 cliente.setAfecciones(rs.getString("afecciones"));
                 cliente.setEstado(true);
                 
                 clientes.add(cliente);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla cliente"+ ex.getMessage() + "\nCodigo SQL:" + ex.getErrorCode());
        }
        return clientes;
    }
}