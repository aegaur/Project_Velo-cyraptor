package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model;

/**
 * Created by gabriel on 2015-12-04.
 */
public enum Sexe {
    MASUCULIN(0),
    FEMININ(1),
    INDETERMINE(2);

    private final int INDEX;

    Sexe(int INDEX) {
        this.INDEX = INDEX;
    }

    public int getINDEX() {
        return INDEX;
    }

    public static Sexe getSexeByIndex(int index){
        return values()[index];
    }
}
