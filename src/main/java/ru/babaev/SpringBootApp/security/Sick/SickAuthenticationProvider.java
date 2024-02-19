package ru.babaev.SpringBootApp.security.Sick;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SickAuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {

    private final SickSecurityService sickSecurityService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SickAuthenticationProvider(SickSecurityService sickSecurityService,
                                      PasswordEncoder passwordEncoder) {
        this.sickSecurityService = sickSecurityService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();
        SickDetails sickDetails = (SickDetails) sickSecurityService.loadUserByUsername(login);
        String password  = authentication.getCredentials().toString();
//        if (!passwordEncoder.matches(password, sickDetails.getPassword())) {
        if (!password.equals(sickDetails.getPassword())) {
                throw new BadCredentialsException("Введен неверный пароль");
        }
        return new UsernamePasswordAuthenticationToken(sickDetails, sickDetails.getPassword(), sickDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
