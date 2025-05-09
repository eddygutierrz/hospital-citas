package kosmos.java.com.hospital.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kosmos.java.com.hospital.exceptions.ConflictException;
import kosmos.java.com.hospital.model.Cita;
import kosmos.java.com.hospital.repository.ConsultorioRepository;
import kosmos.java.com.hospital.repository.DoctorRepository;
import kosmos.java.com.hospital.service.CitaService;

@Controller
@RequestMapping("/citas")
public class CitaController {
    
    @Autowired
    private CitaService citaService;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private ConsultorioRepository consultorioRepository;

    @GetMapping ("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("cita", new Cita());
        model.addAttribute("doctores", doctorRepository.findAll());
        model.addAttribute("consultorios", consultorioRepository.findAll());
        return "form-cita";
    }

    @PostMapping
    public String guardarCita(@ModelAttribute Cita cita, RedirectAttributes attrs) {
        try {
            citaService.crearCita(cita);
            attrs.addFlashAttribute("success", "Cita agendada exitosamente");
        } catch (ConflictException | IllegalArgumentException e) {
            attrs.addFlashAttribute("error", e.getMessage());
            attrs.addFlashAttribute("cita", cita); // Para mantener los datos del formulario
        }
        return "redirect:/citas/nueva";
    }

    @GetMapping
    public String listarCitas(
        @RequestParam(required = false) LocalDate fecha,
        @RequestParam(required = false) Long doctorId,
        @RequestParam(required = false) Long consultorioId,
        Model model) {

        List<Cita> citas = citaService.buscarCitas(fecha, doctorId, consultorioId);
        model.addAttribute("citas", citas);
        model.addAttribute("doctores", doctorRepository.findAll());
        model.addAttribute("consultorios", consultorioRepository.findAll());
        return "citas";
    }

    @GetMapping("/cancelar/{id}")
    public String cancelarCita(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            citaService.cancelarCita(id);
            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/citas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Cita cita = citaService.obtenerCitaParaEdicion(id);
        model.addAttribute("cita", cita);
        model.addAttribute("doctores", doctorRepository.findAll());
        model.addAttribute("consultorios", consultorioRepository.findAll());
        return "form-cita-edicion";
    }

    @PostMapping("/editar/{id}")
    public String procesarEdicion(
        @PathVariable Long id,
        @ModelAttribute Cita cita,
        RedirectAttributes attrs) {
        
        try {
            citaService.actualizarCita(id, cita);
            attrs.addFlashAttribute("success", "Cita actualizada correctamente");
        } catch (Exception e) {
            attrs.addFlashAttribute("error", e.getMessage());
            attrs.addFlashAttribute("cita", cita);
            return "redirect:/citas/editar/" + id;
        }
        return "redirect:/citas";
    }
}