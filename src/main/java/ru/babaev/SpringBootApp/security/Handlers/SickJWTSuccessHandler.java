package ru.babaev.SpringBootApp.security.Handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.babaev.SpringBootApp.Utils.SickJWTUtil;
import ru.babaev.SpringBootApp.security.Sick.SickDetails;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SickJWTSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final SickJWTUtil sickJwtUtil;

    @Autowired
    public SickJWTSuccessHandler(SickJWTUtil sickJwtUtil) {
        this.sickJwtUtil = sickJwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        SickDetails sickDetails = (SickDetails) authentication.getPrincipal();
        String jwtToken = sickJwtUtil.generateToken(sickDetails.getSick());
        Cookie jwtCookie = new Cookie("Authentication", jwtToken);
        response.addCookie(jwtCookie);
        setDefaultTargetUrl("/sick");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
