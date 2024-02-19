package ru.babaev.SpringBootApp.DTOs;

import lombok.Data;

@Data
public class DoctorDTO {

    private int id;

    private String surname;

    private String firstName;

    private String lastName;

    private String specialization;

    private String category;

    private int experience;

    private float rating;

    private String phoneNumber;

    private int cost;
}
