package ru.babaev.SpringBootApp.Validators;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.babaev.SpringBootApp.ObjectsToValidate.AppointmentToValidate;
import ru.babaev.SpringBootApp.AppConstants.ApplicationConstants;
import ru.babaev.SpringBootApp.Utils.CustomBindingResult;
import java.io.IOException;

@Component
public class AppointmentCustomValidator {
    public void validate(AppointmentToValidate appointmentToValidate,
                              CustomBindingResult customBindingResult) throws IOException {
        String filesFieldName = "files";
        java.io.File directory = new java.io.File(appointmentToValidate.getPathToUploadDirectory());
        if (appointmentToValidate.getFiles().length > 10) {
            customBindingResult.setError(filesFieldName,"Вы можете прикрепить не более 10 файлов");
        }
        else if (!directory.exists()) {
            customBindingResult.setError(filesFieldName,"Невозможно прикрепить документы");
        }
        else {
            for (MultipartFile file : appointmentToValidate.getFiles()) {
                if (notAllowedExtension(file.getOriginalFilename())) {
                    customBindingResult.setError(filesFieldName,
                            "Расширение '" + FilenameUtils.getExtension(file.getOriginalFilename()) + "' не поддерживается");
                    break;
                }
                if (file.getBytes().length > ApplicationConstants.MAX_FILE_SIZE_BYTES) {
                    customBindingResult.setError(filesFieldName, "Вы не можете загружать файлы, размером больше 3 МБ");
                    break;
                }
            }
        }
    }

    private boolean notAllowedExtension(String fileName) {
        String fileExtension = FilenameUtils.getExtension(fileName);
        return !ApplicationConstants.ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase());
    }
}
