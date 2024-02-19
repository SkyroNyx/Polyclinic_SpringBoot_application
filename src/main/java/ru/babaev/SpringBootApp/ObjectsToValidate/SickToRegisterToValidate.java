package ru.babaev.SpringBootApp.ObjectsToValidate;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class SickToRegisterToValidate {

    private String surname;

    private String firstName;

    private String lastName;

    @NotEmpty(message = "Это поле не должно быть пустым")
    @Email(message = "Введите корректный email")
    private String email;

    private String phoneNumber;

    private String password;

    private String repeatedPassword;

    private int disability;
}
