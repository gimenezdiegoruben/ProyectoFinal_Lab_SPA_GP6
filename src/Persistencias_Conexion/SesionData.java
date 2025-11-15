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
    }

    public SesionData(EmpleadoData empData, ConsultorioData consData, TratamientoData tratData, InstalacionData instalData) {
        con = Conexion.getConexion();
        this.empData = empData;
        this.consData = consData;
        this.tratData = tratData;
        this.instalData = instalData;
    }

    public void crearSesion(Sesion sesion) {
        String sql = "INSERT INTO sesion(fechaHoraInicio, fechaHoraFinal, codTratam, nroConsultorio, matriculaMasajista, idRegistrador, codInstal, codPack, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setTimestamp(1, Timestamp.valueOf(sesion.getFechaHoraInicio()));
            ps.setTimestamp(2, Timestamp.valueOf(sesion.getFechaHoraFinal()));
            ps.setInt(3, sesion.getTratamiento().getCodTratam());
            ps.setInt(4, sesion.getConsultorio().getNroConsultorio());
            ps.setString(5, sesion.getMasajista().getMatricula());  //matricula del masajista
            ps.setInt(6, sesion.getRegistrador().getIdEmpleado());  //id del registrador (la recep)

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

            ps.setBoolean(9, sesion.getEstado());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                sesion.setCodSesion(rs.getInt(1));
                JOptionPane.showMessageDialog(null, "Sesión creada con éxito. Código: " + sesion.getCodSesion());
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al crear la Sesión: " + ex.getMessage());
        }
    }

    public void modificarSesion(Sesion sesion) {
        String sql = "UPDATE sesion SET fechaHoraInicio = ?, fechaHoraFinal = ?, codTratam = ?, nroConsultorio = ?, matriculaMasajista = ?, idRegistrador = ?, codInstal = ?, codPack = ?, estado = ? "
                + "WHERE codSesion = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(sesion.getFechaHoraInicio()));
            ps.setTimestamp(2, Timestamp.valueOf(sesion.getFechaHoraFinal()));
            ps.setInt(3, sesion.getTratamiento().getCodTratam());
            ps.setInt(4, sesion.getConsultorio().getNroConsultorio());
            ps.setString(5, sesion.getMasajista().getMatricula());  //matricula
            ps.setInt(6, sesion.getRegistrador().getIdEmpleado());  //id recep

            //Maneja el codPack, si es 0 o valor inválido se inserta nulo
            if (sesion.getInstalacion() != null) {
                ps.setInt(7, sesion.getInstalacion().getCodInstal());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }

            ps.setInt(8, sesion.getCodPack());
            ps.setBoolean(9, sesion.getEstado());
            ps.setInt(10, sesion.getCodSesion());

            int exito = ps.executeUpdate();
            if (exito > 0) {
                JOptionPane.showMessageDialog(null, "Sesión modificada.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró la sesión a modificar.");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla sesión (modificar): " + ex.getMessage());
        }
    }

    public List<Sesion> listarSesiones() {
        ArrayList<Sesion> lista = new ArrayList<>();
        String sql = "SELECT * FROM sesion";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Sesion sesi = new Sesion();
                sesi.setCodSesion(rs.getInt("codSesion"));

                //Obtener datos de bd
                int nroConsultorio = rs.getInt("nroConsultorio");
                int codTratam = rs.getInt("codTratam");
                int codInstal = rs.getInt("codInstal");
                String matriculaMasajista = rs.getString("matriculaMasajista");  //matric masaj
                int idRegistrador = rs.getInt("idRegistrador");                  //id recep
                int codPack = rs.getInt("codPack");

                //Buscar objetos complet
                Empleado masajista = empData.buscarEmpleadoPorMatricula(matriculaMasajista);
                Empleado registrador = empData.buscarEmpleadoPorId(idRegistrador);
                Consultorio cons = consData.buscarConsultorio(nroConsultorio);
                Tratamiento trat = tratData.buscarTratamientoPorCodigo(codTratam);
                Instalacion inst = instalData.buscarInstalacionPorCod(codInstal);

                //objetos
                sesi.setMasajista(masajista);
                sesi.setRegistrador(registrador);
                sesi.setConsultorio(cons);
                sesi.setTratamiento(trat);
                sesi.setInstalacion(inst);

                //horarios
                sesi.setFechaHoraInicio(rs.getTimestamp("fechaHoraInicio").toLocalDateTime());
                sesi.setFechaHoraFinal(rs.getTimestamp("fechaHoraFinal").toLocalDateTime());

                sesi.setCodPack(codPack);
                sesi.setEstado(rs.getBoolean("estado"));

                lista.add(sesi);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla sesión: " + ex.getMessage());
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
                sesion = new Sesion();
                sesion.setCodSesion(rs.getInt("codSesion"));
                int nroConsultorio = rs.getInt("nroConsultorio");
                int codTratam = rs.getInt("codTratam");
                int codInstal = rs.getInt("codInstal");
                String matriculaMasajista = rs.getString("matriculaMasajista");
                int idRegistrador = rs.getInt("idRegistrador");
                int codPack = rs.getInt("codPack");
                Empleado masajista = empData.buscarEmpleadoPorMatricula(matriculaMasajista);
                Empleado registrador = empData.buscarEmpleadoPorId(idRegistrador);
                Consultorio cons = consData.buscarConsultorio(nroConsultorio);
                Tratamiento trat = tratData.buscarTratamientoPorCodigo(codTratam);
                Instalacion inst = instalData.buscarInstalacionPorCod(codInstal);
                sesion.setMasajista(masajista);
                sesion.setRegistrador(registrador);
                sesion.setConsultorio(cons);
                sesion.setTratamiento(trat);
                sesion.setInstalacion(inst);
                sesion.setFechaHoraInicio(rs.getTimestamp("fechaHoraInicio").toLocalDateTime());
                sesion.setFechaHoraFinal(rs.getTimestamp("fechaHoraFinal").toLocalDateTime());
                sesion.setCodPack(codPack);
                sesion.setEstado(rs.getBoolean("estado"));
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al buscar sesión: " + ex.getMessage());
        }
        return sesion;
    }

    public void actualizarSesion(Sesion sesion) {
        String sql = "UPDATE sesion SET fechaHoraInicio = ?, fechaHoraFinal = ?, codTratam = ?, nroConsultorio = ?, matriculaMasajista = ?, idRegistrador = ?, codInstal = ?, codPack = ?, estado = ? WHERE codSesion = ?";

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

            ps.setBoolean(9, sesion.isActiva());
            ps.setInt(10, sesion.getCodSesion()); //Condición where de cod ses

            if (ps.executeUpdate() == 0) {
                System.out.println("No se encontró la sesión para actualizar: " + sesion.getCodSesion());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar sesión: " + ex.getMessage());
        }
    }
    
    
    
}
