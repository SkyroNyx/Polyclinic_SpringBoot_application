package ru.babaev.SpringBootApp.DTOs;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class PrescriptionDTO {

    private String time;

    private int sickID;

    private String sickSurname;

    private String sickFirstName;

    private String sickLastName;

    private String complaints;

    private String diagnosis;

    private String recommendations;

    private List<PrescriptionsMedicamentDTO> prescriptionsMedicamentList = new ArrayList<>();
}
