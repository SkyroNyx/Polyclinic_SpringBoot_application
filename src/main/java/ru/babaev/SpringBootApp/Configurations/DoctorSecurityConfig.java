package ru.babaev.SpringBootApp.Configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import ru.babaev.SpringBootApp.security.Doctor.DoctorAuthenticationProvider;
import ru.babaev.SpringBootApp.security.Handlers.DoctorJWTSuccessHandler;

@Configuration
@EnableWebSecurity
@Order(2)
public class DoctorSecurityConfig extends WebSecurityConfigurerAdapter {

    private final DoctorAuthenticationProvider doctorAuthenticationProvider;
    private final DoctorJWTSuccessHandler doctorJWTSuccessHandler;

    @Autowired
    public DoctorSecurityConfig(DoctorAuthenticationProvider doctorAuthenticationProvider,
                                DoctorJWTSuccessHandler doctorJWTSuccessHandler) {
        this.doctorAuthenticationProvider = doctorAuthenticationProvider;
        this.doctorJWTSuccessHandler = doctorJWTSuccessHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(doctorAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringAntMatchers( "/doctor/loginAsDoctor", "/doctor/logout")
                .and()
                .antMatcher("/doctor/**").authorizeRequests()
                .antMatchers("/doctor/**").hasRole("DOCTOR")
                .antMatchers("/auth","/auth/doctorLoginPage").anonymous()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/auth/doctorLoginPage")
                .loginProcessingUrl("/doctor/loginAsDoctor")
                .defaultSuccessUrl("/doctor", true)
                .failureUrl("/auth/doctorLoginPage?error")
                .successHandler(doctorJWTSuccessHandler)
                .and()
                .logout().logoutUrl("/doctor/logout")
                .logoutSuccessUrl("/auth")
                .deleteCookies("JSESSIONID", "peekedDateInPersonalAccount", "Authentication");
    }
}

