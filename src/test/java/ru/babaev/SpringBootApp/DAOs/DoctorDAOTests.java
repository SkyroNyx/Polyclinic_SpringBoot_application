package ru.babaev.SpringBootApp.DAOs;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.babaev.SpringBootApp.Models.Appointment;
import ru.babaev.SpringBootApp.Models.Doctor;
import ru.babaev.SpringBootApp.Models.Sick;
import ru.babaev.SpringBootApp.Repositories.SickRepository;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;


@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class DoctorDAOTests {

    @Autowired
    private SickDAO sickDAO;
    @Autowired
    private DoctorDAO doctorDAO;
    private final SimpleDateFormat to_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
//    private final SimpleDateFormat to_HH_MM = new SimpleDateFormat("HH:mm");


    @Test
    @DirtiesContext
    public void getAppointmentsByDate_returnEmptyList() {
        List<Appointment> appointments = doctorDAO.getAppointmentsByDate("2024-02-26", 1);
        Assertions.assertNotNull(appointments);
        Assertions.assertEquals(0, appointments.size());
    }

    @Test
    @DirtiesContext
    public void getAppointmentsByDate_returnOnlyRequestedDateAppointments() throws ParseException {
        int doctorId = 1;
        Doctor doctor = createTestDoctor(doctorId);
        Sick sick = createTestSick();
        String requestedDate = "2024-02-26";
        String randomDate = "3000-01-01";
        Appointment rightDateAppointment = createTestAppointment(doctor, sick, false, requestedDate, Time.valueOf(LocalTime.of(10, 0, 0)));
        Appointment wrongDateAppointment = createTestAppointment(doctor, sick, false, randomDate ,Time.valueOf(LocalTime.of(11, 0, 0)));
        sickDAO.save(sick);
        doctorDAO.save(doctor);
        doctorDAO.saveAppointment(rightDateAppointment);
        doctorDAO.saveAppointment(wrongDateAppointment);
        List<Appointment> appointments = doctorDAO.getAppointmentsByDate(requestedDate, doctorId);
        Assertions.assertNotNull(appointments);
        Assertions.assertEquals(1, appointments.size());
        Assertions.assertEquals(requestedDate, to_YYYY_MM_DD.format(appointments.get(0).getDate()));
    }

    @Test
    @DirtiesContext
    public void getAppointmentsByDate_returnOnlyRequestedDoctorsAppointments() throws ParseException {
        int requestedDoctorId = 1;
        int randomDoctorId = 2;
        Doctor requestedDoctor = createTestDoctor(requestedDoctorId);
        Doctor randomDoctor = createTestDoctor(randomDoctorId);
        Sick sick = createTestSick();
        String date = "2024-02-26";
        Appointment requestedDoctorAppointment = createTestAppointment(requestedDoctor, sick, false, date, Time.valueOf(LocalTime.of(10, 0, 0)));
        Appointment randomDoctorAppointment = createTestAppointment(randomDoctor, sick, false, date, Time.valueOf(LocalTime.of(11, 0, 0)));
        sickDAO.save(sick);
        doctorDAO.save(requestedDoctor);
        doctorDAO.save(randomDoctor);
        doctorDAO.saveAppointment(requestedDoctorAppointment);
        doctorDAO.saveAppointment(randomDoctorAppointment);
        List<Appointment> appointments = doctorDAO.getAppointmentsByDate(date, requestedDoctorId);
        Assertions.assertNotNull(appointments);
        Assertions.assertEquals(1, appointments.size());
        Assertions.assertEquals(requestedDoctorId, appointments.get(0).getDoctor().getId());
    }

    @Test
    @DirtiesContext
    public void getAppointmentsByDate_returnOnlyNotPastAppointments() throws ParseException {
        int doctorId = 1;
        Doctor doctor = createTestDoctor(doctorId);
        Sick sick = createTestSick();
        String date = "2024-02-26";
        Appointment notPastAppointment = createTestAppointment(doctor, sick, false, date, Time.valueOf(LocalTime.of(10, 0, 0)));
        Appointment pastAppointment = createTestAppointment(doctor, sick, true, date, Time.valueOf(LocalTime.of(11, 0, 0)));
        sickDAO.save(sick);
        doctorDAO.save(doctor);
        doctorDAO.saveAppointment(notPastAppointment);
        doctorDAO.saveAppointment(pastAppointment);
        List<Appointment> appointments = doctorDAO.getAppointmentsByDate(date, doctorId);
        Assertions.assertNotNull(appointments);
        Assertions.assertEquals(1, appointments.size());
        Assertions.assertFalse(appointments.get(0).isPast());
    }

    @Test
    @DirtiesContext
    public void getAppointmentsByDate_returnSortedByTimeAppointments() throws ParseException {
        int doctorId = 1;
        Doctor doctor = createTestDoctor(doctorId);
        Sick sick = createTestSick();
        String date = "2024-02-26";
        Appointment elevenHourAppointment = createTestAppointment(doctor, sick, false, date,
                Time.valueOf(LocalTime.of(11, 0, 0)));
        Appointment twelveHourAppointment = createTestAppointment(doctor, sick, false, date,
                Time.valueOf(LocalTime.of(12, 0, 0)));
        Appointment tenHourAppointment = createTestAppointment(doctor, sick, false, date,
                Time.valueOf(LocalTime.of(10, 0, 0)));
        sickDAO.save(sick);
        doctorDAO.save(doctor);
        doctorDAO.saveAppointment(elevenHourAppointment);
        doctorDAO.saveAppointment(twelveHourAppointment);
        doctorDAO.saveAppointment(tenHourAppointment);
        List<Appointment> appointments = doctorDAO.getAppointmentsByDate(date, doctorId);
        Comparator<Appointment> comparator = Comparator.comparing(Appointment::getTime);
        boolean appointmentsAreSortedByTime = IntStream.range(0, appointments.size() - 1)
                .allMatch(i -> comparator.compare(appointments.get(i), appointments.get(i + 1)) <= 0);
        Assertions.assertNotNull(appointments);
        Assertions.assertFalse(appointments.isEmpty());
        Assertions.assertEquals(3, appointments.size());
        Assertions.assertTrue(appointmentsAreSortedByTime);
    }

    private Appointment createTestAppointment(Doctor doctor, Sick sick, Boolean past, String date, Time time) throws ParseException {
            return Appointment.builder()
                    .date(to_YYYY_MM_DD.parse(date))
                    .doctor(doctor)
                    .sick(sick)
                    .past(past)
                    .time(time)
                    .build();

    }

    private Doctor createTestDoctor(int id) {
        return Doctor.builder()
                .id(id)
                .surname("test")
                .firstName("test")
                .lastName("test")
                .experience(0)
                .category("test")
                .cost(0)
                .phoneNumber("test")
                .rating(0)
                .specialization("test")
                .password("test")
                .build();
    }

    private Sick createTestSick() {
        return Sick.builder()
                .surname("test")
                .firstName("test")
                .lastName("test")
                .email("test@mail.ru")
                .disability(0)
                .phoneNumber("test")
                .password("test")
                .build();
    }
}
