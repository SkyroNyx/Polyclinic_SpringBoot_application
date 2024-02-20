package ru.babaev.SpringBootApp.RestControllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.babaev.SpringBootApp.Errors.JWTVerificationError;
import ru.babaev.SpringBootApp.DTOs.*;
import ru.babaev.SpringBootApp.Errors.NoFreeDatesError;
import ru.babaev.SpringBootApp.Exceptions.NoFreeDatesException;
import ru.babaev.SpringBootApp.Models.*;
import ru.babaev.SpringBootApp.Services.DoctorService;
import ru.babaev.SpringBootApp.AppConstants.ApplicationConstants;
import ru.babaev.SpringBootApp.Utils.DoctorJWTUtil;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctorRest")
public class DoctorRestController {

    private final DoctorService doctorService;
    private final DoctorJWTUtil doctorJWTUtil;

    @Autowired
    public DoctorRestController(DoctorService doctorService, DoctorJWTUtil doctorJWTUtil) {
        this.doctorService = doctorService;
        this.doctorJWTUtil = doctorJWTUtil;
    }

    @GetMapping("/getFreeTimesListByDate")
    public List<String> getFreeTimesList(@RequestHeader(value = "Authentication", defaultValue = "null") String jwtToken,
                                              @RequestParam("date") String date) {
        Doctor doctor = doctorJWTUtil.validateTokenAndRetrieveDoctor(jwtToken);
        return doctorService.getFreeTimesByDate(date, doctor.getId());
    }

    @GetMapping("/getAppointmentsByDate")
    public List<AppointmentDTO> getAppointmentsByDate(@RequestHeader(value = "Authentication", defaultValue = "null") String jwtToken,
                                                      @RequestParam("date") String date) {
        Doctor doctor = doctorJWTUtil.validateTokenAndRetrieveDoctor(jwtToken);
        return doctorService.getAppointmentsByDate(date, doctor.getId()).stream()
                .map(this::convertToAppointmentDTO).collect(Collectors.toList());
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<byte[]> downloadFile(@RequestHeader(value = "Authentication", defaultValue = "null") String jwtToken,
                                               @RequestParam("fileId") int id) {
        doctorJWTUtil.validateTokenAndRetrieveDoctor(jwtToken);
        try {
            AttachedDocument attachedDocument = doctorService.getAttachedDocumentById(id);
            Path path = Paths.get(attachedDocument.getPath());
            byte[] fileContent = Files.readAllBytes(path);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("inline",
                    URLEncoder.encode(attachedDocument.getName().replaceAll(" ","_"), StandardCharsets.UTF_8));
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/getNearestFreeDate")
    public ResponseEntity<String> getNearestFreeDate(@RequestHeader(value = "Authentication", defaultValue = "null") String jwtToken) {
        Doctor doctor = doctorJWTUtil.validateTokenAndRetrieveDoctor(jwtToken);
        return ResponseEntity.ok(doctorService.getNearestFreeDate(doctor.getId(), ApplicationConstants.MAX_RANGE_OF_DAYS));
    }

    @ExceptionHandler
    private ResponseEntity<NoFreeDatesError> handleNoFreeDatesException(NoFreeDatesException e) {
        NoFreeDatesError response = new NoFreeDatesError(
                System.currentTimeMillis(),
                "doctor with id " + e.getDoctorId() + " doesn't have any free dates"
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getPrescriptions")
    public Map<String, List<PrescriptionDTO>> getPrescriptions(@RequestHeader(value = "Authentication", defaultValue = "null") String jwtToken,
                                                                @RequestParam("phoneNumber") String phoneNumber) {
        Doctor doctor = doctorJWTUtil.validateTokenAndRetrieveDoctor(jwtToken);
        return doctorService.getDatesPrescriptionsHashMap(doctor.getId(), phoneNumber)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(this::convertToPrescriptionDTO)
                                .collect(Collectors.toList()),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    @GetMapping("/alreadyHaveAppointmentInDateAndTime")
    public ResponseEntity<Boolean> alreadyHaveAppointmentInDateAndTime(@RequestHeader(value = "Authentication", defaultValue = "null") String jwtToken,
                                                       @RequestParam("date") String date,
                                                       @RequestParam("time") String time) {
        Doctor doctor = doctorJWTUtil.validateTokenAndRetrieveDoctor(jwtToken);
        return ResponseEntity.ok(doctorService.alreadyHaveAppointmentInDateAndTime(doctor.getId(), date, time));
    }

    @PostMapping("/createPrescription")
    public ResponseEntity<HttpStatus> createPrescription(@RequestHeader(value = "Authentication", defaultValue = "null") String jwtToken,
                                                         @RequestBody CreatePrescriptionDTO createPrescriptionDTO) {
        Appointment appointment = doctorService.getAppointmentById(createPrescriptionDTO.getAppointmentId());
        if (appointment == null) {
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);
        }
        if (appointment.isPast()) {
            return ResponseEntity.ok(HttpStatus.ALREADY_REPORTED);
        }
        appointment.setPast(true);
        Prescription prescription = new Prescription();
        prescription.setAppointment(appointment);
        if (createPrescriptionDTO.getDiagnosis() != null) {
            prescription.setDiagnosis(createPrescriptionDTO.getDiagnosis());
        }
        if (createPrescriptionDTO.getRecommendations() != null) {
            prescription.setRecommendations(createPrescriptionDTO.getRecommendations());
        }
        List<PrescriptionsMedicament> medicaments = new ArrayList<>();
        if (createPrescriptionDTO.getMedicamentList() != null) {
            for (CreatePrescriptionMedDTO prescriptionMed : createPrescriptionDTO.getMedicamentList()) {
                PrescriptionsMedicament prescriptionsMedicament = new PrescriptionsMedicament();
                prescriptionsMedicament.setMedicament(prescriptionMed.getMedicament());
                prescriptionsMedicament.setUse(prescriptionMed.getApplication());
                prescriptionsMedicament.setPrescription(prescription);
                medicaments.add(prescriptionsMedicament);
            }
        }
        if (createPrescriptionDTO.getDateOfNextApp() != null && createPrescriptionDTO.getTimeOfNextApp() != null) {
            Appointment newAppointment = new Appointment();
            newAppointment.setDoctor(prescription.getAppointment().getDoctor());
            newAppointment.setSick(prescription.getAppointment().getSick());
            newAppointment.setComplaints("(повторный прием)");
            try {
                newAppointment.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(createPrescriptionDTO.getDateOfNextApp()));
                newAppointment.setTime(new Time(new SimpleDateFormat("HH:mm").
                        parse(createPrescriptionDTO.getTimeOfNextApp()).getTime()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            newAppointment.setPast(false);
            for (AttachedDocument document : appointment.getAttachedDocumentsList()) {
                document.setAppointment(newAppointment);
            }
            doctorService.saveAppointment(newAppointment);
            doctorService.updateDocuments(appointment.getAttachedDocumentsList());
        }
        else {
            doctorService.deleteDocuments(appointment.getAttachedDocumentsList());
            appointment.setAttachedDocumentsList(null);
        }
        doctorService.updateAppointment(appointment);
        doctorService.savePrescription(prescription);
        doctorService.savePrescriptionMedicaments(medicaments);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<JWTVerificationError> handleJWTVerificationException(JWTVerificationException e) {
        JWTVerificationError response = new JWTVerificationError(
                System.currentTimeMillis(),
                "invalid JWT token"
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    private AppointmentDTO convertToAppointmentDTO(Appointment appointment) {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setId(appointment.getId());
        appointmentDTO.setTime(appointment.getFormatedTime());
        appointmentDTO.setSickID(appointment.getSick().getId());
        appointmentDTO.setSickSurname(appointment.getSick().getSurname());
        appointmentDTO.setSickFirstName(appointment.getSick().getFirstName());
        appointmentDTO.setSickLastName(appointment.getSick().getLastName());
        appointmentDTO.setComplaints(appointment.getComplaints());
        for (AttachedDocument ad : appointment.getAttachedDocumentsList()) {
            AttachedDocumentDTO adDTO = new AttachedDocumentDTO();
            adDTO.setId(ad.getId());
            adDTO.setName(ad.getName());
            appointmentDTO.getDocumentsList().add(adDTO);
        }
        return appointmentDTO;
    }

    private PrescriptionDTO convertToPrescriptionDTO(Prescription prescription) {
        PrescriptionDTO prescriptionDTO = new PrescriptionDTO();
        prescriptionDTO.setTime(prescription.getAppointment().getFormatedTime());
        prescriptionDTO.setSickID(prescription.getAppointment().getSick().getId());
        prescriptionDTO.setSickSurname(prescription.getAppointment().getSick().getSurname());
        prescriptionDTO.setSickFirstName(prescription.getAppointment().getSick().getFirstName());
        prescriptionDTO.setSickLastName(prescription.getAppointment().getSick().getLastName());
        prescriptionDTO.setComplaints(prescription.getAppointment().getComplaints());
        prescriptionDTO.setDiagnosis(prescription.getDiagnosis());
        prescriptionDTO.setRecommendations(prescription.getRecommendations());
        for (PrescriptionsMedicament pm : prescription.getPrescriptionsMedicamentList()) {
            PrescriptionsMedicamentDTO pmDTO = new PrescriptionsMedicamentDTO();
            pmDTO.setName(pm.getMedicament());
            pmDTO.setUse(pm.getUse());
            prescriptionDTO.getPrescriptionsMedicamentList().add(pmDTO);
        }
        return prescriptionDTO;
    }


}
