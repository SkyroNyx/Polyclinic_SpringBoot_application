package ru.babaev.SpringBootApp.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.babaev.SpringBootApp.DAOs.DoctorDAO;
import ru.babaev.SpringBootApp.DAOs.SickDAO;
import ru.babaev.SpringBootApp.Exceptions.NoFreeDatesException;
import ru.babaev.SpringBootApp.Models.*;
import ru.babaev.SpringBootApp.Repositories.DoctorRepository;
import ru.babaev.SpringBootApp.Repositories.SickRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
public class SickService {
    private final SickDAO sickDAO;
    private final DoctorDAO doctorDAO;
    private final SickRepository sickRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public SickService(SickRepository sickRepository,SickDAO sickDAO,DoctorDAO doctorDAO,DoctorRepository doctorRepository) {
        this.sickRepository = sickRepository;
        this.sickDAO = sickDAO;
        this.doctorDAO = doctorDAO;
        this.doctorRepository = doctorRepository;
    }

    public Sick getSickById(int sickId){
        Optional<Sick> sickOptional = sickRepository.findById(sickId);
        return sickOptional.orElse(null);
    }

    public Doctor getDoctorById(int doctorId){
        Optional<Doctor> doctorOptional = doctorRepository.findById(doctorId);
        return doctorOptional.orElse(null);
    }

    public List<Appointment> getCurrentSickAppointments(int sickId){
        return sickDAO.currentSickAppointments(sickId);
    }

    public List<Doctor> getFilteredDoctorsList(String[] specialisations,String[] categories,
                                               String experienceSign, String experienceNumber) {
        return sickDAO.getFilteredDoctorsList(specialisations,categories,experienceSign,experienceNumber);
    }

    public boolean haveAnAppointmentWithCurrentDoctor(int sickId, int doctorId) {
        return sickDAO.haveAnAppointmentWithCurrentDoctor(sickId,doctorId);
    }

    public Appointment getAppointmentWithCurrentDoctor(int sickId, int doctorId) {
        return sickDAO.getAppointmentWithCurrentDoctor(sickId,doctorId);
    }

    public String getNearestFreeDate(int doctorId, int numOfMaxDays) throws NoFreeDatesException {
        return doctorDAO.getNearestFreeDate(doctorId, numOfMaxDays);
    }

    public LinkedHashMap<String, List<Prescription>> getDatesPrescriptionsHashMap(int sickId) {
        return sickDAO.getDatesPrescriptionsHashMap(sickId);
    }

    public void saveAppointment(Appointment appointment) {
        sickDAO.saveAppointment(appointment);
    }

    public void saveDocuments(List<AttachedDocument> documents) {
        sickDAO.saveDocuments(documents);
    }

    public void updateSick(Sick sick) {
        sickDAO.updateSick(sick);
    }

    public Optional<Sick> getSickByEmail(String email) {
        return sickRepository.getSickByEmail(email);
    }

    public Optional<Sick> getSickByPhoneNumber(String phoneNumber) {
        return sickRepository.getSickByPhoneNumber(phoneNumber);
    }
}
