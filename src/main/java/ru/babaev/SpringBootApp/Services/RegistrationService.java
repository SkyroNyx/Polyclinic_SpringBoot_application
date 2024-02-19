package ru.babaev.SpringBootApp.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.babaev.SpringBootApp.DAOs.SickDAO;
import ru.babaev.SpringBootApp.ObjectsToValidate.SickToRegisterToValidate;
import ru.babaev.SpringBootApp.Models.Sick;

@Service
public class RegistrationService {

    private final PasswordEncoder passwordEncoder;
    private final SickDAO sickDAO;

    @Autowired
    public RegistrationService(PasswordEncoder passwordEncoder, SickDAO sickDAO) {
        this.passwordEncoder = passwordEncoder;
        this.sickDAO = sickDAO;
    }

    public void register(SickToRegisterToValidate sickToRegister) {
        Sick sick = new Sick();
        sick.setSurname(sickToRegister.getSurname());
        sick.setLastName(sickToRegister.getLastName());
        sick.setFirstName(sickToRegister.getFirstName());
        sick.setEmail(sickToRegister.getEmail());
        sick.setPhoneNumber(sickToRegister.getPhoneNumber());
        sick.setPassword(passwordEncoder.encode(sickToRegister.getPassword()));
        sick.setDisability(sickToRegister.getDisability());
        sickDAO.save(sick);
    }
}
