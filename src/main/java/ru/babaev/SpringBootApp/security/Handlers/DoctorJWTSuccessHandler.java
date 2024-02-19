package ru.babaev.SpringBootApp.security.Handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.babaev.SpringBootApp.Utils.DoctorJWTUtil;
import ru.babaev.SpringBootApp.security.Doctor.DoctorDetails;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class DoctorJWTSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final DoctorJWTUtil doctorJWTUtil;

    @Autowired
    public DoctorJWTSuccessHandler(DoctorJWTUtil doctorJWTUtil) {
        this.doctorJWTUtil = doctorJWTUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        DoctorDetails doctorDetails = (DoctorDetails) authentication.getPrincipal();
        String jwtToken = doctorJWTUtil.generateToken(doctorDetails.getDoctor());
        Cookie jwtCookie = new Cookie("Authentication", jwtToken);
        response.addCookie(jwtCookie);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
