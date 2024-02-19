package ru.babaev.SpringBootApp.Errors;

import lombok.Data;

@Data
public class NoFreeDatesError {

    private long timestamp;

    private String description;

    public NoFreeDatesError(long timestamp, String description) {
        this.timestamp = timestamp;
        this.description = description;
    }
}
