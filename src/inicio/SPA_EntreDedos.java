package inicio;

import ControladoresDeVistas.ControladorLogin;
import Vistas.VistaLogin;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */
public class SPA_EntreDedos {

    public static void main(String[] args) {

        VistaLogin login = new VistaLogin();
        login.setLocationRelativeTo(null);
        ControladorLogin control = new ControladorLogin(login);
        control.iniciar();
    }
}
