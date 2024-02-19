package ru.babaev.SpringBootApp.Validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.babaev.SpringBootApp.Models.Sick;
import ru.babaev.SpringBootApp.Services.SickService;
import java.util.Optional;

@Component
public class SickValidator implements Validator {

    private final SickService sickService;

    @Autowired
    public SickValidator(SickService sickService) {
        this.sickService = sickService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Sick.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Sick sick = (Sick) target;
        Optional<Sick> sickWithEmailAlreadyExists = sickService.getSickByEmail(sick.getEmail());
        Optional<Sick> sickWithPhoneNumberAlreadyExists = sickService.getSickByPhoneNumber(sick.getPhoneNumber());
        if (sickWithEmailAlreadyExists.isPresent() && sickWithEmailAlreadyExists.get().getId() != sick.getId()) {
            errors.rejectValue("email", "", "Email уже занят");
        }
        if (sick.getPhoneNumber().isBlank()) {
            errors.rejectValue("phoneNumber", "", "Это поле не должно быть пустым");
        }
        else if (!sick.getPhoneNumber().matches("\\d{11,}")) {
            errors.rejectValue("phoneNumber", "", "Введите корректный номер");
        }
        if (sickWithPhoneNumberAlreadyExists.isPresent() && sickWithPhoneNumberAlreadyExists.get().getId() != sick.getId()) {
            errors.rejectValue("phoneNumber", "", "Номер уже занят");
        }
    }
}
