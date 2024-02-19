package ru.babaev.SpringBootApp.Services;

import org.springframework.stereotype.Service;
import ru.babaev.SpringBootApp.DAOs.DoctorDAO;
import ru.babaev.SpringBootApp.Exceptions.NoFreeDatesException;
import ru.babaev.SpringBootApp.Models.*;
import ru.babaev.SpringBootApp.Repositories.AppointmentRepository;
import ru.babaev.SpringBootApp.Repositories.AttachedDocumentRepository;
import ru.babaev.SpringBootApp.Repositories.DoctorRepository;
import ru.babaev.SpringBootApp.Repositories.SickRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    private final SickRepository sickRepository;

    private final AppointmentRepository appointmentRepository;

    private final AttachedDocumentRepository attachedDocumentRepository;

    private final DoctorDAO doctorDAO;

    public DoctorService(DoctorRepository doctorRepository, SickRepository sickRepository, AppointmentRepository appointmentRepository,
                         DoctorDAO doctorDAO, AttachedDocumentRepository attachedDocumentRepository) {
        this.doctorRepository = doctorRepository;
        this.sickRepository = sickRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorDAO = doctorDAO;
        this.attachedDocumentRepository = attachedDocumentRepository;
    }

    public Doctor getDoctorById(int doctorId) {
        Optional<Doctor> getDoctor = doctorRepository.findById(doctorId);
        return getDoctor.orElse(null);
    }

    public Sick getSickById(int sickId) {
        Optional<Sick> getSick = sickRepository.findById(sickId);
        return getSick.orElse(null);
    }

    public Appointment getAppointmentById(int appointmentId) {
        Optional<Appointment> getAppointment = appointmentRepository.findById(appointmentId);
        return getAppointment.orElse(null);
    }

    public String getNearestFreeDate(int doctorId, int numOfMaxDays) throws NoFreeDatesException {
        return doctorDAO.getNearestFreeDate(doctorId, numOfMaxDays);
    }

    public List<String> getFreeTimesByDate(String date, int doctorId){
        return doctorDAO.getFreeTimesByDate(date, doctorId);
    }

    public boolean alreadyHaveAppointmentInDateAndTime(int doctorId, String date, String time) {
        return doctorDAO.alreadyHaveAppointmentInDateAndTime(doctorId, date, time);
    }

    public List<Appointment> getAppointmentsByDate(String date, int doctorId) {
        return doctorDAO.getAppointmentsByDate(date, doctorId);
    }

    public AttachedDocument getAttachedDocumentById(int id) {
        return attachedDocumentRepository.getAttachedDocumentById(id);
    }

    public LinkedHashMap<String, List<Prescription>> getDatesPrescriptionsHashMap(int doctorId, String phoneNumber) {
        return doctorDAO.getDatesPrescriptionsHashMap(doctorId, phoneNumber);
    }

    public void savePrescription(Prescription prescription) {
        doctorDAO.savePrescription(prescription);
    }

    public void saveAppointment(Appointment appointment) {
        doctorDAO.saveAppointment(appointment);
    }

    public void savePrescriptionMedicaments(List<PrescriptionsMedicament> medicaments) {
        doctorDAO.savePrescriptionMedicaments(medicaments);
    }

    public void updateAppointment(Appointment appointment) {
        doctorDAO.updateAppointment(appointment);
    }
    public void updateDocuments(List<AttachedDocument> documents) {
        doctorDAO.updateDocuments(documents);
    }

    public void deleteAppointment(Appointment appointment) {
        doctorDAO.deleteAppointment(appointment);
    }
    public void deleteDocuments(List<AttachedDocument> documents) {
        doctorDAO.deleteDocuments(documents);
    }

}
