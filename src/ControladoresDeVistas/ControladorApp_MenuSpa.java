package ControladoresDeVistas;

import ControladoresDeVistas.ControladorHistorialCliente;
import Modelos.Empleado;
import Persistencias_Conexion.ClienteData;
import Persistencias_Conexion.ConsultorioData;
import Persistencias_Conexion.DiaDeSpaData;
import Vistas.VistaEmpleados;
import Persistencias_Conexion.EmpleadoData;
import Persistencias_Conexion.InstalacionData;
import Persistencias_Conexion.ProductoData;
import Persistencias_Conexion.SesionData;
import Persistencias_Conexion.TratamientoData;
import Vistas.VistaCliente;
import Vistas.VistaConsultorio;
import Vistas.VistaDiaDeSpa;
import Vistas.VistaHistorial;
import Vistas.VistaHistorialClientes;
import Vistas.VistaInstalacion;
import Vistas.VistaProductos;
import Vistas.VistaSesiones;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import Vistas.Vista_MenuSpa;
import Vistas.VistasTratamiento;
import javax.swing.JOptionPane;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */
public class ControladorApp_MenuSpa implements ActionListener, MenuListener, ComponentListener {

    private final Vista_MenuSpa menu;
    private final Empleado empleadoLogueado;

    public ControladorApp_MenuSpa(Vista_MenuSpa menu, Empleado empleadoLogueado) {
        this.menu = menu;
        this.empleadoLogueado = empleadoLogueado;
        //para mostrar el nombre ne la vista del empl logeado
        this.menu.lblUsuarioActual.setText(
                empleadoLogueado.getNombre() + " " + empleadoLogueado.getApellido()
        );

        // AddMenuListener escucha a jMenuBar en los metodos menuSelected, MenuDeselected y menuCanceled
        this.menu.jMenuConfigUserAndPass.addActionListener(this);
        this.menu.jMenuHistorialSes_Turnos.addActionListener(this);
        this.menu.jMenuHistorialClientes.addActionListener(this);
        this.menu.jMenuHistorialVentas.addActionListener(this);
        this.menu.jMenuHistorialProductos.addActionListener(this);

        this.menu.jmSalir.addMenuListener(this);

        //escucha de botones del panel Lateral
        menu.jButtonTurnos_DiaDeSPA.addActionListener(this);
        menu.jButtonBusquedas.addActionListener(this);
        menu.jButtonClientes.addActionListener(this);
        menu.jButtonEmpleados.addActionListener(this);
        menu.jButtonTratamientos.addActionListener(this);
        menu.jButtonInstalaciones.addActionListener(this);
        menu.jButtonConsultorios.addActionListener(this);
        menu.jButtonTienda.addActionListener(this);
        menu.jButtonSesiones.addActionListener(this);
        menu.jButtonSalir.addActionListener(this);

        //Listener para redimensionar el fondo cuando el escritorio cambie de tamaño
        menu.JDesktopPFondo.addComponentListener(this);
    }

    public void iniciar() {
        menu.setTitle("SPA ENTRE DEDOS VIP");
        menu.setVisible(true);
        menu.setLocationRelativeTo(null);
        menu.setResizable(false);
        ponerFondo();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //[aquí los action performeds de los menús]
        if (e.getSource() == menu.jMenuConfigUserAndPass) {
            VistaEmpleados vista = new VistaEmpleados();
            EmpleadoData data = new EmpleadoData();
            ControladorEmpleados ctrl = new ControladorEmpleados(menu, vista, data);

            //vista interna al JDesktopPane
            menu.JDesktopPFondo.add(vista);
            ctrl.iniciar();

            try {
                vista.setSelected(true);
                vista.toFront();
            } catch (Exception ex) {
                System.err.println("Error al cargar Vista empleados");
            }
        }

        //[y los otros menúes...]
        // ----------------------------------------------------------
        //[aquí los actionperformed de los botones]
        if (e.getSource() == menu.jButtonSalir) {
            
            int confirmacion= JOptionPane.showConfirmDialog(menu, "Esta seguro que desea salir del programa?","Confirmar salida",JOptionPane.YES_NO_OPTION);
            
            if(confirmacion==JOptionPane.YES_OPTION){
              menu.dispose();
            }
        }

        if (e.getSource() == menu.jButtonEmpleados) {
            javax.swing.JInternalFrame vistaExistente = obtenerVistaAbierta(Vistas.VistaEmpleados.class);

            if (vistaExistente != null) {
                try {
                    menu.JDesktopPFondo.moveToFront(vistaExistente);
                    vistaExistente.setSelected(true);

                } catch (java.beans.PropertyVetoException ex) {
                    System.err.println("Error al enfocar la vista: " + ex.getMessage());
                }
                return;
            } else {
                VistaEmpleados vista = new VistaEmpleados();
                EmpleadoData data = new EmpleadoData();
                ControladorEmpleados ctrl = new ControladorEmpleados(menu, vista, data);

                menu.JDesktopPFondo.add(vista);
                ctrl.iniciar();
                vista.setVisible(true);
                vista.toFront();
            }
        }
        
        if (e.getSource() == menu.getMenuHistorialClientes()) {
            abrirHistorialClientes();
        }
        
       

        if (e.getSource() == menu.jButtonClientes) {
            javax.swing.JInternalFrame vistaExistente = obtenerVistaAbierta(Vistas.VistaCliente.class);

            if (vistaExistente != null) {
                try {
                    menu.JDesktopPFondo.moveToFront(vistaExistente);
                    vistaExistente.setSelected(true);

                } catch (java.beans.PropertyVetoException ex) {
                    System.err.println("Error al enfocar la vista: " + ex.getMessage());
                }
                return;
            } else {
                VistaCliente vista = new VistaCliente();
                ClienteData data = new ClienteData();
                ControladorCliente ctrl = new ControladorCliente(vista, data, menu);

                ctrl.iniciar();
            }
        }
        
        if (e.getSource() == menu.jButtonBusquedas) {
            VistaHistorial vista = new VistaHistorial();
            ClienteData clienteData = new ClienteData();
            ConsultorioData consultorioData = new ConsultorioData();
            DiaDeSpaData diaDeSpaData = new DiaDeSpaData();
            EmpleadoData empleadoData = new EmpleadoData();
            InstalacionData instalacionData = new InstalacionData();
            ProductoData productoData = new ProductoData();
            SesionData sesionData = new SesionData();
            TratamientoData tratamientoData = new TratamientoData();
            ControladorHistorial ctrl = new ControladorHistorial(vista, clienteData, consultorioData, diaDeSpaData, empleadoData, instalacionData, productoData, sesionData, tratamientoData, menu);
            
            ctrl.iniciar();
        }

        if (e.getSource() == menu.jButtonConsultorios) {
            javax.swing.JInternalFrame vistaExistente = obtenerVistaAbierta(Vistas.VistaConsultorio.class);

            if (vistaExistente != null) {
                try {
                    menu.JDesktopPFondo.moveToFront(vistaExistente);
                    vistaExistente.setSelected(true);

                } catch (java.beans.PropertyVetoException ex) {
                    System.err.println("Error al enfocar la vista: " + ex.getMessage());
                }
                return;
            } else {
                VistaConsultorio vista = new VistaConsultorio();
                ConsultorioData data = new ConsultorioData();
                ControladorConsultorio ctrl = new ControladorConsultorio(vista, data, menu);

                ctrl.iniciar();
            }
        }

        if (e.getSource() == menu.jButtonTurnos_DiaDeSPA) {
            javax.swing.JInternalFrame vistaExistente = obtenerVistaAbierta(Vistas.VistaDiaDeSpa.class);

            if (vistaExistente != null) {
                try {
                    menu.JDesktopPFondo.moveToFront(vistaExistente);
                    vistaExistente.setSelected(true);

                } catch (java.beans.PropertyVetoException ex) {
                    System.err.println("Error al enfocar la vista: " + ex.getMessage());
                }
                return;
            } else {
                VistaDiaDeSpa vista = new VistaDiaDeSpa();
                DiaDeSpaData data = new DiaDeSpaData();
                ClienteData clienteData = new ClienteData();
                SesionData sesionData = new SesionData();
                ControladorDiaDeSpa ctrl = new ControladorDiaDeSpa(vista, data, clienteData, sesionData, menu);

                ctrl.iniciar();
            }
        }

        if (e.getSource() == menu.jButtonSesiones) {

            javax.swing.JInternalFrame vistaExistente = obtenerVistaAbierta(Vistas.VistaSesiones.class);

            if (vistaExistente != null) {
                try {
                    menu.JDesktopPFondo.moveToFront(vistaExistente);
                    vistaExistente.setSelected(true);//foco a la vista

                } catch (java.beans.PropertyVetoException ex) {
                    System.err.println("Error al enfocar la vista: " + ex.getMessage());
                }
                return;
            } else {
                VistaSesiones vista = new VistaSesiones();

                EmpleadoData empleadoData = new EmpleadoData();
                ConsultorioData consultorioData = new ConsultorioData();
                TratamientoData tratamientoData = new TratamientoData();
                InstalacionData instalacionData = new InstalacionData();
                ClienteData clienteData = new ClienteData();
                DiaDeSpaData diaDeSpaData = new DiaDeSpaData();

                //Instanciamos SesionData con sus 4 dependencias 
                SesionData sesionData = new SesionData(empleadoData, consultorioData, tratamientoData, instalacionData);

                //Creamos el ControladorSesiones con sus 9 argumentos..su vista, 7 Data objects, menu
                ControladorSesiones ctrl = new ControladorSesiones(vista, sesionData, empleadoData, consultorioData, tratamientoData, instalacionData, clienteData, diaDeSpaData, menu);

                ctrl.iniciar();
            }
        }

        if (e.getSource() == menu.jButtonInstalaciones) {
            javax.swing.JInternalFrame vistaExistente = obtenerVistaAbierta(Vistas.VistaInstalacion.class);

            if (vistaExistente != null) {
                try {
                    menu.JDesktopPFondo.moveToFront(vistaExistente);
                    vistaExistente.setSelected(true);

                } catch (java.beans.PropertyVetoException ex) {
                    System.err.println("Error al enfocar la vista: " + ex.getMessage());
                }
                return;
            } else {

                VistaInstalacion vista = new VistaInstalacion();
                InstalacionData data = new InstalacionData();
                SesionData sesionData = new SesionData();

                ControladorInstalacion ctrl = new ControladorInstalacion(vista, data, sesionData, menu);

                ctrl.iniciar();
            }
        }
        if (e.getSource() == menu.jButtonTratamientos) {
            javax.swing.JInternalFrame vistaExistente = obtenerVistaAbierta(Vistas.VistasTratamiento.class);

            if (vistaExistente != null) {
                try {
                    menu.JDesktopPFondo.moveToFront(vistaExistente);
                    vistaExistente.setSelected(true);

                } catch (java.beans.PropertyVetoException ex) {
                    System.err.println("Error al enfocar la vista: " + ex.getMessage());
                }
                return;
            } else {

                VistasTratamiento vista = new VistasTratamiento();
                TratamientoData data = new TratamientoData();

                ControladorTratamiento controlador = new ControladorTratamiento(vista, data, menu);
                controlador.iniciar();
            }
        }
        
        if (e.getSource() == menu.jButtonTienda) {
            VistaProductos vista = new VistaProductos();
            ProductoData data = new ProductoData();
            TratamientoData tratamientoData = new TratamientoData();
            
            ControladorProducto ctrl = new ControladorProducto(vista, data, tratamientoData, menu);
            
            ctrl.iniciar();
        }
    }

    private javax.swing.JInternalFrame obtenerVistaAbierta(Class tipo) {
        for (java.awt.Component comp : menu.JDesktopPFondo.getComponents()) {
            if (tipo.isInstance(comp)) {
                return (javax.swing.JInternalFrame) comp;
            }
        }
        return null;
    }
    
     public void abrirHistorialClientes(){
            VistaHistorialClientes historialClientes= new VistaHistorialClientes();
            ControladorHistorialCliente controladorHistorial= new ControladorHistorialCliente(historialClientes);
            controladorHistorial.iniciar();
        }
     
    public void ponerFondo() {
        ClassLoader directorio = getClass().getClassLoader();
        URL rutaImagenFondo = directorio.getResource("Images/fondo.jpg");

        //Crea un ImageIcon a partir de la imagen de fondo
        ImageIcon imagenFondoIcon = new ImageIcon(rutaImagenFondo);

        //Obtiene la imagen de fondo
        Image imagenFondo = imagenFondoIcon.getImage();

        //Redimensiona la imagen de fondo al tamaño del JPanel
        imagenFondo = imagenFondo.getScaledInstance(menu.getWidth(), menu.JDesktopPFondo.getHeight(), Image.SCALE_SMOOTH);

        //Crea un nuevo ImageIcon con la imagen redimensionada
        ImageIcon imagenFondoRedimensionadaIcon = new ImageIcon(imagenFondo);

        //Crea una etiqueta JLabel para mostrar la imagen de fondo en el JPanel
        JLabel imagenFondoLabel = new JLabel(imagenFondoRedimensionadaIcon);

        //Establece la ubicación y el tamaño de la imagen de fondo
        imagenFondoLabel.setBounds(0, 0, menu.JDesktopPFondo.getWidth(), menu.JDesktopPFondo.getHeight());

        //Agrega la imagen de fondo al JPanel
        menu.JDesktopPFondo.add(imagenFondoLabel);
        menu.JDesktopPFondo.setComponentZOrder(imagenFondoLabel, 0); //Fondo detras siempre
        menu.JDesktopPFondo.revalidate(); //Actualiza el JPanel para mostrar la imagen
        menu.JDesktopPFondo.repaint();
    }

    @Override
    public void menuSelected(MenuEvent e) {
        if (e.getSource() == menu.jmSalir) { //sale cuando el menu salir es seleccionado
            
            int confirmacion= JOptionPane.showConfirmDialog(menu, "Esta seguro que desea salir del programa?","Confirmar salida",JOptionPane.YES_NO_OPTION);
            
            if(confirmacion==JOptionPane.YES_OPTION){
              menu.dispose();
            }
        }
    }

    @Override
    public void menuDeselected(MenuEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void menuCanceled(MenuEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // Métodos requeridos por COMPONENTLISTENER
    @Override
    public void componentResized(ComponentEvent e) {
        ponerFondo();
    }

    @Override
    public void componentMoved(java.awt.event.ComponentEvent e) {

    }

    @Override
    public void componentShown(java.awt.event.ComponentEvent e) {

    }

    @Override
    public void componentHidden(java.awt.event.ComponentEvent e) {

    }
}
