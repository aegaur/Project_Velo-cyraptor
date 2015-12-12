package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.history;

/**
 * Created by Louis-Simon on 12/12/2015.
 */
public enum HistorySorts {
    DUREE("DUREE"),
    TRAJET_ID("TRAJET_ID"),
    DATE("DATE desc"),
    DISTANCE("DISTANCE");

    private final String ORDER_BY_STRING;

    HistorySorts(String ORDER_BY_STRING) {
        this.ORDER_BY_STRING = ORDER_BY_STRING;
    }

    public String getORDER_BY_STRING() {
        return ORDER_BY_STRING;
    }
}
