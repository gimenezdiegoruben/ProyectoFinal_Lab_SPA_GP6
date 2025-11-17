/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ControladoresDeVistas;

import Vistas.VistaHistorialClientes;
import Persistencias_Conexion.ClienteData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Ger
 */
public class ControladorHistorialCliente implements ActionListener{
     private VistaHistorialClientes vista;
     private ClienteData clienteData;
     
     public ControladorHistorialCliente(VistaHistorialClientes vista) {
        this.vista = vista;
        this.clienteData = new ClienteData();
        cargarDatosIniciales();
        this.vista.jButtonSalir.addActionListener(this);
     }
     
     private void cargarDatosIniciales(){
         try {
             vista.cargarTablaHistorial(clienteData.listarClientes());
         } catch (Exception e) {
             JOptionPane.showMessageDialog(vista, "Error al cargar el historial: " +e.getMessage());
         }
     }
     
     
     public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.jButtonSalir) {
            vista.dispose();
        }
     }
      public void iniciar() {
        vista.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }
}
