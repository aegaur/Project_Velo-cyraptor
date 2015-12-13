package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.history;

/**
 * Created by Louis-Simon on 12/12/2015.
 */
public enum HistorySorts {
    DUREE("DUREE", display_string),
    TRAJET_ID("TRAJET_ID", display_string),
    DATE("DATE desc", display_string),
    DISTANCE("DISTANCE", display_string);

    private final String ORDER_BY_STRING;
    private final int DISPLAY_STRING;

    HistorySorts(String ORDER_BY_STRING, int DISPLAY_STRING) {
        this.ORDER_BY_STRING = ORDER_BY_STRING;
        this.DISPLAY_STRING = DISPLAY_STRING;
    }

    public String getORDER_BY_STRING() {
        return ORDER_BY_STRING;
    }

    public int getDISPLAY_STRING() {
        return DISPLAY_STRING;
    }
}
