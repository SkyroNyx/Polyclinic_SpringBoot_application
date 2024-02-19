package ru.babaev.SpringBootApp.Controllers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.babaev.SpringBootApp.AppConstants.ApplicationConstants;
import ru.babaev.SpringBootApp.CookieManagers.SickCookieManager;
import ru.babaev.SpringBootApp.Exceptions.NoFreeDatesException;
import ru.babaev.SpringBootApp.Models.Appointment;
import ru.babaev.SpringBootApp.Models.AttachedDocument;
import ru.babaev.SpringBootApp.Models.Prescription;
import ru.babaev.SpringBootApp.Models.Sick;
import ru.babaev.SpringBootApp.ObjectsToValidate.AppointmentToValidate;
import ru.babaev.SpringBootApp.Validators.AppointmentCustomValidator;
import ru.babaev.SpringBootApp.Validators.SickValidator;
import ru.babaev.SpringBootApp.security.Sick.SickDetails;
import ru.babaev.SpringBootApp.Services.SickService;
import ru.babaev.SpringBootApp.Utils.*;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.io.FilenameUtils;

@Controller
@RequestMapping("/sick")
public class SickController {
    private final SickService sickService;
    private final SickCookieManager sickCookieManager;
    private final SickValidator sickValidator;
    private final AppointmentCustomValidator appointmentCustomValidator;
    private final SickJWTUtil sickJWTUtil;

    @Autowired
    public SickController(SickService sickService, SickCookieManager sickCookieManager,
                          SickValidator sickValidator, AppointmentCustomValidator appointmentCustomValidator, SickJWTUtil sickJWTUtil) {
        this.sickService = sickService;
        this.sickCookieManager = sickCookieManager;
        this.sickValidator = sickValidator;
        this.appointmentCustomValidator = appointmentCustomValidator;
        this.sickJWTUtil = sickJWTUtil;
    }

    @GetMapping
    public String personalAccountPage(@AuthenticationPrincipal SickDetails sickDetails,
                                      Model model) {
        Sick sick = sickDetails.getSick();
        model.addAttribute("sick", sick);
        model.addAttribute("appointments",sickService.getCurrentSickAppointments(sick.getId()));
        return "/sick/personal-account";
    }

    @GetMapping("/doctors")
    public String doctorsPage() {
        return "/sick/doctors-list";
    }

    @GetMapping("/about-polyclinic")
    public String aboutPolyclinicPage() {
        return "/sick/about-polyclinic";
    }

    @GetMapping("/update")
    public String updatePage(@AuthenticationPrincipal SickDetails sickDetails,
                             Model model) {
        Sick sick = sickDetails.getSick();
        model.addAttribute("sick", sick);
        return "/sick/edit-data";
    }

    @PatchMapping("/update")
    public String update(@AuthenticationPrincipal SickDetails sickDetails,
                         @ModelAttribute("sick") @Valid Sick sick,
                         BindingResult bindingResult,
                         HttpServletResponse response) {
        Sick sickToUpdate = sickDetails.getSick();
        sick.setId(sickToUpdate.getId());
        sickValidator.validate(sick, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/sick/edit-data";
        }
        sickToUpdate.setEmail(sick.getEmail());
        sickToUpdate.setPhoneNumber(sick.getPhoneNumber());
        Sick sickPrincipal = sickDetails.getSick();
        sickPrincipal.setPhoneNumber(sickToUpdate.getPhoneNumber());
        sickPrincipal.setEmail(sickToUpdate.getEmail());
        String jwtToken = sickJWTUtil.generateToken(sickDetails.getSick());
        Cookie jwtCookie = new Cookie("Authentication", jwtToken);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);
        sickService.updateSick(sickToUpdate);
        return "redirect:/sick";
    }

    @GetMapping("/doctor/{doctorId}")
    public String doctorPage(@AuthenticationPrincipal SickDetails sickDetails,
                             @PathVariable("doctorId") int doctorId,
                             Model model,
                             HttpServletRequest request,
                             @ModelAttribute("customBindingResult") CustomBindingResult customBindingResult) {
        Sick sick = sickDetails.getSick();
        HashMap<String,String> sickDoctorAppointmentsCookies = new HashMap<>();
        boolean haveFreeDates = true;
        String currentDate = Dates.getCurrentDatePlusNDays(ApplicationConstants.MAX_RANGE_OF_DAYS);
        String nearestFreeDate = Dates.getCurrentDatePlusNDays(ApplicationConstants.MAX_RANGE_OF_DAYS);
        try {
            nearestFreeDate = sickService.getNearestFreeDate(doctorId,ApplicationConstants.MAX_RANGE_OF_DAYS);
            sickDoctorAppointmentsCookies = sickCookieManager.getSickDoctorAppointmentsCookie(request, doctorId);
            currentDate = sickDoctorAppointmentsCookies.get("currentDate");
        }
        catch (NoFreeDatesException e) {
            haveFreeDates = false;
        }
        boolean haveAnAppointmentWithCurrentDoctor = sickService.haveAnAppointmentWithCurrentDoctor(sick.getId(),doctorId);
        if (haveAnAppointmentWithCurrentDoctor) {
            Appointment appointment = sickService.getAppointmentWithCurrentDoctor(sick.getId(),doctorId);
            model.addAttribute("dateOfAppointment",new SimpleDateFormat("dd-MM-yyyy").format(appointment.getDate()));
            model.addAttribute("timeOfAppointment",new SimpleDateFormat("HH:mm").format(appointment.getTime()));
        }
        model.addAttribute("doctor", sickService.getDoctorById(doctorId));
        model.addAttribute("haveFreeDates", haveFreeDates);
        model.addAttribute("haveAnAppointment", haveAnAppointmentWithCurrentDoctor);
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("minDate", nearestFreeDate);
        model.addAttribute("maxDate", Dates.getCurrentDatePlusNDays(ApplicationConstants.MAX_RANGE_OF_DAYS));
        model.addAttribute("complaints", sickDoctorAppointmentsCookies.get("complaints"));
        model.addAttribute("customBindingResult", customBindingResult);
        return "/sick/current-doctor";
    }

    @PostMapping("/doctor/{doctorId}/makeAnAppointment")
    public String makeAnAppointment(@AuthenticationPrincipal SickDetails sickDetails,
                                    @PathVariable("doctorId") int doctorId,
                                    HttpServletRequest request,
                                    @RequestParam("appointment-complaints") String appointmentComplaints,
                                    @RequestParam("appointment-documents") MultipartFile[] files,
                                    RedirectAttributes redirectAttributes) throws IOException {
        Sick sick = sickDetails.getSick();
        String[] appointmentDate = request.getParameterValues("appointment-date");
        String[] appointmentTime = request.getParameterValues("appointment-time");
        if (sickService.haveAnAppointmentWithCurrentDoctor(sick.getId(), doctorId)
                || appointmentDate == null || appointmentTime == null) {
            return "redirect:/sick/doctor/{doctorId}";
        }
        Appointment appointment = new Appointment();
        appointment.setSick(sick);
        appointment.setDoctor(sickService.getDoctorById(doctorId));
        appointment.setComplaints(appointmentComplaints);
        appointment.setPast(false);
        try {
            appointment.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(appointmentDate[0]));
            appointment.setTime(new Time(new SimpleDateFormat("HH:mm").parse(appointmentTime[0]).getTime()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (files[0].isEmpty()) {
            sickService.saveAppointment(appointment);
            return "redirect:/sick/doctor/{doctorId}";
        }
        AppointmentToValidate appointmentToValidate =
                convertTo_AppointmentToValidateObject(files, ApplicationConstants.PATH_TO_APPOINTMENTS_FILES);
        CustomBindingResult customBindingResult = new CustomBindingResult();
        appointmentCustomValidator.validate(appointmentToValidate, customBindingResult);
        if (customBindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("customBindingResult", customBindingResult);
            return "redirect:/sick/doctor/{doctorId}";
        }
        List<AttachedDocument> documents = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String fileName = file.getOriginalFilename();
                String uniqueFileName = generateUniqueFileName(fileName, appointmentToValidate.getPathToUploadDirectory());
                String filePath = appointmentToValidate.getPathToUploadDirectory() + uniqueFileName;
                java.io.File dest = new java.io.File(filePath);
                FileCopyUtils.copy(file.getBytes(), dest);
                AttachedDocument attachedDocument = new AttachedDocument();
                attachedDocument.setAppointment(appointment);
                attachedDocument.setName(fileName);
                attachedDocument.setPath(filePath);
                documents.add(attachedDocument);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sickService.saveAppointment(appointment);
        sickService.saveDocuments(documents);
        return "redirect:/sick/doctor/{doctorId}";
    }

    private AppointmentToValidate convertTo_AppointmentToValidateObject(MultipartFile[] files,
                                                                        String pathToUploadDirectory) {
        AppointmentToValidate appointmentToValidate = new AppointmentToValidate();
        appointmentToValidate.setFiles(files);
        appointmentToValidate.setPathToUploadDirectory(pathToUploadDirectory);
        return appointmentToValidate;
    }

    @GetMapping("/prescriptions")
    public String prescriptionsPage(@AuthenticationPrincipal SickDetails sickDetails,
                                    Model model) {
        Sick sick = sickDetails.getSick();
        LinkedHashMap<String, List<Prescription>> stringPrescriptionHashMap = sickService.getDatesPrescriptionsHashMap(sick.getId());
        List<String> dates = new ArrayList<>(stringPrescriptionHashMap.keySet());
        model.addAttribute("dates",dates);
        model.addAttribute("sickPrescriptionsHashMap",stringPrescriptionHashMap);
        return "/sick/prescriptions";
    }

    private String generateUniqueFileName(String originalFileName, String uploadDir) {
        String baseName = FilenameUtils.getBaseName(originalFileName);
        String extension = FilenameUtils.getExtension(originalFileName);
        String uniqueFileName = baseName + "_" + System.currentTimeMillis() + "." + extension;
        java.io.File file = new java.io.File(uploadDir + uniqueFileName);
        int i = 1;
        while (file.exists()) {
            uniqueFileName = baseName + "_" + System.currentTimeMillis() + "_" + i + "." + extension;
            file = new java.io.File(uploadDir + uniqueFileName);
            i++;
        }
        return uniqueFileName;
    }
}
