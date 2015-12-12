package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model;

import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;

/**
 * Created by gabriel on 2015-12-04.
 */
public enum Sexe {
    INDETERMINE(0, R.string.activity_profile_spn_sexe_indetermine),
    FEMININ(1, R.string.activity_profile_spn_sexe_masculin),
    MASCULIN(2, R.string.activity_profile_spn_sexe_feminin);

    private final int INDEX;
    private final int ID_STRING;

    Sexe(int INDEX, int ID_STRING) {
        this.INDEX = INDEX;
        this.ID_STRING = ID_STRING;
    }

    public int getINDEX() {
        return INDEX;
    }

    public int getID_STRING() {
        return ID_STRING;
    }

    public static Sexe getSexeByIndex(int index) {
        return values()[index];
    }
}
