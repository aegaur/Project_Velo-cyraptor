package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.history;

import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;

/**
 * Created by Louis-Simon on 12/12/2015.
 */
public enum HistorySorts {
    DATE("DATE desc", R.string.activity_history_dialog_sort_date),
    DUREE("DUREE", R.string.activity_history_dialog_sort_duree),
    DISTANCE("DISTANCE", R.string.activity_history_dialog_sort_distance),
    TRAJET_ID("TRAJET_ID", R.string.activity_history_dialog_sort_trajet);

    private final String ORDER_BY_STRING;
    private final int DISPLAY_ID;

    HistorySorts(String ORDER_BY_STRING, int DISPLAY_ID) {
        this.ORDER_BY_STRING = ORDER_BY_STRING;
        this.DISPLAY_ID = DISPLAY_ID;
    }

    public String getORDER_BY_STRING() {
        return ORDER_BY_STRING;
    }

    public int getDISPLAY_ID() {
        return DISPLAY_ID;
    }
}
