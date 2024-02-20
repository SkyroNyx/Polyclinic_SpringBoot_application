package ru.babaev.SpringBootApp.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "лекарство_назначения")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrescriptionsMedicament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "код_лекарства_назначения")
    private int id;

    @Column(name = "название")
    private String medicament;

    @ManyToOne
    @JoinColumn(name="код_назначения",referencedColumnName = "код_назначения")
    @JsonIgnore
    private Prescription prescription;

    @Column(name = "применение")
    private String use;
}
