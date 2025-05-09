package kosmos.java.com.hospital.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Consultorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer numeroConsultorio;
    private Integer piso;

    public Consultorio() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getNumeroConsultorio() {
        return numeroConsultorio;
    }
    public void setNumeroConsultorio(Integer numeroConsultorio) {
        this.numeroConsultorio = numeroConsultorio;
    }
    public Integer getPiso() {
        return piso;
    }
    public void setPiso(Integer piso) {
        this.piso = piso;
    }

    

}