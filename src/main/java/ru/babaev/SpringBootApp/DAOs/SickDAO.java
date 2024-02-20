package ru.babaev.SpringBootApp.DAOs;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.babaev.SpringBootApp.Models.*;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
@Transactional
public class SickDAO {

    private final SimpleDateFormat to_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    private final SessionFactory sessionFactory;

    @Autowired
    public SickDAO(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Appointment> currentSickAppointments(int sickId){
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Appointment ap where ap.sick.id = " + sickId + " and " +
                " ap.past = false order by ap.date asc,ap.time asc", Appointment.class).getResultList();
    }

    public List<Doctor> getFilteredDoctorsList(String[] specialisations, String[] categories,
                                               String experienceSign, String experienceNumber){
        boolean specialisationIsEmpty = specialisations.length == 0;
        boolean categoryIsEmpty = categories.length == 0;
        boolean experienceIsEmpty = experienceNumber.isBlank() || experienceSign.isBlank();
        String parameters = "where 1=1";
        if (!specialisationIsEmpty) {
            parameters += " and d.specialization in ('" + String.join("','",specialisations) + "')";
        }
        if (!categoryIsEmpty) {
            parameters += " and d.category in ('" + String.join("','",categories) + "')";
        }
        if (!experienceIsEmpty) {
            parameters += " and d.experience " + experienceSign + " " + experienceNumber;
        }
        Session session = sessionFactory.getCurrentSession();
        String query = "from Doctor d " + parameters;
        return session.createQuery(query , Doctor.class).getResultList();
    }

    public boolean haveAnAppointmentWithCurrentDoctor(int sickId, int doctorId) {
        Session session = sessionFactory.getCurrentSession();
        List<Appointment> appointmentList = session.createQuery("from Appointment p where p.doctor.id = "+doctorId+
                " and p.sick.id = " + sickId + " and p.past = false", Appointment.class).getResultList();
        return !appointmentList.isEmpty();
    }

    public Appointment getAppointmentWithCurrentDoctor(int sickId, int doctorId) {
        Session session = sessionFactory.getCurrentSession();
        List<Appointment> appointmentList = session.createQuery("from Appointment p where p.doctor.id = " + doctorId +
                " and p.sick.id = " + sickId + "", Appointment.class).getResultList();
        if (!appointmentList.isEmpty()) {
            return appointmentList.get(0);
        }
        else {
            return null;
        }
    }

    public LinkedHashMap<String, List<Prescription>> getDatesPrescriptionsHashMap(int sickId) {
        Session session = sessionFactory.getCurrentSession();
        List<Date> dates = session.createQuery("select distinct p.appointment.date from Prescription p where p.appointment.sick.id" +
                " = " + sickId + " order by p.appointment.date desc ", Date.class).getResultList();
        LinkedHashMap<String,List<Prescription>> stringPrescriptionHashMap = new LinkedHashMap<>();
        for (Date date : dates) {
            stringPrescriptionHashMap.put(to_YYYY_MM_DD.format(date),prescriptionsByDate(date, sickId));
        }
        return stringPrescriptionHashMap;
    }

    private List<Prescription> prescriptionsByDate(Date date, int sickId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Prescription p where p.appointment.date = '" + to_YYYY_MM_DD.format(date) + "' and " +
                "p.appointment.sick.id = " + sickId + " order by p.appointment.time desc ", Prescription.class).getResultList();
    }

    public void updateSick(Sick sick) {
        Session session = sessionFactory.getCurrentSession();
        session.update(sick);
    }

    public void saveAppointment(Appointment appointment) {
        Session session = sessionFactory.getCurrentSession();
        session.save(appointment);
    }

    public void saveDocuments(List<AttachedDocument> documents) {
        if (!documents.isEmpty()) {
            Session session = sessionFactory.getCurrentSession();
            for (AttachedDocument document : documents) {
                session.save(document);
            }
        }
    }

    public void save(Sick sick) {
        Session session = sessionFactory.getCurrentSession();
        session.save(sick);
    }
}
