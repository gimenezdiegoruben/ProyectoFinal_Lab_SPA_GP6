package Persistencias_Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**  
 * 
    @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
**/

public class Conexion {

    private static final String URL = "jdbc:mariadb://localhost/";
    private static final String DB = "spafinal_gp6";
    private static final String USUARIO = "root";
    private static String PASSWORD = "";

    private static Connection connection;

    //Metodo Constructor
    private Conexion() {
    }

    public static Connection getConexion() {

        try {
            if (connection == null || connection.isClosed()) {
                
                // Carga el driver de conexión
                Class.forName("org.mariadb.jdbc.Driver");
                
                // Establece la conexión con BD
                connection = DriverManager.getConnection(URL + DB, USUARIO ,PASSWORD);
                System.out.println("Conexion exitosa a la base de datos");
            }
        } catch (ClassNotFoundException ex) {

            JOptionPane.showMessageDialog(null, "Error al cargar los Drivers " + ex.getMessage());

        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class
                    .getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error al conectarse a la BD " + ex.getMessage());
        }
        return connection;
    }
}