package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by gabriel on 2015-10-21.
 */
public class Date implements Serializable{
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private java.util.Date date;

    public Date() {
        this.date = new java.util.Date();
    }

    public Date(String date) {
        try {
            this.date = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return dateFormat.format(date);
    }
}
