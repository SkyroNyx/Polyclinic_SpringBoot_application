package ru.babaev.SpringBootApp.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.babaev.SpringBootApp.ObjectsToValidate.SickToRegisterToValidate;
import ru.babaev.SpringBootApp.Services.RegistrationService;
import ru.babaev.SpringBootApp.Validators.RegistrationValidator;
import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {
    private final RegistrationValidator registrationValidator;
    private final RegistrationService registrationService;

    @Autowired
    public AuthenticationController(RegistrationValidator registrationValidator,
                                    RegistrationService registrationService) {
        this.registrationValidator = registrationValidator;
        this.registrationService = registrationService;
    }

    @GetMapping
    public String authPage(){
        return "/authentication/main-page";
    }

    @GetMapping("/doctorLoginPage")
    public String doctorLoginPage(){
        return "/authentication/doctor-login";
    }

    @GetMapping("/sickLoginPage")
    public String sickLoginPage() {
        return "/authentication/sick-login";
    }

    @GetMapping("/registrationPage")
    public String registrationPage(@ModelAttribute("sick") SickToRegisterToValidate sickToRegister) {
        return "/authentication/sick-register";
    }

    @PostMapping("/register")
    public String registerSick(@ModelAttribute("sick") @Valid SickToRegisterToValidate sickToRegister,
                               BindingResult bindingResult) {
        registrationValidator.validate(sickToRegister, bindingResult);
        if (bindingResult.hasErrors())
            return "/authentication/sick-register";
        registrationService.register(sickToRegister);
        return "redirect:/auth/sickLoginPage";
    }
}
