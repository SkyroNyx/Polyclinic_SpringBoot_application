package ru.babaev.SpringBootApp.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "прикрепленный_документ")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachedDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "код_прикрепленного_документа")
    private int id;

    @ManyToOne
    @JoinColumn(name="код_приема",referencedColumnName = "код_приема")
    @JsonIgnore
    private Appointment appointment;

    @Column(name = "название")
    private String name;

    @Column(name = "путь")
    private String path;
}
