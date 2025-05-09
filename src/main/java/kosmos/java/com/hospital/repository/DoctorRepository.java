package kosmos.java.com.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kosmos.java.com.hospital.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {}