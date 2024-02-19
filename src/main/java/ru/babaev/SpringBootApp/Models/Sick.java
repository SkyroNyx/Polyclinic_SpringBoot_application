package ru.babaev.SpringBootApp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
@Table(name = "больной")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "код_больного")
    private int id;

    @Column(name = "фамилия")
    private String surname;

    @Column(name = "имя")
    private String firstName;

    @Column(name = "отчество")
    private String lastName;

    @Column(name = "почта")
    @Email(message = "Введите корректный email")
    @NotEmpty(message = "Это поле не должно быть пустым")
    private String email;

    @Column(name = "пароль")
    private String password;

    @Column(name = "телефон")
    private String phoneNumber;

    @Column(name = "инвалидность")
    private int disability;

    @OneToMany(mappedBy = "sick")
    private List<Appointment> appointmentList;
}

