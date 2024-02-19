package ru.babaev.SpringBootApp.CookieManagers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.babaev.SpringBootApp.Exceptions.NoFreeDatesException;
import ru.babaev.SpringBootApp.Services.SickService;
import ru.babaev.SpringBootApp.AppConstants.ApplicationConstants;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Component
public class SickCookieManager {

    private final SickService sickService;

    @Autowired
    public SickCookieManager(SickService sickService) {
        this.sickService = sickService;
    }
    public HashMap<String, String> getSickDoctorAppointmentsCookie(HttpServletRequest request,
                                                                   int doctorId) throws NoFreeDatesException{
        HashMap<String,String> cookieHashMap = new HashMap<>();
        cookieHashMap.put("currentDate", sickService.getNearestFreeDate(doctorId, ApplicationConstants.MAX_RANGE_OF_DAYS));
        cookieHashMap.put("complaints","");
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
                if (cookie.getName().equals("doctorId" + doctorId + "date")) {
                    if (!isFirstDateBeforeSecondDate(cookie.getValue(), cookieHashMap.get("currentDate"))) {
                        cookieHashMap.put("currentDate", cookie.getValue());
                    }
                }
                else if (cookie.getName().equals("doctorId" + doctorId + "complaints")) {
                    cookieHashMap.put("complaints", cookie.getValue());
                }
        }
        return cookieHashMap;
    }

    private boolean isFirstDateBeforeSecondDate(String dateStr1,
                                                String dateStr2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = dateFormat.parse(dateStr1);
            Date date2 = dateFormat.parse(dateStr2);
            return date1.before(date2);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


}
