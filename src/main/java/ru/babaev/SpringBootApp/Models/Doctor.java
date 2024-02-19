package ru.babaev.SpringBootApp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "врач")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "код_врача")
    private int id;

    @Column(name = "фамилия")
    private String surname;

    @Column(name = "имя")
    private String firstName;

    @Column(name = "отчество")
    private String lastName;

    @Column(name = "специализация")
    private String specialization;

    @Column(name = "категория")
    private String category;

    @Column(name = "стаж")
    private int experience;

    @Column(name = "рейтинг")
    private float rating;

    @Column(name = "пароль")
    private String password;

    @Column(name = "телефон")
    private String phoneNumber;

    @Column(name = "стоимость_приема")
    private int cost;

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointmentList;
}
