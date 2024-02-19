package ru.babaev.SpringBootApp.ObjectsToValidate;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AppointmentToValidate {

    private MultipartFile[] files;

    private String pathToUploadDirectory;
}
