package ru.babaev.SpringBootApp.security.Sick;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.babaev.SpringBootApp.Models.Sick;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SickDetails implements UserDetails {
    private final Sick sick;

    public SickDetails(Sick sick) {
        this.sick = sick;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_SICK"));
        return authorities;
    }

    @Override
    public String getPassword() {
        return sick.getPassword();
    }

    @Override
    public String getUsername() {
        return sick.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    public Sick getSick(){
        return sick;
    }
}