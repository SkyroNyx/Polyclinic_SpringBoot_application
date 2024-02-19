package ru.babaev.SpringBootApp.DTOs;

import lombok.Data;

@Data
public class CreatePrescriptionMedDTO {

    private String medicament;

    private String application;
}
