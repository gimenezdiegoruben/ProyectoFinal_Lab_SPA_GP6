package Persistencias_Conexion;

import Modelos.Producto;
import Modelos.Tratamiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Grupo 6 Gimenez Diego Ruben Carlos German Mecias Giacomelli Tomas
 * Migliozzi Badani Urbani Jose
 *
 */
public class ProductoData {

    private Connection con = null;

    public ProductoData() {

        con = Conexion.getConexion();

    }

    public void guardarProducto(Producto producto) {

        String sql = "INSERT INTO producto(descripcion, precio, stock, codTratam) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, producto.getDescripcion());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getStock());
            ps.setInt(4, producto.getTratamiento().getCodTratam());
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                producto.setCodProducto(rs.getInt(1));
                System.out.println("Producto guardado");
            }
        } catch (SQLException ex) {
            System.out.println("Error al conectarse a la tabla productos");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }
    }

    public void modificarProducto(Producto producto) {

        String sql = "UPDATE producto SET descripcion = ?, precio = ?, stock = ?, codTratam = ? WHERE codProducto = ?";
        PreparedStatement ps = null;

        try {

            ps = con.prepareStatement(sql);
            ps.setString(1, producto.getDescripcion());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getStock());
            ps.setInt(4, producto.getTratamiento().getCodTratam());
            ps.setInt(5, producto.getCodProducto());
            int exito = ps.executeUpdate();

            if (exito == 1) {
                System.out.println("Producto modificado");
            }
        } catch (SQLException ex) {
            System.out.println("Error al conectarse con la tabla productos");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }
    }

    public void eliminarProducto(int codProducto) {

        String sql = "DELETE FROM producto WHERE codProducto = ?";
        PreparedStatement ps = null;

        try {

            ps = con.prepareStatement(sql);
            ps.setInt(1, codProducto);
            int exito = ps.executeUpdate();

            if (exito == 1) {
                System.out.println("Producto eliminado");
            } else {
                System.out.println("No se encontro ningun producto con ese código");
            }
        } catch (SQLException ex) {
            System.out.println("Error al conectarse con la tabla productos");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }
    }

    public Producto buscarProductoPorCod(int codProducto) {

        String sql = "SELECT descripcion, precio, stock, codTratam FROM producto WHERE codProducto = ?";
        Producto p1 = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = con.prepareStatement(sql);
            ps.setInt(1, codProducto);
            rs = ps.executeQuery();

            if (rs.next()) {
                p1 = new Producto();
                p1.setCodProducto(codProducto);
                p1.setDescripcion(rs.getString("descripcion"));
                p1.setPrecio(rs.getDouble("precio"));
                p1.setStock(rs.getInt("stock"));
                Tratamiento t1 = new Tratamiento();
                t1.setCodTratam(rs.getInt("codTratam"));
                p1.setTratamiento(t1);
            } else {
                System.out.println("No existe un producto con ese código");
            }
        } catch (SQLException ex) {
            System.out.println("Error al conectarse con la tabla productos");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }
        return p1;
    }

    public List<Producto> listarProductos() {

        String sql = "SELECT codProducto, descripcion, precio, stock, codTratam FROM producto";
        ArrayList<Producto> productos = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Producto p1 = new Producto();
                p1.setCodProducto(rs.getInt("codProducto"));
                p1.setDescripcion(rs.getString("descripcion"));
                p1.setPrecio(rs.getDouble("precio"));
                p1.setStock(rs.getInt("stock"));
                Tratamiento t1 = new Tratamiento();
                t1.setCodTratam(rs.getInt("codTratam"));
                p1.setTratamiento(t1);

                productos.add(p1);
            }

        } catch (SQLException ex) {
            System.out.println("Error al acceder a la tabla productos");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }
        return productos;
    }

    public List<Producto> listarProductosPorCodTratam(int codTratam) {
        String sql = "SELECT codProducto, descripcion, precio, stock, codTratam FROM producto WHERE codTratam = ?";
        List<Producto> productos = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, codTratam);  // <-- CORRECCIÓN
            rs = ps.executeQuery();

            while (rs.next()) {
                Producto p1 = new Producto();
                p1.setCodProducto(rs.getInt("codProducto"));
                p1.setDescripcion(rs.getString("descripcion"));
                p1.setPrecio(rs.getDouble("precio"));
                p1.setStock(rs.getInt("stock"));

                Tratamiento t1 = new Tratamiento();
                t1.setCodTratam(rs.getInt("codTratam"));
                p1.setTratamiento(t1);

                productos.add(p1);
            }

        } catch (SQLException ex) {
            System.out.println("Error al acceder a la tabla productos: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }

        return productos;
    }
}
