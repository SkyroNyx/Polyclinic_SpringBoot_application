package ru.babaev.SpringBootApp.DTOs;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class AppointmentDTO {

    private int id;

    private int sickID;

    private String sickSurname;

    private String sickFirstName;

    private String sickLastName;

    private String time;

    private String complaints;

    private List<AttachedDocumentDTO> documentsList = new ArrayList<>();
}
