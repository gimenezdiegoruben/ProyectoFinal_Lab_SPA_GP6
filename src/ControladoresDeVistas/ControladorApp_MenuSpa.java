package ControladoresDeVistas;

import Modelos.Empleado;
import Persistencias_Conexion.ClienteData;
import Persistencias_Conexion.ConsultorioData;
import Vistas.VistaEmpleados;
import Persistencias_Conexion.EmpleadoData;
import Vistas.VistaCliente;
import Vistas.VistaConsultorio;
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

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */
public class ControladorApp_MenuSpa implements ActionListener, MenuListener, ComponentListener {

    private final Vista_MenuSpa menu;

    public ControladorApp_MenuSpa(Vista_MenuSpa menu) {
        this.menu = menu;

        // AddMenuListener escucha a jMenuBar en los metodos menuSelected, MenuDeselected y menuCanceled
        this.menu.jMenuVerEmpleados.addActionListener(this);
        this.menu.jMenuHistorialSes_Turnos.addActionListener(this);
        this.menu.jMenuHistorialClientes.addActionListener(this);
        this.menu.jMenuHistorialVentas.addActionListener(this);
        this.menu.jMenuHistorialProductos.addActionListener(this);

        this.menu.jmSalir.addMenuListener(this);

        //escucha de botones del panel Lateral
        menu.jButtonTurnos.addActionListener(this);
        menu.jButtonBusquedas.addActionListener(this);
        menu.jButtonClientes.addActionListener(this);
        menu.jButtonProfesionales.addActionListener(this);
        menu.jButtonEmpleados.addActionListener(this);
        menu.jButtonTratamientos.addActionListener(this);
        menu.jButtonInstalaciones.addActionListener(this);
        menu.jButtonConsultorios.addActionListener(this);
        menu.jButtonTienda.addActionListener(this);
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
        if (e.getSource() == menu.jMenuVerEmpleados) {
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
            menu.dispose();
        }

        if (e.getSource() == menu.jButtonEmpleados) { // Iniciar el jInternalFrame de Pacientes
            VistaEmpleados vista = new VistaEmpleados();
            EmpleadoData data = new EmpleadoData();
            ControladorEmpleados ctrl = new ControladorEmpleados(menu, vista, data);

            menu.JDesktopPFondo.add(vista);
            ctrl.iniciar();
            vista.setVisible(true);
            vista.toFront();
        }
        //[aquí los demás botones]
        if (e.getSource() == menu.jButtonClientes) {
            VistaCliente vista = new VistaCliente();
            ClienteData data = new ClienteData();
            ControladorCliente ctrl = new ControladorCliente(vista, data, menu);
            
            ctrl.iniciar();
        }
        
        if (e.getSource() == menu.jButtonConsultorios) {
            VistaConsultorio vista = new VistaConsultorio();
            ConsultorioData data = new ConsultorioData();
            ControladorConsultorio ctrl = new ControladorConsultorio(vista, data, menu);
            
            ctrl.iniciar();
        }
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
            menu.dispose();
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
