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
    
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == vista.jbtnGuardar){
            guardarInstalacion();
        }else if(e.getSource()== vista.jbtnBuscar){
            buscarInstalacion();
        }else if(e.getSource()== vista.jbtnModificar){
            modificarInstalacion();
        }else if(e.getSource()==vista.jbtnEliminar){
            eliminarInstalacion();
        }else if(e.getSource()==vista.jbtnLimpiar){
            vista.limpiarCampos();
        }
        //Recargamos tabla despues 
        if (e.getSource() == vista.jbtnGuardar || e.getSource() == vista.jbtnModificar || e.getSource() == vista.jbtnEliminar) {
             vista.cargarTablaInstalaciones(instalacionData.listarInstalacionesActivas());
        }
    }
    
    private Instalacion obtenerDatosForm(boolean incluirId){
        Instalacion instalacion= new Instalacion();
        
        try{
            if(incluirId && !vista.jtfCodInstalacion.getText().trim().isEmpty()){
                instalacion.setCodInstal(Integer.parseInt(vista.jtfCodInstalacion.getText()));
            } else if(incluirId){
                throw new NumberFormatException("El codigo de instalacion es necesario para realizar esta accion");
            }
            instalacion.setNombre(vista.jtfNombreInstalacion.getText());
            instalacion.setDetalleUso(vista.jtaDetalleUso.getText());
            instalacion.setPrecio(Double.parseDouble(vista.jtfPrecio30m.getText()));
            instalacion.setEstado(vista.jchbEstadoInstalacion.isSelected());
        }catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Error de formato: El ID o el Precio deben ser números. " + e.getMessage(), "Error de Datos", JOptionPane.ERROR_MESSAGE);
            return null; 
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return instalacion;
    }

    
    
    
    //GUARDAR INSTALACION
    
    private void guardarInstalacion(){
    Instalacion nuevaInstalacion = obtenerDatosForm(false);
    
    if(nuevaInstalacion!=null){
        try {
            instalacionData.crearInstalacion(nuevaInstalacion);
            vista.limpiarCampos();
        } catch (Exception e) {
        }
    }
        
    }
    
    
    //BUSCAR INSTALACION
    private void buscarInstalacion(){
        try {
             int codInstal= Integer.parseInt(vista.jtfCodInstalacion.getText().trim());
    
             Instalacion encontrada = instalacionData.buscarInstalacionPorCod(codInstal);
             if(encontrada!=null){
                vista.cargarFormularioDesdeInstalacion(encontrada);
             }else{
                JOptionPane.showMessageDialog(vista, "Instalación con ID " + codInstal + " no encontrada.");
                vista.limpiarCampos();
             }
        } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(vista, "Ingrese un código de instalación válido para buscar.", "Error de Búsqueda", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    //MODIFICAR INSTALACION
    private void modificarInstalacion(){
    Instalacion instalacionModif = obtenerInstalacionDesdeFormulario(true);
    
    if(instalacionModif != null)
    }
    
    
    //ELIMINAR INSTALACION
    private void eliminarInstalacion(){}
    
    
}
