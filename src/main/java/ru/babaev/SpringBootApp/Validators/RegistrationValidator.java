package ru.babaev.SpringBootApp.Validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.babaev.SpringBootApp.ObjectsToValidate.SickToRegisterToValidate;
import ru.babaev.SpringBootApp.Models.Sick;
import ru.babaev.SpringBootApp.Services.SickService;
import java.util.Optional;

@Component
public class RegistrationValidator  implements Validator {

    private final SickService sickService;

    @Autowired
    public RegistrationValidator(SickService sickService) {
        this.sickService = sickService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return SickToRegisterToValidate.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        SickToRegisterToValidate sickToRegister = (SickToRegisterToValidate) o;
        Optional<Sick> sickWithEmailAlreadyExists = sickService.getSickByEmail(sickToRegister.getEmail());
        Optional<Sick> sickWithPhoneNumberAlreadyExists = sickService.getSickByPhoneNumber(sickToRegister.getPhoneNumber());
        String nameFieldsPattern = "^[a-zA-Zа-яА-Я\\s-]+$";
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?!.*\\s).+$";
        //фамилия
        if (sickToRegister.getSurname().isBlank()) {
            errors.rejectValue("surname", "", "Это поле не должно быть пустым");
        }
        else if (sickToRegister.getSurname().length() < 2 || sickToRegister.getSurname().length() > 50) {
            errors.rejectValue("surname", "", "Длина поля должна быть от 2 до 50 символов");
        }
        else if (!sickToRegister.getSurname().matches(nameFieldsPattern)) {
            errors.rejectValue("surname", "", "Это поле должно содержать только буквенные символы");
        }
        //имя
        if (sickToRegister.getFirstName().isBlank()) {
            errors.rejectValue("firstName", "", "Это поле не должно быть пустым");
        }
        else if (sickToRegister.getFirstName().length() < 2 || sickToRegister.getFirstName().length() > 50) {
            errors.rejectValue("firstName", "", "Длина поля должна быть от 2 до 50 символов");
        }
        else if (!sickToRegister.getFirstName().matches(nameFieldsPattern)) {
            errors.rejectValue("firstName", "", "Это поле должно содержать только буквенные символы");
        }
        //отчество
        if (sickToRegister.getLastName().isBlank()) {
            errors.rejectValue("lastName", "", "Это поле не должно быть пустым");
        }
        else if (sickToRegister.getLastName().length() < 2 || sickToRegister.getLastName().length() > 50) {
            errors.rejectValue("lastName", "", "Длина поля должна быть от 2 до 50 символов");
        }
        else if (!sickToRegister.getLastName().matches(nameFieldsPattern)) {
            errors.rejectValue("lastName", "", "Это поле должно содержать только буквенные символы");
        }
        //email
        if (sickWithEmailAlreadyExists.isPresent()) {
            errors.rejectValue("email", "", "Email уже занят");
        }
        //номер телефона
        if (sickToRegister.getPhoneNumber().isBlank()) {
            errors.rejectValue("phoneNumber", "", "Это поле не должно быть пустым");
        }
        else if (!sickToRegister.getPhoneNumber().matches("\\d{11,}")) {
            errors.rejectValue("phoneNumber", "", "Введите корректный номер");
        }
        else if (sickWithPhoneNumberAlreadyExists.isPresent()) {
            errors.rejectValue("phoneNumber", "", "Номер уже занят");
        }
        //пароль
        if (sickToRegister.getPassword().isBlank()) {
            errors.rejectValue("password", "", "Это поле не должно быть пустым");
        }
        else if (sickToRegister.getPassword().length() < 8 || sickToRegister.getFirstName().length() > 50) {
            errors.rejectValue("password", "", "Длина поля должна быть от 8 до 50 символов");
        }
        else if (!sickToRegister.getPassword().matches(passwordPattern)) {
            errors.rejectValue("password", "", "Пароль должен содержать хотя бы одну цифру, одну латинскую букву и не должен содержать пробелов");
        }
        //повторенный пароль
        if (sickToRegister.getRepeatedPassword().isBlank()) {
            errors.rejectValue("repeatedPassword", "", "Это поле не должно быть пустым");
        }
        else if (!sickToRegister.getPassword().equals(sickToRegister.getRepeatedPassword()) && !sickToRegister.getPassword().isBlank()) {
            errors.rejectValue("repeatedPassword", "", "Пароли должны совпадать");
        }
    }
}
