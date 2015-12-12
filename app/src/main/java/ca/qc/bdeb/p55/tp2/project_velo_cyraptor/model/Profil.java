package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model;

/**
 * Created by gabriel on 2015-12-04.
 */
public class Profil {

    private int poidsLbs;
    private Sexe sexe;
    private int tailleCm;
    private int age;

    public Profil(int poidsLbs, Sexe sexe, int tailleCm, int age) {
        this.poidsLbs = poidsLbs;
        this.sexe = sexe;
        this.tailleCm = tailleCm;
        this.age = age;
    }

    public int getPoidsLbs() {
        return poidsLbs;
    }

    public void setPoidsLbs(int poidsLbs) {
        this.poidsLbs = poidsLbs;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }

    public int getTailleCm() {
        return tailleCm;
    }

    public void setTailleCm(int tailleCm) {
        this.tailleCm = tailleCm;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
