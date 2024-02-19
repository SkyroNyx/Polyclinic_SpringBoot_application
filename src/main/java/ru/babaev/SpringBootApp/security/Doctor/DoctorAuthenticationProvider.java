package ru.babaev.SpringBootApp.security.Doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DoctorAuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {
    private final DoctorSecurityService doctorSecurityService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DoctorAuthenticationProvider(DoctorSecurityService doctorSecurityService,
                                        PasswordEncoder passwordEncoder) {
        this.doctorSecurityService = doctorSecurityService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();
        DoctorDetails doctorDetails = (DoctorDetails) doctorSecurityService.loadUserByUsername(login);
        String password = authentication.getCredentials().toString();
//        if (!passwordEncoder.matches(password, doctorDetails.getPassword())) {
        if (!password.equals(doctorDetails.getPassword())) {
            throw new BadCredentialsException("Введен неверный пароль");
        }
        return new UsernamePasswordAuthenticationToken(doctorDetails, doctorDetails.getPassword(), doctorDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
