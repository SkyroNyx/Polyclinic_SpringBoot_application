package ru.babaev.SpringBootApp.AppConstants;

import java.util.Arrays;
import java.util.List;

public class ApplicationConstants {

    public static final int MAX_RANGE_OF_DAYS = 29;

    public static final List<String> ALLOWED_EXTENSIONS =
            Arrays.asList("jpg", "jpeg", "png", "pdf", "txt", "dicom", "docx", "webp", "ppt","doc");

    public static final long MAX_FILE_SIZE_BYTES = 3 * 1024 * 1024;

    public static final String PATH_TO_APPOINTMENTS_FILES = "D:/appointmentDocuments/";
    public static final String JWTSecret = "wjqkfhnksjagvjqnelfhgaslkjnv";
    public static final String JWTIssuer = "PolyclinicSpringBootApplication";
}
