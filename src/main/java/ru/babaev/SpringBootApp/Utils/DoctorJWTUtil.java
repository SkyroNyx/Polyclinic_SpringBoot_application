package ru.babaev.SpringBootApp.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.babaev.SpringBootApp.AppConstants.ApplicationConstants;
import ru.babaev.SpringBootApp.Models.Doctor;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class DoctorJWTUtil {

    @Value("${server.servlet.session.cookie.max-age}")
    private int expirationTime;
    public String generateToken(Doctor doctor) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusSeconds(expirationTime).toInstant());
        return JWT.create()
                .withSubject("doctor")
                .withClaim("id", doctor.getId())
                .withClaim("surname", doctor.getSurname())
                .withClaim("firstName", doctor.getFirstName())
                .withClaim("lastName", doctor.getLastName())
                .withClaim("category", doctor.getCategory())
                .withClaim("cost", doctor.getCost())
                .withClaim("experience", doctor.getExperience())
                .withClaim("specialization", doctor.getSpecialization())
                .withClaim("rating", Float.toString(doctor.getRating()))
                .withClaim("phoneNumber", doctor.getPhoneNumber())
                .withClaim("password", doctor.getPassword())
                .withIssuedAt(new Date())
                .withIssuer(ApplicationConstants.JWTIssuer)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(ApplicationConstants.JWTSecret));
    }

    public Doctor validateTokenAndRetrieveDoctor(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(ApplicationConstants.JWTSecret))
                .withSubject("doctor")
                .withIssuer(ApplicationConstants.JWTIssuer)
                .build();
        DecodedJWT jwt = verifier.verify(token);
        String ratingString = jwt.getClaim("rating").asString();
        Doctor doctor = new Doctor();
        doctor.setId(jwt.getClaim("id").asInt());
        doctor.setSurname(jwt.getClaim("surname").asString());
        doctor.setFirstName(jwt.getClaim("firstName").asString());
        doctor.setLastName(jwt.getClaim("lastName").asString());
        doctor.setCategory(jwt.getClaim("category").asString());
        doctor.setCost(jwt.getClaim("cost").asInt());
        doctor.setExperience(jwt.getClaim("experience").asInt());
        doctor.setSpecialization(jwt.getClaim("specialization").asString());
        doctor.setRating(Float.parseFloat(ratingString));
        doctor.setPhoneNumber(jwt.getClaim("phoneNumber").asString());
        doctor.setPassword(jwt.getClaim("password").asString());
        return doctor;
    }
}