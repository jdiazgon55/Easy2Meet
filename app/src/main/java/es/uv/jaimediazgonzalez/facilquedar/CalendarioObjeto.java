package es.uv.jaimediazgonzalez.facilquedar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jdiazgon on 10/04/2018.
 */

public class CalendarioObjeto {
    private String nombreCalendario;
    private String fechaDesdeString;
    private String fechaHastaString;

    public CalendarioObjeto(String nombreCalendario, String fechaDesdeString, String fechaHastaString) {
        this.nombreCalendario = nombreCalendario;
        this.fechaDesdeString = fechaDesdeString;
        this.fechaHastaString = fechaHastaString;
    }

    public String getNombreCalendario() {
        return nombreCalendario;
    }

    public void setNombreCalendario(String nombreCalendario) {
        this.nombreCalendario = nombreCalendario;
    }

    public String getFechaDesdeString() {
        return fechaDesdeString;
    }

    public void setFechaDesdeString(String fechaDesdeString) {
        this.fechaDesdeString = fechaDesdeString;
    }

    public String getFechaHastaString() {
        return fechaHastaString;
    }

    public void setFechaHastaString(String fechaHastaString) {
        this.fechaHastaString = fechaHastaString;
    }

    public Date getFechaDesdeDate(){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return format.parse(fechaDesdeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getFechaHastaDate(){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return format.parse(fechaHastaString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
