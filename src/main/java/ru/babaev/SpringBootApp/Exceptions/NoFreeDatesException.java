package ru.babaev.SpringBootApp.Exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NoFreeDatesException extends RuntimeException {

    public NoFreeDatesException(int doctorId) {
        this.doctorId = doctorId;
    }

    private int doctorId;
}
