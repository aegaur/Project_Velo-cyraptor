package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model;


import java.io.Serializable;

/**
 * Created by gabriel on 2015-11-27.
 */
public class Statistiques implements Serializable {

    private long dureeTotale;
    private long dureeMoyenne;
    private double distanceTotale;
    private double distanceMoyenne;
    private double vitesseMoyenne;
    private int caloriesTotales;
    private int caloriesMoyennes;
    private int pasTotaux;
    private int pasMoyens;
    private int countTrajet;

    public Statistiques() {
        this.dureeTotale = 0;
        this.dureeMoyenne = 0;
        this.distanceTotale = 0;
        this.distanceMoyenne = 0;
        this.vitesseMoyenne = 0;
        this.caloriesTotales = 0;
        this.caloriesMoyennes = 0;
        this.pasTotaux = 0;
        this.pasMoyens = 0;
        this.countTrajet = 0;
    }

    public int getCountTrajet() {
        return countTrajet;
    }

    public void setCountTrajet(int countTrajet) {
        this.countTrajet = countTrajet;
    }

    public long getDureeTotale() {
        return dureeTotale;
    }

    public void setDureeTotale(long dureeTotale) {
        this.dureeTotale = dureeTotale;
    }

    public long getDureeMoyenne() {
        return dureeMoyenne;
    }

    public void setDureeMoyenne(long dureeMoyenne) {
        this.dureeMoyenne = dureeMoyenne;
    }

    public double getDistanceTotale() {
        return distanceTotale;
    }

    public void setDistanceTotale(double distanceTotale) {
        this.distanceTotale = distanceTotale;
    }

    public double getDistanceMoyenne() {
        return distanceMoyenne;
    }

    public void setDistanceMoyenne(double distanceMoyenne) {
        this.distanceMoyenne = distanceMoyenne;
    }

    public double getVitesseMoyenne() {
        return vitesseMoyenne;
    }

    public void setVitesseMoyenne(double vitesseMoyenne) {
        this.vitesseMoyenne = vitesseMoyenne;
    }

    public int getCaloriesTotales() {
        return caloriesTotales;
    }

    public void setCaloriesTotales(int caloriesTotales) {
        this.caloriesTotales = caloriesTotales;
    }

    public int getCaloriesMoyennes() {
        return caloriesMoyennes;
    }

    public void setCaloriesMoyennes(int caloriesMoyennes) {
        this.caloriesMoyennes = caloriesMoyennes;
    }

    public int getPasTotaux() {
        return pasTotaux;
    }

    public void setPasTotaux(int pasTotaux) {
        this.pasTotaux = pasTotaux;
    }

    public int getPasMoyens() {
        return pasMoyens;
    }

    public void setPasMoyens(int pasMoyens) {
        this.pasMoyens = pasMoyens;
    }
}
