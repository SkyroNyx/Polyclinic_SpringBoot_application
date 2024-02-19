package ru.babaev.SpringBootApp.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.babaev.SpringBootApp.Exceptions.NoFreeDatesException;
import ru.babaev.SpringBootApp.Models.*;
import ru.babaev.SpringBootApp.security.Doctor.DoctorDetails;
import ru.babaev.SpringBootApp.Services.DoctorService;
import ru.babaev.SpringBootApp.AppConstants.ApplicationConstants;
import ru.babaev.SpringBootApp.Utils.Dates;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorController {
    private final DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public String personalAccountPage(@AuthenticationPrincipal DoctorDetails doctorDetails,
                                      Model model,
                                      HttpServletRequest request) {
        Doctor doctor = doctorDetails.getDoctor();
        String currentDate = getPeekedDateInPersonalAccountCookie(request);
        model.addAttribute("doctor", doctor);
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("maxDate", Dates.getCurrentDatePlusNDays(ApplicationConstants.MAX_RANGE_OF_DAYS));
        return "/doctor/personal-account";
    }

    @GetMapping("/prescriptions")
    public String prescriptionsPage() {
        return "/doctor/prescriptions";
    }

    @GetMapping("/sick/{sickId}")
    public String sickPage(@PathVariable("sickId") int sickId,
                           Model model) {
        model.addAttribute("sick", doctorService.getSickById(sickId));
        return "/doctor/current-sick";
    }

    @DeleteMapping("/cancelAppointment")
    public String cancelAppointment(@RequestParam("appointmentId") int appointmentId) {
        Appointment appointment = doctorService.getAppointmentById(appointmentId);
        if (appointment != null && !appointment.isPast()) {
            doctorService.deleteAppointment(appointment);
        }
        return "redirect:/doctor";
    }

    @GetMapping("/rescheduleAppointment")
    public String rescheduleAppointmentPage(@AuthenticationPrincipal DoctorDetails doctorDetails,
                                            @RequestParam("appointmentId") int appointmentId,
                                            Model model) {
        Doctor doctor = doctorDetails.getDoctor();
        Appointment appointment = doctorService.getAppointmentById(appointmentId);
        if (appointment == null || appointment.isPast()) {
            return "redirect:/doctor";
        }
        boolean haveFreeDates = true;
        String nearestFreeDate = Dates.getCurrentDatePlusNDays(ApplicationConstants.MAX_RANGE_OF_DAYS);
        try {
            nearestFreeDate = doctorService.getNearestFreeDate(doctor.getId(), ApplicationConstants.MAX_RANGE_OF_DAYS);
        }
        catch (NoFreeDatesException e) {
            haveFreeDates = false;
        }
        model.addAttribute("appointment", appointment);
        model.addAttribute("haveFreeDates", haveFreeDates);
        model.addAttribute("nearestFreeDate", nearestFreeDate);
        model.addAttribute("maxDate", Dates.getCurrentDatePlusNDays(ApplicationConstants.MAX_RANGE_OF_DAYS));
        return "/doctor/rescheduling";
    }

    @PatchMapping("/rescheduleAppointment")
    public String rescheduleAppointment(@RequestParam("appointmentId") String appointmentId,
                                        HttpServletRequest request) {
        Appointment appointment = doctorService.getAppointmentById(Integer.parseInt(appointmentId));
        String[] dateToReschedule = request.getParameterValues("appointment-date");
        String[] timeToReschedule = request.getParameterValues("appointment-time");
        if (appointment == null || appointment.isPast() || dateToReschedule == null || timeToReschedule == null) {
            return "redirect:/doctor";
        }
        try {
            appointment.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateToReschedule[0]));
            appointment.setTime(new Time(new SimpleDateFormat("HH:mm").parse(timeToReschedule[0]).getTime()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        doctorService.updateAppointment(appointment);
        return "redirect:/doctor";
    }

    @GetMapping("/createPrescription")
    public String createPrescriptionPage(@AuthenticationPrincipal DoctorDetails doctorDetails,
                                         Model model,
                                         @RequestParam("appointmentId") int appointmentId) {
        Doctor doctor = doctorDetails.getDoctor();
        Appointment appointment = doctorService.getAppointmentById(appointmentId);
        if (appointment == null || appointment.isPast()) {
            return "redirect:/doctor";
        }
        boolean haveFreeDates = true;
        String nearestFreeDate = "2023-10-23";
        try {
             nearestFreeDate = doctorService.getNearestFreeDate(doctor.getId(), ApplicationConstants.MAX_RANGE_OF_DAYS);
        }
        catch (NoFreeDatesException e) {
            haveFreeDates = false;
        }
        model.addAttribute("appointment", doctorService.getAppointmentById(appointmentId));
        model.addAttribute("haveFreeDates", haveFreeDates);
        model.addAttribute("nearestFreeDate", nearestFreeDate);
        model.addAttribute("maxDate", Dates.getCurrentDatePlusNDays(ApplicationConstants.MAX_RANGE_OF_DAYS));
        return "/doctor/create-prescription";
    }

    @PostMapping("/createPrescription")
    public String createPrescription(@RequestParam("appointmentId") String appointmentId,
                                     @RequestParam("appointment-diagnosis") String diagnosis,
                                     @RequestParam("appointment-recommendations") String recommendations,
                                     @RequestParam(name = "repeat-appointment", required = false) Boolean repeatAppointment,
                                     HttpServletRequest request) {
        Appointment prescriptionsAppointment = doctorService.getAppointmentById(Integer.parseInt(appointmentId));
        if (prescriptionsAppointment == null || prescriptionsAppointment.isPast()) {
            return "redirect:/doctor";
        }
        prescriptionsAppointment.setPast(true);
        Prescription prescription = new Prescription();
        prescription.setAppointment(prescriptionsAppointment);
        prescription.setDiagnosis(diagnosis);
        prescription.setRecommendations(recommendations);
        List<PrescriptionsMedicament> medicaments = new ArrayList<>();
        int medIdCounter = 0;
        String[] medicament = request.getParameterValues("medicament" + medIdCounter);
        String[] application = request.getParameterValues("application" + medIdCounter);
        while (medicament != null && application != null) {
            PrescriptionsMedicament prescriptionsMedicament = new PrescriptionsMedicament();
            prescriptionsMedicament.setPrescription(prescription);
            prescriptionsMedicament.setMedicament(medicament[0]);
            prescriptionsMedicament.setUse(application[0]);
            medicaments.add(prescriptionsMedicament);
            medIdCounter++;
            medicament = request.getParameterValues("medicament" + medIdCounter);
            application = request.getParameterValues("application" + medIdCounter);
        }
        String[] dateArr = request.getParameterValues("appointment-date");
        String[] timeArr = request.getParameterValues("appointment-time");
        if (Boolean.TRUE.equals(repeatAppointment) && dateArr != null && timeArr != null) {
            Appointment newAppointment = new Appointment();
            newAppointment.setDoctor(prescriptionsAppointment.getDoctor());
            newAppointment.setSick(prescriptionsAppointment.getSick());
            newAppointment.setComplaints("(повторный прием)");
            try {
                newAppointment.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateArr[0]));
                newAppointment.setTime(new Time(new SimpleDateFormat("HH:mm").parse(timeArr[0]).getTime()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            newAppointment.setPast(false);
            for (AttachedDocument document : prescriptionsAppointment.getAttachedDocumentsList()) {
                document.setAppointment(newAppointment);
            }
            doctorService.saveAppointment(newAppointment);
            doctorService.updateDocuments(prescriptionsAppointment.getAttachedDocumentsList());
        }
        else {
            doctorService.deleteDocuments(prescriptionsAppointment.getAttachedDocumentsList());
            prescriptionsAppointment.setAttachedDocumentsList(null);
        }
        doctorService.updateAppointment(prescriptionsAppointment);
        doctorService.savePrescription(prescription);
        doctorService.savePrescriptionMedicaments(medicaments);
        return "redirect:/doctor";
    }

    private String getPeekedDateInPersonalAccountCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String date = "";
        boolean dateCookieNotFound = true;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("peekedDateInPersonalAccount")){
                date = cookie.getValue();
                dateCookieNotFound = false;
                break;
            }
        }
        if (dateCookieNotFound) {
            date = Dates.getCurrentDate();
        }
        return date;
    }

    private String getAuthenticationCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Authentication"))
                return cookie.getValue();
        }
        return null;
    }
}
