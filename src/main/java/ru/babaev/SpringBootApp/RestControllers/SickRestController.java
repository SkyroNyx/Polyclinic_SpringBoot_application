package ru.babaev.SpringBootApp.RestControllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.babaev.SpringBootApp.Errors.JWTVerificationError;
import ru.babaev.SpringBootApp.DTOs.DoctorDTO;
import ru.babaev.SpringBootApp.Models.Doctor;
import ru.babaev.SpringBootApp.Services.DoctorService;
import ru.babaev.SpringBootApp.Services.SickService;
import ru.babaev.SpringBootApp.Utils.SickJWTUtil;
import java.util.List;

@RestController
@RequestMapping("/sickRest")
public class SickRestController {

    private final SickService sickService;
    private final DoctorService doctorService;
    private final ModelMapper modelMapper;
    private final SickJWTUtil sickJWTUtil;

    @Autowired
    public SickRestController(SickService sickService, DoctorService doctorService, ModelMapper modelMapper, SickJWTUtil sickJWTUtil) {
        this.sickService = sickService;
        this.doctorService = doctorService;
        this.modelMapper = modelMapper;
        this.sickJWTUtil = sickJWTUtil;
    }

    @GetMapping ("/getFilteredDoctorsList")
    public List<DoctorDTO> getFilteredDoctorsList(@RequestHeader(value = "Authentication", defaultValue = "null") String jwtToken,
                                                  @RequestParam("specialisations") String[] specialisations,
                                                  @RequestParam("categories") String[] categories,
                                                  @RequestParam("experienceSign") String experienceSign,
                                                  @RequestParam("experienceNumber") String experienceNumber) {
        sickJWTUtil.validateTokenAndRetrieveSick(jwtToken);
        return sickService.getFilteredDoctorsList(specialisations,categories,experienceSign,experienceNumber)
                .stream().map(this::convertToDoctorDTO).toList();
    }

    @ExceptionHandler
    private ResponseEntity<JWTVerificationError> handleJWTVerificationException(JWTVerificationException e) {
        JWTVerificationError response = new JWTVerificationError(
                System.currentTimeMillis(),
                "invalid JWT token"
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/doctorAlreadyHaveAppointmentInDateAndTime")
    public ResponseEntity<Boolean> alreadyHaveAppointmentInDateAndTime(@RequestHeader(value = "Authentication", defaultValue = "null") String jwtToken,
                                                                       @RequestParam("doctorId") int doctorId,
                                                                       @RequestParam("date") String date,
                                                                       @RequestParam("time") String time) {
        sickJWTUtil.validateTokenAndRetrieveSick(jwtToken);
        return ResponseEntity.ok(doctorService.alreadyHaveAppointmentInDateAndTime(doctorId, date, time));
    }

    @GetMapping("/getDoctorsFreeTimesListByDate")
    public List<String> getFreeTimesList(@RequestHeader(value = "Authentication", defaultValue = "null") String jwtToken,
                                         @RequestParam("date") String date,
                                         @RequestParam("doctorId") int doctorId) {
        sickJWTUtil.validateTokenAndRetrieveSick(jwtToken);
        return doctorService.getFreeTimesByDate(date, doctorId);
    }

    private DoctorDTO convertToDoctorDTO(Doctor doctor) {
        return modelMapper.map(doctor, DoctorDTO.class);
    }

}
