/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ControladoresDeVistas;

import Modelos.Instalacion;
import Persistencias_Conexion.InstalacionData;
import Vistas.VistaInstalacion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
/**
 *
 * @author Ger
 */
public class ControladorInstalacion implements ActionListener{
    private VistaInstalacion vista;
    private InstalacionData instalacionData;
    
    public ControladorInstalacion(VistaInstalacion vista){
        this.vista=vista;
        this.instalacionData= new InstalacionData();
        iniciarListenerBotones();
    }
    
    private void iniciarListenerBotones(){
    vista.jbtnGuardar.addActionListener(this);
    vista.jbtnBuscar.addActionListener(this);
    vista.jbtnModificar.addActionListener(this);
    vista.jbtnEliminar.addActionListener(this);
    vista.jbtnLimpiar.addActionListener(this);

}
}
