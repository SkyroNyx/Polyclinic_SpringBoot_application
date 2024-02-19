package ru.babaev.SpringBootApp.Errors;

import lombok.Data;

@Data
public class JWTVerificationError {
    private long timestamp;

    private String description;

    public JWTVerificationError(long timestamp, String description) {
        this.timestamp = timestamp;
        this.description = description;
    }
}
