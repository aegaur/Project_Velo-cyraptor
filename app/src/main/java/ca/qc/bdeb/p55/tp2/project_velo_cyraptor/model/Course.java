package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model;


import java.io.Serializable;

/**
 * Created by gabriel on 2015-11-27.
 */
public class Course implements Serializable {

    private int id;
    private Trajet trajet;
    private final TypeCourse TYPE_COURSE;
    private final Date DATE;
    private long duree;
    private double distance;
    private double vitesse;
    private int calories;
    private int pas;

    public Course(final TypeCourse TYPE_COURSE) {
        this.trajet = null;
        this.TYPE_COURSE = TYPE_COURSE;
        this.DATE = new Date();
        this.distance = 0;
        this.vitesse = 0;
        this.calories = 0;
        this.pas = -1;
    }

    public Course(int id, Trajet trajet, TypeCourse TYPE_COURSE, Date DATE, int duree, double distance, double vitesse, int calories, int pas) {
        this.id = id;
        this.trajet = trajet;
        this.TYPE_COURSE = TYPE_COURSE;
        this.DATE = DATE;
        this.duree = duree;
        this.distance = distance;
        this.vitesse = vitesse;
        this.calories = calories;
        this.pas = pas;
    }

    public Course(int id, Trajet trajet, TypeCourse TYPE_COURSE, Date DATE, int duree, double distance, double vitesse, int calories) {
        this.id = id;
        this.trajet = trajet;
        this.TYPE_COURSE = TYPE_COURSE;
        this.DATE = DATE;
        this.duree = duree;
        this.distance = distance;
        this.vitesse = vitesse;
        this.calories = calories;
    }

    public Course(TypeCourse typeCourse, Trajet trajet) {
        this(typeCourse);
        this.trajet = trajet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Trajet getTrajet() {
        return trajet;
    }

    public void setTrajet(Trajet trajet) {
        this.trajet = trajet;
    }

    public TypeCourse getTYPE_COURSE() {
        return TYPE_COURSE;
    }

    public Date getDATE() {
        return DATE;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getDuree() {
        return duree;
    }

    public void setDuree(long duree) {
        this.duree = duree;
    }

    public double getVitesse() {
        return vitesse;
    }

    public void setVitesse(double vitesse) {
        this.vitesse = vitesse;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getPas() {
        return pas;
    }

    public void setPas(int pas) {
        this.pas = pas;
    }
}
