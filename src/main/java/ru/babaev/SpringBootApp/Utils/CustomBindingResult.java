package ru.babaev.SpringBootApp.Utils;

import java.util.HashMap;

public class CustomBindingResult {
    private HashMap<String, String> fieldsErrors = new HashMap<>();

    public boolean hasErrors() {
        return !fieldsErrors.keySet().isEmpty();
    }

    public void setError(String field, String errorText) {
        fieldsErrors.put(field, errorText);
    }

    public String getErrorByField(String field) {
        return fieldsErrors.get(field);
    }

}
