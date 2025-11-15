/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ControladoresDeVistas;

import Modelos.Tratamiento;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import Vistas.VistasTratamiento;
import Persistencias_Conexion.TratamientoData;
import Vistas.Vista_MenuSpa;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;

public class ControladorTratamiento implements ActionListener,KeyListener{
    
    private final VistasTratamiento vista;
    private final TratamientoData data;
    private final Vista_MenuSpa menu;
    
    private Tratamiento  tratamientoSeleccionado;

    public ControladorTratamiento(VistasTratamiento vista, TratamientoData data, Vista_MenuSpa menu) {
        this.vista = vista;
        this.data = data;
        this.menu = menu;
        
       vista.btnGuardarTrat.addActionListener(this);
       vista.btnEliminar.addActionListener(this);
       vista.btnModificar.addActionListener(this);
       vista.btnSalir.addActionListener(this);
       vista.btndeAltaoBaja.addActionListener(this);
       vista.comboEspecialidad.addActionListener(this);
       vista.comboProducto.addActionListener(this);
       
             
    }
    public void iniciar(){
        menu.JDesktopPFondo.add(vista);
        vista.setVisible(true);
        menu.JDesktopPFondo.moveToFront(vista);
        vista.requestFocus();
        
        desactivarCampos();
    }    
    public void desactivarCampos(){
        vista.txtNombre.setEnabled(false);
        vista.txtDetalle.setEnabled(false);
        vista.txtPrecio.setEnabled(false);
        vista.comboEspecialidad.setEnabled(false);
        vista.comboProducto.setEnabled(false);
    }
    public void activarCampos(){
        vista.txtNombre.setEnabled(true);
        vista.txtDetalle.setEnabled(true);
        vista.txtPrecio.setEnabled(true);
        vista.comboEspecialidad.setEnabled(true);
        vista.comboProducto.setEnabled(true);
    }
    public void despejarCampos(){
        vista.txtNombre.setText("");
        vista.txtDetalle.setText("");
        vista.txtPrecio.setText("");
        vista.comboProducto.setSelectedIndex(-1);
        vista.comboEspecialidad.setSelectedIndex(-1);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == vista.btnGuardarTrat){
            guardarTratamiento();
        }
        if(e.getSource() == vista.btnModificar){
            modificarTratamiento();
        }
        if(e.getSource() == vista.btnEliminar){
            eliminarTratamiento();
        }
    }
    private void guardarTratamiento(){
        
        String nombre = vista.txtNombre.getText().trim();
        String tipo = (String) vista.comboEspecialidad.getSelectedItem();
        String detalle = vista.txtDetalle.getText().trim();
        String producto = (String) vista.comboProducto.getSelectedItem();
        String duraciontxt = vista.txtDuracion.getText();
        String precioTxt=vista.txtPrecio.getText().trim();
        
        if(nombre.isEmpty() || tipo == null || detalle.isEmpty() || precioTxt.isEmpty()){
            JOptionPane.showMessageDialog(null, "complete todos los campos.");
            return;
        }
        double costo;
        int duracion;
        
        try{
            costo = Double.parseDouble(precioTxt);
            duracion = Integer.parseInt(duraciontxt);
            
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(null, "Duracion y precio deben ser un numeros");
            return;
        }
        
        Tratamiento t = new Tratamiento();
        t.setNombre(nombre);
        t.setTipo(tipo);
        t.setDetalle(detalle);
        t.setDuracion(duracion);
        t.setCosto(costo);
        t.setEstado(true);
        
        //t.setProductos(1);
        
        data.guardarTratamiento(t);
        
        despejarCampos();
        desactivarCampos();
         
    }
    private void modificarTratamiento(){
       if(tratamientoSeleccionado == null){
           JOptionPane.showMessageDialog(null,"Primero busque un tratamiento para modificar");
           return;
       }
       
       String nombre = vista.txtNombre.getText().trim();
       String tipo = (String) vista.comboEspecialidad.getSelectedItem();
       String detalle = vista.txtDetalle.getText().trim();
       String producto = (String) vista.comboProducto.getSelectedItem();
       String duraciontxt = vista.txtDuracion.getText();
       String precioTxt=vista.txtPrecio.getText().trim();
       
       if(nombre.isEmpty() || tipo == null || detalle.isEmpty() || precioTxt.isEmpty()){
            JOptionPane.showMessageDialog(null, "complete todos los campos.");
            return;
        }
        double costo;
        int duracion;
        try{
            costo = Double.parseDouble(precioTxt);
            duracion = Integer.parseInt(duraciontxt);
            
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(null, "Duracion y precio deben ser un numeros");
            return;
        }
        tratamientoSeleccionado.setNombre(nombre);
        tratamientoSeleccionado.setTipo(tipo);
        tratamientoSeleccionado.setDetalle(detalle);
        tratamientoSeleccionado.setDuracion(duracion);
        tratamientoSeleccionado.setCosto(costo);
        tratamientoSeleccionado.setEstado(true);
        
        List<String> lista = new ArrayList<>();
        lista.add(producto);
        tratamientoSeleccionado.setProductos(lista);
        
        data.modificarTratamiento(tratamientoSeleccionado);
        
        JOptionPane.showMessageDialog(null, "Tratamiento modificado correctamente");
        
        despejarCampos();
        desactivarCampos();
    }   
    private void eliminarTratamiento(){
        if(tratamientoSeleccionado == null){
            JOptionPane.showMessageDialog(null, "Primero busque un tratamiento para eliminar.");
            return;
        }
        data.eliminarTratamiento(tratamientoSeleccionado.getCodTratam());
        
        JOptionPane.showMessageDialog(null, "Tratamiento eliminado correctamente");
        
        despejarCampos();
        desactivarCampos();
        tratamientoSeleccionado = null;
    }
private void cambiarEstadoTratamiento(){
    
}

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
}
