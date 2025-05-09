package kosmos.java.com.hospital.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kosmos.java.com.hospital.exceptions.ConflictException;
import kosmos.java.com.hospital.model.Cita;
import kosmos.java.com.hospital.repository.CitaRepository;

@Service
public class CitaService {
    
    @Autowired
    private CitaRepository citaRepository;    

    @Transactional
    public void crearCita(Cita cita) throws ConflictException {
        // Validaciones en orden de prioridad
        validarCamposObligatorios(cita);
        validarDisponibilidadConsultorio(cita);
        validarDisponibilidadDoctor(cita);
        validarCitasPaciente(cita);
        validarMaxCitasDiarias(cita);
        
        citaRepository.save(cita);
    }

    private void validarCamposObligatorios(Cita cita) {
        if(cita.getConsultorio() == null || 
           cita.getDoctor() == null || 
           cita.getHorarioConsulta() == null || 
           cita.getNombrePaciente() == null || 
           cita.getNombrePaciente().isBlank()) {
            throw new IllegalArgumentException("Todos los campos obligatorios deben ser completados");
        }
    }

    private void validarDisponibilidadConsultorio(Cita cita) {
        LocalDateTime inicioHora = cita.getHorarioConsulta().truncatedTo(ChronoUnit.HOURS);
        LocalDateTime finHora = inicioHora.plusHours(1);
        
        boolean consultorioOcupado = citaRepository.existsByConsultorioAndHorarioConsultaBetween(
            cita.getConsultorio(),
            inicioHora,
            finHora.minusNanos(1)
        );
        
        if(consultorioOcupado) {
            throw new ConflictException("El consultorio " + cita.getConsultorio().getNumeroConsultorio() 
                + " ya tiene una cita programada entre " 
                + inicioHora.format(DateTimeFormatter.ofPattern("HH:mm")) 
                + " y " 
                + finHora.format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }

    private void validarDisponibilidadDoctor(Cita cita) {
        LocalDateTime inicioHora = cita.getHorarioConsulta().truncatedTo(ChronoUnit.HOURS);
        LocalDateTime finHora = inicioHora.plusHours(1);
        
        boolean doctorOcupado = citaRepository.existsByDoctorAndHorarioConsultaBetween(
            cita.getDoctor(),
            inicioHora,
            finHora.minusNanos(1) // Evita solapamiento con hora siguiente
        );
        
        if(doctorOcupado) {
            throw new ConflictException("El Dr. " + cita.getDoctor().getApellidoPaterno() 
                + " ya tiene una cita entre " 
                + inicioHora.format(DateTimeFormatter.ofPattern("HH:mm"))
                + " y "
                + finHora.format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }

    private void validarCitasPaciente(Cita cita) {
        LocalDateTime inicioIntervalo = cita.getHorarioConsulta().minusHours(2);
        LocalDateTime finIntervalo = cita.getHorarioConsulta().plusHours(2);
        
        List<Cita> citasProximas = citaRepository
            .findByNombrePacienteAndHorarioConsultaBetween(
                cita.getNombrePaciente(),
                inicioIntervalo,
                finIntervalo
            );
        
        if(!citasProximas.isEmpty()) {
            throw new ConflictException("El paciente " + cita.getNombrePaciente() 
                + " tiene citas cercanas al horario seleccionado (mínimo 2 horas de diferencia requeridas)");
        }
    }

    private void validarMaxCitasDiarias(Cita cita) {
        LocalDate fechaCita = cita.getHorarioConsulta().toLocalDate();
        Long citasDiarias = citaRepository.countByDoctorAndFecha(
            cita.getDoctor(), 
            fechaCita
        );
        
        if(citasDiarias >= 8) {
            throw new ConflictException("El Dr. " + cita.getDoctor().getApellidoPaterno() 
                + " ya tiene el máximo de 8 citas programadas para el día " 
                + fechaCita.format(DateTimeFormatter.ISO_DATE));
        }
    }
    
    public List<Cita> buscarCitas(LocalDate fecha, Long doctorId, Long consultorioId) {
        if (fecha != null || doctorId != null || consultorioId != null) {
            System.out.println("ConsultorioId = " + consultorioId);
            System.out.println("DoctorId= " + doctorId);
            return citaRepository.findByFilters(
                fecha != null ? fecha.atStartOfDay() : null,
                fecha != null ? fecha.plusDays(1).atStartOfDay() : null,
                consultorioId,
                doctorId
            );
        }
        return citaRepository.findAll();
    }

    @Transactional
    public void cancelarCita(Long citaId) {
        Cita cita = citaRepository.findById(citaId)
            .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
        
        if (!cita.isCancelable()) {
            throw new IllegalStateException("Solo se pueden cancelar citas futuras");
        }
        
        citaRepository.delete(cita);
    }

    @Transactional
    public void actualizarCita(Long id, Cita citaActualizada) {
        Cita citaExistente = citaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
        
        validarEdicionPermitida(citaExistente);
        aplicarValidaciones(citaActualizada, citaExistente.getId());
        
        actualizarCampos(citaExistente, citaActualizada);
        citaRepository.save(citaExistente);
    }

    private void validarEdicionPermitida(Cita cita) {
        if (cita.getHorarioConsulta().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("No se puede editar una cita pasada");
        }
    }

    private void aplicarValidaciones(Cita cita, Long excludeId) {
        validarDisponibilidadConsultorio(cita, excludeId);
        validarDisponibilidadDoctor(cita, excludeId);
        validarCitasPaciente(cita, excludeId);
        validarMaxCitasDiarias(cita, excludeId);
    }

    private void validarDisponibilidadConsultorio(Cita cita, Long excludeId) {
        LocalDateTime inicio = cita.getHorarioConsulta().truncatedTo(ChronoUnit.HOURS);
        LocalDateTime fin = inicio.plusHours(1).minusNanos(1);
        
        boolean ocupado = citaRepository.existsByConsultorioAndHorarioBetweenExcludingId(
            cita.getConsultorio(), inicio, fin, excludeId);
        
        if (ocupado) {
            throw new ConflictException("Consultorio no disponible en este horario");
        }
    }

    private void validarDisponibilidadDoctor(Cita cita, Long excludeId) {
        LocalDateTime inicio = cita.getHorarioConsulta().truncatedTo(ChronoUnit.HOURS);
        LocalDateTime fin = inicio.plusHours(1).minusNanos(1);
        
        boolean ocupado = citaRepository.existsByDoctorAndHorarioBetweenExcludingId(
            cita.getDoctor(), inicio, fin, excludeId);
        
        if (ocupado) {
            throw new ConflictException("Doctor no disponible en este horario");
        }
    }

    private void validarCitasPaciente(Cita cita, Long excludeId) {
        LocalDateTime inicio = cita.getHorarioConsulta().minusHours(2);
        LocalDateTime fin = cita.getHorarioConsulta().plusHours(2);
        
        List<Cita> citas = citaRepository.findByPacienteAndHorarioBetweenExcludingId(
            cita.getNombrePaciente(), inicio, fin, excludeId);
        
        if (!citas.isEmpty()) {
            throw new ConflictException("El paciente tiene citas cercanas");
        }
    }

    private void validarMaxCitasDiarias(Cita cita, Long excludeId) {
        LocalDate fecha = cita.getHorarioConsulta().toLocalDate();
        Long count = citaRepository.countByDoctorAndFechaExcludingId(
            cita.getDoctor(), fecha, excludeId);
        
        if (count >= 8) {
            throw new ConflictException("Máximo 8 citas diarias por doctor");
        }
    }
    
    private void actualizarCampos(Cita existente, Cita actualizada) {
        existente.setConsultorio(actualizada.getConsultorio());
        existente.setDoctor(actualizada.getDoctor());
        existente.setHorarioConsulta(actualizada.getHorarioConsulta());
        existente.setNombrePaciente(actualizada.getNombrePaciente());
    }

    public Cita obtenerCitaParaEdicion(Long id) {
        Cita cita = citaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
        
        validarEdicionPermitida(cita);
        return cita;
    }
    
}