package ru.babaev.SpringBootApp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="прием")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "код_приема")
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="код_больного",referencedColumnName = "код_больного")
    private Sick sick;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="код_врача",referencedColumnName = "код_врача")
    private Doctor doctor;

    @Column(name = "дата")
    private Date date;

    @Column(name = "время")
    private Time time;

    @Column(name = "жалобы")
    private String complaints ;

    @Column(name = "прошел")
    private boolean past;

    @OneToOne(mappedBy = "appointment")
    private Prescription prescription;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<AttachedDocument> attachedDocumentsList;

    public String getFormatedDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

    public String getFormatedTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(time);
    }
}
