package ru.babaev.SpringBootApp.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.babaev.SpringBootApp.AppConstants.ApplicationConstants;
import ru.babaev.SpringBootApp.Models.Sick;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class SickJWTUtil {

    @Value("${server.servlet.session.cookie.max-age}")
    private int expirationTime;
    public String generateToken(Sick sick) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusSeconds(expirationTime).toInstant());
        return JWT.create()
                .withSubject("sick")
                .withClaim("id", sick.getId())
                .withClaim("surname", sick.getSurname())
                .withClaim("firstName", sick.getFirstName())
                .withClaim("lastName", sick.getLastName())
                .withClaim("email", sick.getEmail())
                .withClaim("phoneNumber", sick.getPhoneNumber())
                .withClaim("disability", sick.getDisability())
                .withClaim("password", sick.getPassword())
                .withIssuedAt(new Date())
                .withIssuer(ApplicationConstants.JWTIssuer)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(ApplicationConstants.JWTSecret));
    }

    public Sick validateTokenAndRetrieveSick(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(ApplicationConstants.JWTSecret))
                .withSubject("sick")
                .withIssuer(ApplicationConstants.JWTIssuer)
                .build();
        DecodedJWT jwt = verifier.verify(token);
        Sick sick = new Sick();
        sick.setId(jwt.getClaim("id").asInt());
        sick.setSurname(jwt.getClaim("surname").asString());
        sick.setFirstName(jwt.getClaim("firstName").asString());
        sick.setLastName(jwt.getClaim("lastName").asString());
        sick.setEmail(jwt.getClaim("email").asString());
        sick.setPhoneNumber(jwt.getClaim("phoneNumber").asString());
        sick.setDisability(jwt.getClaim("disability").asInt());
        sick.setPassword(jwt.getClaim("password").asString());
        return sick;
    }

}
