package ru.babaev.SpringBootApp.security.Sick;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.babaev.SpringBootApp.Models.Sick;

import java.util.Optional;

@Service
public class SickSecurityService implements UserDetailsService {

    private final SickSecurityRepository sickSecurityRepository;
    @Autowired
    public SickSecurityService(SickSecurityRepository sickSecurityRepository) {
        this.sickSecurityRepository = sickSecurityRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<Sick> sickOptional = sickSecurityRepository.getSickByEmailOrPhoneNumber(login, login);
        if (sickOptional.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        else
            return new SickDetails(sickOptional.get());
    }
}