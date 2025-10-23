package Test;

import Modelos.Cliente;
import Persistencias_Conexion.ClienteData;
import java.util.List;

/*  @author Grupo 6 
    Gimenez Diego Ruben
    Carlos German Mecias Giacomelli
    Tomas Migliozzi Badani
    Urbani Jose
 */

public class TestApp {

    public static void main(String[] args) {
        
        System.out.println("Agregando clientes...");
        ClienteData clienteData = new ClienteData();
        Cliente c1 = new Cliente(46260667, "Homero", 123456789, 20, "Afeccion0", true);
        clienteData.guardarCliente(c1);
        Cliente c2 = new Cliente (30551131, "Gimenez Diego", 213456789, 41, "Afeccion1", true);
        clienteData.guardarCliente(c2);
        Cliente c3 = new Cliente (45802941, "Migliozzi Tomás", 312456789, 21, "Afeccion2", true);
        clienteData.guardarCliente(c3);
        Cliente c4 = new Cliente (44309664, "Mecias Germán", 41256789, 23, "Afeccion3", true);
        clienteData.guardarCliente(c4);
        Cliente eliminado = new Cliente(01234112, "Cliente a eliminar", 3123213, 30, "Afeccion0", true);
        clienteData.guardarCliente(eliminado);
        System.out.println("Modificando clientes...");
        Cliente modificar = new Cliente(46260667, "Urbani José", 123456789, 20, "Afeccion0", true);
        clienteData.modificarCliente(modificar);
        System.out.println("Listando clientes...");
        List<Cliente> clientes = clienteData.listarClientes();
        for (Cliente cliente : clientes) {
            System.out.println(cliente);
        }
        System.out.println("Buscando clientes por dni...");
        Cliente buscado = clienteData.buscarPorDni(30551131);
        System.out.println(buscado);
        System.out.println("Eliminando un cliente...");
        clienteData.eliminarCliente(01234112);
        System.out.println("Lista de clientes despues de eliminar...");
        clientes = clienteData.listarClientes();
        for (Cliente cliente : clientes) {
            System.out.println(cliente);
        }
            
        }
        
        
    }

