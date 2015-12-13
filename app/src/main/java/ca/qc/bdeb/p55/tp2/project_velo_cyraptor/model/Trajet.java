package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by gabriel on 2015-12-09.
 */
public class Trajet implements Serializable {
    private long id;
    private String nom;
    private double distance;
    private long meilleurTemps;
    /**
     * Liste des points parcourue pendant le trajet
     */
    private LinkedList<PointCourse> listePoints;

    public Trajet(int id, String nom, double distance, long meilleurTemps, LinkedList<PointCourse> listePoints) {
        this.id = id;
        this.nom = nom;
        this.distance = distance;
        this.meilleurTemps = meilleurTemps;
        this.listePoints = listePoints;
    }

    public Trajet(String nom, double distance, long meilleurTemps, LinkedList<PointCourse> listePoints) {
        this.nom = nom;
        this.distance = distance;
        this.meilleurTemps = meilleurTemps;
        this.listePoints = listePoints;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public long getMeilleurTemps() {
        return meilleurTemps;
    }

    public void setMeilleurTemps(long meilleurTemps) {
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
