package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model;

/**
 * Created by gabriel on 2015-11-27.
 */
public class Course {

    private final TypeCourse TYPE_COURSE;
    private double distance;
    private double vitesse;
    private int calories;
    private int pas;

    public Course(final TypeCourse TYPE_COURSE) {
        this.TYPE_COURSE = TYPE_COURSE;
        this.distance = 0;
        this.vitesse = 0;
        this.calories = 0;
        this.pas = 0;
    }

    public Course(final TypeCourse TYPE_COURSE, double distance, double vitesse, int calories, int pas) {
        this.TYPE_COURSE = TYPE_COURSE;
        this.distance = distance;
        this.vitesse = vitesse;
        this.calories = calories;
        this.pas = pas;
    }

    public Course(final TypeCourse TYPE_COURSE, double distance, double vitesse, int calories) {
        this.TYPE_COURSE = TYPE_COURSE;
        this.distance = distance;
        this.vitesse = vitesse;
        this.calories = calories;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
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
