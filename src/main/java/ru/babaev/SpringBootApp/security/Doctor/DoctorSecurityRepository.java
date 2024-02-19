package ru.babaev.SpringBootApp.security.Doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.babaev.SpringBootApp.Models.Doctor;
import java.util.Optional;

@Repository
public interface DoctorSecurityRepository  extends JpaRepository<Doctor,Integer> {
    public Optional<Doctor> getDoctorByPhoneNumber(String phoneNumber);
}

