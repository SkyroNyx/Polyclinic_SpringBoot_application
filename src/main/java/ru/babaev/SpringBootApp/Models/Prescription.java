package ru.babaev.SpringBootApp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="назначение")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "код_назначения")
    private int id;

    @OneToOne
    @JoinColumn(name = "код_приема",referencedColumnName = "код_приема")
    private Appointment appointment;

    @Column(name = "диагноз")
    private String diagnosis;

    @Column(name = "рекомендации")
    private String recommendations;

    @OneToMany(mappedBy = "prescription",fetch = FetchType.EAGER)
    private List<PrescriptionsMedicament> prescriptionsMedicamentList;
}
