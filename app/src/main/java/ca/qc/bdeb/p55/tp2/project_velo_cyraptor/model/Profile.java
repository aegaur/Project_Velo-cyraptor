package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model;

/**
 * Created by gabriel on 2015-12-04.
 */
public class Profile {

    private static final double POIDS_PAR_DEFAUT = 160;
    private static final double TAILLE_PAR_DEFAUT = 170;
    private static final int AGE_PAR_DEFAUT = 30;

    private double poidsLbs;
    private Sexe sexe;
    private double tailleCm;
    private int age;

    public Profile() {
        poidsLbs = POIDS_PAR_DEFAUT;
        sexe = Sexe.INDETERMINE;
        tailleCm = TAILLE_PAR_DEFAUT;
        age = AGE_PAR_DEFAUT;
    }

    public Profile(double poidsLbs, Sexe sexe, double tailleCm, int age) {
        this.poidsLbs = poidsLbs;
        this.sexe = sexe;
        this.tailleCm = tailleCm;
        this.age = age;
    }

    public double getPoidsLbs() {
        return poidsLbs;
    }

    public void setPoidsLbs(double poidsLbs) {
        this.poidsLbs = poidsLbs;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }

    public double getTailleCm() {
        return tailleCm;
    }

    public void setTailleCm(double tailleCm) {
        this.tailleCm = tailleCm;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
