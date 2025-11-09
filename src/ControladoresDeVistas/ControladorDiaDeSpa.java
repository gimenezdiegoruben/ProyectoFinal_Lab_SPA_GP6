package ControladoresDeVistas;

import Persistencias_Conexion.DiaDeSpaData;
import Persistencias_Conexion.ClienteData;
import Vistas.VistaDiaDeSpa;
import Vistas.Vista_MenuSpa;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.table.DefaultTableModel;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */

public class ControladorDiaDeSpa implements ActionListener {

    private final VistaDiaDeSpa vista;
    private final DiaDeSpaData diaDeSpaData;
    private final ClienteData clienteData;
    private final Vista_MenuSpa menu;
    private final DefaultTableModel modelo;

    // Campo para mantener el ID del paquete seleccionado (para Modificar/Eliminar)
    private int codPackSeleccionado = -1;

    public ControladorDiaDeSpa(VistaDiaDeSpa vista, DiaDeSpaData diaDeSpaData, ClienteData clienteData, Vista_MenuSpa menu) {
        this.vista = vista;
        this.diaDeSpaData = diaDeSpaData;
        this.clienteData = clienteData;
        this.menu = menu;
        
        this.modelo = (DefaultTableModel) vista.jTable1.getModel(); 


        this.vista.jbtNuevo.addActionListener(this);
        this.vista.jbtGuardar.addActionListener(this);
        this.vista.jbtEliminar.addActionListener(this);
        this.vista.jbtSalir.addActionListener(this);
        this.vista.btnAgregarSesiones.addActionListener(this);
    }

    
    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
    
}