package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model;

import java.util.LinkedList;

/**
 * Created by gabriel on 2015-12-09.
 */
public class Trajet {
    private int id;
    private String nom;
    private double distance;
    private int meilleurTemps;
    private LinkedList<PointCourse> listePoints;

    public Trajet(int id, String nom, double distance, int meilleurTemps, LinkedList<PointCourse> listePoints) {
        this.id = id;
        this.nom = nom;
        this.distance = distance;
        this.meilleurTemps = meilleurTemps;
        this.listePoints = listePoints;
    }

    public Trajet(String nom, double distance, int meilleurTemps) {
        this.nom = nom;
        this.distance = distance;
        this.meilleurTemps = meilleurTemps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getMeilleurTemps() {
        return meilleurTemps;
    }

    public void setMeilleurTemps(int meilleurTemps) {
        this.meilleurTemps = meilleurTemps;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public LinkedList<PointCourse> getListePoints() {
        return listePoints;
    }

    public void setListePoints(LinkedList<PointCourse> listePoints) {
        this.listePoints = listePoints;
    }
}
