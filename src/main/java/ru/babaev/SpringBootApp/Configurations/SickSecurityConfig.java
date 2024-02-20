package ru.babaev.SpringBootApp.Configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import ru.babaev.SpringBootApp.security.Handlers.SickCookieClearingLogoutHandler;
import ru.babaev.SpringBootApp.security.Handlers.SickJWTSuccessHandler;
import ru.babaev.SpringBootApp.security.Sick.SickAuthenticationProvider;

@Order(1)
@Configuration
@EnableWebSecurity
public class SickSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SickAuthenticationProvider sickAuthenticationProvider;
    private final SickCookieClearingLogoutHandler sickCookieClearingLogoutHandler;
    private final SickJWTSuccessHandler sickJWTSuccessHandler;

    @Autowired
    public SickSecurityConfig(SickAuthenticationProvider sickAuthenticationProvider,
                              SickCookieClearingLogoutHandler sickCookieClearingLogoutHandler,
                              SickJWTSuccessHandler sickJWTSuccessHandler) {
        this.sickAuthenticationProvider = sickAuthenticationProvider;
        this.sickCookieClearingLogoutHandler = sickCookieClearingLogoutHandler;
        this.sickJWTSuccessHandler = sickJWTSuccessHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(sickAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringAntMatchers( "/sick/loginAsSick", "/sick/logout")
                .and()
                .antMatcher("/sick/**").authorizeRequests()
                .antMatchers("/sick/**").hasRole("SICK")
                .antMatchers("/auth","/auth/sickLoginPage","/auth/registrationPage").anonymous()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/auth/sickLoginPage")
                .loginProcessingUrl("/sick/loginAsSick")
                .defaultSuccessUrl("/sick", true)
                .failureUrl("/auth/sickLoginPage?error")
                .successHandler(sickJWTSuccessHandler)
                .and()
                .logout().logoutUrl("/sick/logout")
                .logoutSuccessUrl("/auth")
                .deleteCookies("JSESSIONID", "Authentication")
                .addLogoutHandler(sickCookieClearingLogoutHandler);
    }
}
