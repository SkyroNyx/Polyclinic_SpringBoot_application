package ru.babaev.SpringBootApp.DTOs;

import lombok.Data;
import java.util.List;

@Data
public class CreatePrescriptionDTO {

    private int appointmentId;

    private String diagnosis;

    private String recommendations;

    private List<CreatePrescriptionMedDTO> medicamentList;

    private String dateOfNextApp;

    private String timeOfNextApp;
}
