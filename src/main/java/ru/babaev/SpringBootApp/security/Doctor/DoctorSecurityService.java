package ru.babaev.SpringBootApp.security.Doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.babaev.SpringBootApp.Models.Doctor;

import java.util.Optional;
@Service
public class DoctorSecurityService implements UserDetailsService {
    private final DoctorSecurityRepository doctorSecurityRepository;

    @Autowired
    public DoctorSecurityService(DoctorSecurityRepository doctorSecurityRepository) {
        this.doctorSecurityRepository = doctorSecurityRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<Doctor> doctorOptional = doctorSecurityRepository.getDoctorByPhoneNumber(login);
        if (doctorOptional.isEmpty()) {
            throw new UsernameNotFoundException("Доктор не найден");
        }
        else {
            return new DoctorDetails(doctorOptional.get());
        }
    }
}