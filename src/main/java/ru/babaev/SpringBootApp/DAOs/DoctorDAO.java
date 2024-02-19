package ru.babaev.SpringBootApp.DAOs;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.babaev.SpringBootApp.Exceptions.NoFreeDatesException;
import ru.babaev.SpringBootApp.Models.*;

import javax.transaction.Transactional;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class DoctorDAO {

    private final SessionFactory sessionFactory;

    private final SimpleDateFormat to_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    private final SimpleDateFormat to_HH_MM = new SimpleDateFormat("HH:mm");

    @Autowired
    public DoctorDAO(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Appointment> getAppointmentsByDate(String date, int doctorId) {
        Session session = sessionFactory.getCurrentSession();
        List<Appointment> appointments = session.createQuery("select distinct  a from Appointment a " +
                        " left join fetch a.attachedDocumentsList where a.date = '"
                        + date + "' and a.doctor.id = " + doctorId +
                        " and a.past = false order by a.time ", Appointment.class)
                .getResultList();
        return appointments;
    }

    public LinkedHashMap<String, List<Prescription>> getDatesPrescriptionsHashMap(int doctorId, String phoneNumber) {
        String queryPhoneParameter = "";
        if (!phoneNumber.isBlank()) {
            queryPhoneParameter = "and p.appointment.sick.phoneNumber = '" + phoneNumber + "'";
        }
        Session session = sessionFactory.getCurrentSession();
        List<Date> dates = session.createQuery("select distinct p.appointment.date from Prescription p" +
                " where p.appointment.doctor.id" +
                " = " + doctorId + " " + queryPhoneParameter + " order by p.appointment.date desc ", Date.class).getResultList();
        LinkedHashMap<String,List<Prescription>> stringPrescriptionHashMap = new LinkedHashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        for (Date date : dates) {
            stringPrescriptionHashMap.put(dateFormat.format(date),getPrescriptionsByDate(date, doctorId, phoneNumber));
        }
        return stringPrescriptionHashMap;
    }

    private List<Prescription> getPrescriptionsByDate(Date date, int doctorId, String phoneNumber) {
        String queryPart = "";
        if (!phoneNumber.isBlank()) {
            queryPart = "and p.appointment.sick.phoneNumber = '" + phoneNumber + "'";
        }
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Prescription p where p.appointment.date = '" + to_YYYY_MM_DD.format(date) + "' and " +
                "p.appointment.doctor.id = " + doctorId + " " + queryPart +
                " order by p.appointment.time desc ", Prescription.class).getResultList();
    }

    public String getNearestFreeDate(int doctorId, int maxNumOfDays) throws NoFreeDatesException {
        Date today = new Date();
        String date = to_YYYY_MM_DD.format(today);
        Calendar calendar = Calendar.getInstance();
        int counterOfDays = 1;
        while (dayIsWeekend(date) || getFreeTimesByDate(date, doctorId).isEmpty()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date = to_YYYY_MM_DD.format(calendar.getTime());
            if (counterOfDays > maxNumOfDays) {
                throw new NoFreeDatesException(doctorId);
            }
            counterOfDays++;
        }
        return date;
    }

    private boolean dayIsWeekend(String dateStr) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateStr, dateFormatter);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    public List<String> getFreeTimesByDate(String date, int did){
        Session session = sessionFactory.getCurrentSession();
        List<String> times = new ArrayList<>(List.of("09:00","09:30","10:00","10:30","11:00"
                ,"11:30","12:00","12:30", "13:00","13:30","14:00","14:30","15:00","15:30","16:00","16:30","17:00"));
        if (date.equals(to_YYYY_MM_DD.format(new Date()))) {
            times = filterByCurrentTimeList(times);
            if (times.isEmpty()) {
                return times;
            }
        }
        List<Date> busyTimes = session.createQuery("select ap.time from Appointment ap where ap.doctor.id = "+ did +
                        " and ap.past = false and ap.date = '" + date + "'", java.util.Date.class).getResultList();
        if (!busyTimes.isEmpty()) {
            List<String> busyTimesString = busyTimes.stream().
                    map(to_HH_MM::format).toList();
            times.removeAll(busyTimesString);
        }
        return times;
    }

    private List<String> filterByCurrentTimeList(List<String> originalList) {
        LocalTime currentTime = LocalTime.now();
        return originalList.stream()
                .filter(time -> LocalTime.parse(time).isAfter(currentTime))
                .collect(Collectors.toList());
    }

    public boolean alreadyHaveAppointmentInDateAndTime(int doctorId, String date, String time) {
        Session session = sessionFactory.getCurrentSession();
        List<Appointment> appointments =  session.createQuery("from Appointment a where a.doctor.id = " + doctorId +
                " and a.date = '" + date + "' and a.time = '" + time + "' and a.past = false", Appointment.class).getResultList();
        return !appointments.isEmpty();
    }

    public void save(Doctor doctor) {
        Session session = sessionFactory.getCurrentSession();
        session.save(doctor);
    }

    public void savePrescription(Prescription prescription) {
        Session session = sessionFactory.getCurrentSession();
        session.save(prescription);
    }

    public void savePrescriptionMedicaments(List<PrescriptionsMedicament> medicaments) {
        Session session = sessionFactory.getCurrentSession();
        if (!medicaments.isEmpty()) {
            for (PrescriptionsMedicament prescriptionsMedicament : medicaments) {
                session.save(prescriptionsMedicament);
            }
        }
    }

    public void saveAppointment(Appointment appointment) {
        Session session = sessionFactory.getCurrentSession();
        session.save(appointment);
    }

    public void updateAppointment(Appointment appointment) {
        Session session = sessionFactory.getCurrentSession();
        session.update(appointment);
    }

    public void updateDocuments(List<AttachedDocument> documents) {
        if (!documents.isEmpty()) {
            Session session = sessionFactory.getCurrentSession();
            for (AttachedDocument document : documents) {
                session.update(document);
            }
        }
    }

    public void deleteAppointment(Appointment appointment) {
        Session session = sessionFactory.getCurrentSession();
        List<AttachedDocument> documents = appointment.getAttachedDocumentsList();
        if (!documents.isEmpty()) {
            for (AttachedDocument document : documents) {
                if (deleteFile(document.getPath())) {
                    session.delete(document);
                }
            }
        }
        session.delete(appointment);
    }

    public void deleteDocuments(List<AttachedDocument> documents) {
        if (!documents.isEmpty()) {
            Session session = sessionFactory.getCurrentSession();
            for (AttachedDocument document : documents) {
                if (deleteFile(document.getPath())) {
                    session.delete(document);
                }
            }
        }
    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.delete();
    }
}
