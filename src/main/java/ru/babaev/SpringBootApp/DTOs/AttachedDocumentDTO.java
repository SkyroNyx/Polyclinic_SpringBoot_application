package ru.babaev.SpringBootApp.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class AttachedDocumentDTO {

    private int id;

    private String name;
}
