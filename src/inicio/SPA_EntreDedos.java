package inicio;

import ControladoresDeVistas.ControladorApp_MenuSpa;
import Vistas.Vista_MenuSpa;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */

public class SPA_EntreDedos {

 public static void main(String[] args) {
        Vista_MenuSpa menu = new Vista_MenuSpa();
        ControladorApp_MenuSpa crlmenu = new ControladorApp_MenuSpa(menu);
        crlmenu.iniciar();
        menu.setVisible(true);
    }
}
