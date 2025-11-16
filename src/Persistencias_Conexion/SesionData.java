package Persistencias_Conexion;

import Modelos.Instalacion;
import Modelos.Consultorio;
import Modelos.Tratamiento;
import Modelos.Sesion;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import Modelos.Empleado;

public class SesionData {

    private Connection con = null;
    private EmpleadoData empData;
    private ConsultorioData consData;
    private TratamientoData tratData;
    private InstalacionData instalData;

    public SesionData() {
        con = Conexion.getConexion();
    }

    public SesionData(EmpleadoData empData, ConsultorioData consData, TratamientoData tratData, InstalacionData instalData) {
        con = Conexion.getConexion();
        this.empData = empData;
        this.consData = consData;
        this.tratData = tratData;
        this.instalData = instalData;
    }

    public void crearSesion(Sesion sesion) {
        String sql = "INSERT INTO sesion(fechaHoraInicio, fechaHoraFinal, codTratam, nroConsultorio, matriculaMasajista, idRegistrador, codInstal, codPack, monto, notas, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setTimestamp(1, Timestamp.valueOf(sesion.getFechaHoraInicio()));
            ps.setTimestamp(2, Timestamp.valueOf(sesion.getFechaHoraFinal()));
            ps.setInt(3, sesion.getTratamiento().getCodTratam());
            ps.setInt(4, sesion.getConsultorio().getNroConsultorio());
            ps.setString(5, sesion.getMasajista().getMatricula());
            ps.setInt(6, sesion.getRegistrador().getIdEmpleado());//id del registrador (la recep)
            
            //Si la instalación es opcional “Ninguna”, no rompe nada
            //Si aún no estmos vinculando la sesión a un codPack real y sigue en 0, no romperá la clave foranea y guarda null
            if (sesion.getInstalacion() != null) {
                ps.setInt(7, sesion.getInstalacion().getCodInstal());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            
            if (sesion.getCodPack() > 0) {
                ps.setInt(8, sesion.getCodPack());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }            
            
            ps.setDouble(9, sesion.getMonto());
            ps.setString(10, sesion.getNotas());
            ps.setBoolean(11, sesion.getEstado());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                sesion.setCodSesion(rs.getInt(1));
                JOptionPane.showMessageDialog(null, "Sesión creada con éxito. Código: " + sesion.getCodSesion());
            }
            ps.close();
            rs.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al crear la Sesión: " + ex.getMessage());
        }
    }

    public void modificarSesion(Sesion sesion) {
        actualizarSesion(sesion);
    }

    public List<Sesion> listarSesiones() {   // NUEVO

        List<Sesion> lista = new ArrayList<>();
        String sql = "SELECT * FROM sesion";  // NUEVO

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Sesion sesion = construirSesionDesdeResultSet(rs);  // NUEVO: reutiliza helper (incluye monto y notas)
                lista.add(sesion);
            }

            rs.close();
            ps.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error en listarSesiones: " + ex.getMessage());
        }

        return lista;
    }
    public List<Sesion> listarSesionesPorPack(int codPack) {

        List<Sesion> lista = new ArrayList<>();
        String sql = "SELECT * FROM sesion WHERE codPack = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, codPack);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Sesion sesion = construirSesionDesdeResultSet(rs);
                lista.add(sesion);
            }

            rs.close();
            ps.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error en listarSesionesPorPack: " + ex.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al construir la Sesión: " + e.getMessage());
        }

        return lista;
    }

    //Listamos masajistas por la matricula
    public List<Empleado> listarMasajistasLibres(LocalDateTime desde, LocalDateTime hasta) {
        ArrayList<Empleado> lista = new ArrayList<>();

        String sql = "SELECT e.* FROM empleado e "
                + "WHERE e.estado = 1 "
                + "AND e.matricula IS NOT NULL "
                //Solo masajistas (unicos que tienen matrícula)
                + "AND e.matricula NOT IN ("
                + "    SELECT s.matriculaMasajista FROM sesion s "
                + "    WHERE s.estado = 1 "
                + "    AND (s.fechaHoraInicio < ? AND s.fechaHoraFinal > ?)"
                + ")";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(hasta));
            ps.setTimestamp(2, Timestamp.valueOf(desde));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Empleado e = new Empleado();
                e.setIdEmpleado(rs.getInt("idEmpleado"));
                e.setMatricula(rs.getString("matricula"));
                e.setNombre(rs.getString("nombre"));
                e.setApellido(rs.getString("apellido"));
                e.setEspecialidad(rs.getString("especialidad"));
                lista.add(e);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al listar Masajistas libres: " + ex.getMessage());
        }
        return lista;
    }

    //Listar recepcionistas (para asignar como registrador)
    public List<Empleado> listarRecepcionistas() {
        ArrayList<Empleado> lista = new ArrayList<>();

        String sql = "SELECT * FROM empleado WHERE estado = 1 AND puesto LIKE '%recepcionista%'";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Empleado e = new Empleado();
                e.setIdEmpleado(rs.getInt("idEmpleado"));
                e.setNombre(rs.getString("nombre"));
                e.setApellido(rs.getString("apellido"));
                e.setPuesto(rs.getString("puesto"));
                lista.add(e);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al listar Recepcionistas: " + ex.getMessage());
        }
        return lista;
    }

    //Métodos auxiliares
    public List<Empleado> listarMasajistasPorEspecialidad(String especialidad) {
        ArrayList<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM empleado WHERE estado = 1 AND matricula IS NOT NULL AND especialidad = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, especialidad);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Empleado e = new Empleado();
                e.setIdEmpleado(rs.getInt("idEmpleado"));
                e.setMatricula(rs.getString("matricula"));
                e.setNombre(rs.getString("nombre"));
                e.setApellido(rs.getString("apellido"));
                e.setEspecialidad(rs.getString("especialidad"));
                lista.add(e);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al listar Masajistas por especialidad: " + ex.getMessage());
        }
        return lista;
    }

    public List<Instalacion> listarInstalacionesLibres(LocalDateTime desde, LocalDateTime hasta) {
        ArrayList<Instalacion> lista = new ArrayList<>();
        String sql = "SELECT i.* FROM instalacion i "
                + "WHERE i.estado = 1 "
                + "AND i.codInstal NOT IN ("
                + "    SELECT s.codInstal FROM sesion s "
                + "    WHERE s.estado = 1 "
                + "    AND (s.fechaHoraInicio < ? AND s.fechaHoraFinal > ?)" + ")";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(hasta));
            ps.setTimestamp(2, Timestamp.valueOf(desde));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Instalacion ins = new Instalacion();
                ins.setCodInstal(rs.getInt("codInstal"));
                ins.setNombre(rs.getString("nombre"));
                lista.add(ins);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al listar Instalaciones libres: " + ex.getMessage());
        }
        return lista;
    }

    public List<Consultorio> listarConsultoriosLibres(LocalDateTime desde, LocalDateTime hasta) {
        ArrayList<Consultorio> lista = new ArrayList<>();
        String sql = "SELECT c.* FROM consultorio c "
                + "WHERE c.nroConsultorio NOT IN ("
                + "    SELECT s.nroConsultorio FROM sesion s "
                + "    WHERE s.estado = 1 "
                + "    AND (s.fechaHoraInicio < ? AND s.fechaHoraFinal > ?)" + ")";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(hasta));
            ps.setTimestamp(2, Timestamp.valueOf(desde));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Consultorio c = new Consultorio();
                c.setNroConsultorio(rs.getInt("nroConsultorio"));
                lista.add(c);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al listar Consultorios libres: " + ex.getMessage());
        }
        return lista;
    }

    public void anularSesion(int codSesion) {
        String sql = "UPDATE sesion SET estado = 0 WHERE codSesion=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, codSesion);
            int exito = ps.executeUpdate();
            if (exito > 0) {
                JOptionPane.showMessageDialog(null, "Sesión anulada correctamente");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró una sesión con ese código");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al anular sesión: " + ex.getMessage());
        }
    }

    public void eliminarSesion(int codSesion) {
        String sql = "DELETE FROM sesion WHERE codSesion=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, codSesion);
            int exito = ps.executeUpdate();
            if (exito > 0) {
                JOptionPane.showMessageDialog(null, "Se eliminó correctamente la sesión");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró una sesión con ese código");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar sesión: " + ex.getMessage());
        }
    }

    public Sesion buscarSesionPorCodigo(int codSesion) {
        Sesion sesion = null;
        String sql = "SELECT * FROM sesion WHERE codSesion = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, codSesion);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                sesion = construirSesionDesdeResultSet(rs);
            }
            rs.close();
            ps.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al buscar sesión: " + ex.getMessage());
        }
        return sesion;
    }

    public void actualizarSesion(Sesion sesion) {
        String sql = "UPDATE sesion SET fechaHoraInicio=?, fechaHoraFinal=?, codTratam=?, nroConsultorio=?, matriculaMasajista=?, idRegistrador=?, codInstal=?, codPack=?, monto=?, notas=?, estado=? WHERE codSesion=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(sesion.getFechaHoraInicio()));
            ps.setTimestamp(2, Timestamp.valueOf(sesion.getFechaHoraFinal()));
            ps.setInt(3, sesion.getTratamiento().getCodTratam());
            ps.setInt(4, sesion.getConsultorio().getNroConsultorio());
            ps.setString(5, sesion.getMasajista().getMatricula());
            ps.setInt(6, sesion.getRegistrador().getIdEmpleado());
            ps.setInt(7, sesion.getInstalacion().getCodInstal());

            //Maneja el codPack, si es 0 o valor inválido se inserta nulo
            if (sesion.getCodPack() > 0) {
                ps.setInt(8, sesion.getCodPack());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }

            ps.setDouble(9, sesion.getMonto());
            ps.setString(10, sesion.getNotas());
            ps.setBoolean(11, sesion.isActiva());
            ps.setInt(12, sesion.getCodSesion()); //Condición where de cod ses

            ps.executeUpdate();
            ps.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar sesión: " + ex.getMessage());
        }
    }

    private Sesion construirSesionDesdeResultSet(ResultSet rs) throws SQLException {

        Sesion s = new Sesion();

        s.setCodSesion(rs.getInt("codSesion"));
        s.setFechaHoraInicio(rs.getTimestamp("fechaHoraInicio").toLocalDateTime());
        s.setFechaHoraFinal(rs.getTimestamp("fechaHoraFinal").toLocalDateTime());

        //Empleado masajista (solo matricula conocida aquí)
        Empleado mas = new Empleado();
        mas.setMatricula(rs.getString("matriculaMasajista"));
        s.setMasajista(mas);

        // Empleado registrador (solo id conocido aquí)
        Empleado reg = new Empleado();
        reg.setIdEmpleado(rs.getInt("idRegistrador"));
        s.setRegistrador(reg);

        // Consultorio
        Consultorio cons = new Consultorio();
        cons.setNroConsultorio(rs.getInt("nroConsultorio"));
        s.setConsultorio(cons);

        // Tratamiento
        Tratamiento trat = new Tratamiento();
        trat.setCodTratam(rs.getInt("codTratam"));
        s.setTratamiento(trat);

        // Instalación
        Instalacion inst = new Instalacion();
        inst.setCodInstal(rs.getInt("codInstal"));
        s.setInstalacion(inst);

        s.setCodPack(rs.getInt("codPack"));
        s.setMonto(rs.getDouble("monto"));//monto
        s.setNotas(rs.getString("notas"));//notas
        s.setEstado(rs.getBoolean("estado"));

        return s;
    }
}
