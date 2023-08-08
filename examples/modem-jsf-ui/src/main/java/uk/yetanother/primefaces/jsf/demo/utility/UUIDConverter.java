package uk.yetanother.primefaces.jsf.demo.utility;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import java.util.UUID;

@FacesConverter("uk.yetanother.primefaces.jsf.demo.utility.UUIDConverter")
public class UUIDConverter implements Converter<UUID> {

    public UUID getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return s != null && !s.isEmpty() ? UUID.fromString(s) : null;
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, UUID uuid) {
        return uuid == null ? "" : uuid.toString();
    }
}
