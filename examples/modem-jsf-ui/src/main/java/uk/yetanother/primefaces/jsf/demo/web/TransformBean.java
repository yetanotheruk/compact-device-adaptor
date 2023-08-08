package uk.yetanother.primefaces.jsf.demo.web;

import jakarta.inject.Named;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@ApplicationScope
@Named
public class TransformBean {
    
    private static final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm:ss");

    public String dateToString(String date) {
        try {
            return date != null ? LocalDateTime.parse(date).format(CUSTOM_FORMATTER) : "";
        } catch (DateTimeParseException var3) {
            return "";
        }
    }
}
