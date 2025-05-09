package kosmos.java.com.hospital.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kosmos.java.com.hospital.model.Cita;
import kosmos.java.com.hospital.model.Consultorio;
import kosmos.java.com.hospital.model.Doctor;

public interface CitaRepository extends JpaRepository <Cita, Long> {

    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE " +
           "c.consultorio = :consultorio AND " +
           "c.horarioConsulta >= :inicio AND " +
           "c.horarioConsulta < :fin")
    boolean existsByConsultorioAndHorarioConsultaBetween(
        @Param("consultorio") Consultorio consultorio,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE " +
        "c.doctor = :doctor AND " +
        "c.horarioConsulta >= :inicio AND " +
        "c.horarioConsulta < :fin")
    boolean existsByDoctorAndHorarioConsultaBetween(
        @Param("doctor") Doctor doctor,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin);
    
    
    List<Cita> findByNombrePacienteAndHorarioConsultaBetween(
        String nombrePaciente, 
        LocalDateTime inicio, 
        LocalDateTime fin);
    
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.doctor = :doctor AND DATE(c.horarioConsulta) = :fecha")
    Long countByDoctorAndFecha(
        @Param("doctor") Doctor doctor, 
        @Param("fecha") LocalDate fecha);
    
    @Query("SELECT c FROM Cita c WHERE " +
    "(:fechaInicio IS NULL OR c.horarioConsulta >= :fechaInicio) AND " +
    "(:fechaFin IS NULL OR c.horarioConsulta < :fechaFin) AND " +
    "(:doctorId IS NULL OR c.doctor.id = :doctorId) AND " +
    "(:consultorioId IS NULL OR c.consultorio.id = :consultorioId)")
    List<Cita> findByFilters(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin,
        @Param("consultorioId") Long consultorioId,
        @Param("doctorId") Long doctorId);
        
    
    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE " +
    "c.consultorio = :consultorio AND " +
    "c.horarioConsulta BETWEEN :inicio AND :fin AND " +
    "c.id != :excludeId")
    boolean existsByConsultorioAndHorarioBetweenExcludingId(
        @Param("consultorio") Consultorio consultorio,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin,
        @Param("excludeId") Long excludeId);
    
        @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE " +
        "c.doctor = :doctor AND " +
        "c.horarioConsulta BETWEEN :inicio AND :fin AND " +
        "c.id != :excludeId")
 boolean existsByDoctorAndHorarioBetweenExcludingId(
     @Param("doctor") Doctor doctor,
     @Param("inicio") LocalDateTime inicio,
     @Param("fin") LocalDateTime fin,
     @Param("excludeId") Long excludeId);

 @Query("SELECT c FROM Cita c WHERE " +
        "c.nombrePaciente = :paciente AND " +
        "c.horarioConsulta BETWEEN :inicio AND :fin AND " +
        "c.id != :excludeId")
 List<Cita> findByPacienteAndHorarioBetweenExcludingId(
     @Param("paciente") String paciente,
     @Param("inicio") LocalDateTime inicio,
     @Param("fin") LocalDateTime fin,
     @Param("excludeId") Long excludeId);

 @Query("SELECT COUNT(c) FROM Cita c WHERE " +
        "c.doctor = :doctor AND " +
        "DATE(c.horarioConsulta) = :fecha AND " +
        "c.id != :excludeId")
 Long countByDoctorAndFechaExcludingId(
     @Param("doctor") Doctor doctor,
     @Param("fecha") LocalDate fecha,
     @Param("excludeId") Long excludeId);

}