package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.*;

import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.history.HistorySorts;

/**
 * Created by gabriel on 2015-12-09.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "app.db"; // Votre nom de BD
    public static final int DBVERSION = 1; // Votre numéro de version de BD
    private Context context;
    private static DbHelper instance = null; //L’unique instance de DbHelper possible
    // Mettre ici toutes les constantes de noms de tables et de colonnes
    private static final String TABLE_COURSE = "Course";
    private static final String TABLE_TRAJET = "Trajet";
    private static final String TABLE_POINT = "Point";
    private static final String TABLE_PROFIL = "Profil";
    // Colonnes Course
    private static final String COURSE_ID = "_id";
    private static final String COURSE_TRAJET_ID = "trajet_id";
    private static final String COURSE_TYPE = "type";
    private static final String COURSE_DATE = "date";
    private static final String COURSE_DISTANCE = "distance";
    private static final String COURSE_DUREE = "duree";
    private static final String COURSE_VITESSE = "vitesse";
    private static final String COURSE_CALORIES = "calories";
    private static final String COURSE_PAS = "pas";
    // Colonnes Trajet
    private static final String TRAJET_ID = "_id";
    private static final String TRAJET_NOM = "nom";
    private static final String TRAJET_DISTANCE = "distance";
    private static final String TRAJET_MEILLEUR_TEMPS = "meilleur_temps";
    // Colonnes Point
    private static final String POINT_ID = "_id";
    private static final String POINT_TRAJET_ID = "trajet_id";
    private static final String POINT_LONGITUDE = "longitude";
    private static final String POINT_LATITUDE = "latitude";
    private static final String POINT_DISTANCE = "distance";
    // Colonnes Profil
    private static final String PROFIL_ID = "_id";
    private static final String PROFIL_KEY = "key";
    private static final String PROFIL_VALUE = "value";
    // Profil keys
    private static final String PROFIL_KEY_POIDS = "poids";
    private static final String PROFIL_KEY_TAILLE = "taille";
    private static final String PROFIL_KEY_SEXE = "sexe";
    private static final String PROFIL_KEY_AGE = "age";
    // Profil Defaut
    private static final double POIDS_PAR_DEFAUT = 160;
    private static final double TAILLE_PAR_DEFAUT = 170;
    private static final int AGE_PAR_DEFAUT = 30;
    private static final int SEXE_PAR_DEFAUT = Sexe.INDETERMINE.getINDEX();
    // Statistiques ids
    private static final String STATS_CALORIES_AVG = "CALORIES_AVG";
    private static final String STATS_CALORIES_SUM = "CALORIES_SUM";
    private static final String STATS_PAS_AVG = "PAS_AVG";
    private static final String STATS_PAS_SUM = "PAS_SUM";
    private static final String STATS_VITESSE_AVG = "VITESSE_AVG";
    private static final String STATS_DISTANCE_AVG = "DISTANCE_AVG";
    private static final String STATS_DISTANCE_SUM = "DISTANCE_SUM";
    private static final String STATS_DUREE_AVG = "DUREE_AVG";
    private static final String STATS_DUREE_SUM = "DUREE_SUM";
    private static final String STATS_COUNT_TRAJET = "COUNT_TRAJET";
    // La requête pour obtenir toutes les statistques
    private static final String REQUETE_STATISTIQUES =
            "select sum(" + COURSE_CALORIES + ") as " + STATS_CALORIES_SUM +
                    ", avg(" + COURSE_CALORIES + ") as " + STATS_CALORIES_AVG +
                    ", sum(" + COURSE_PAS + ") as " + STATS_PAS_SUM +
                    ", avg(" + COURSE_PAS + ") as " + STATS_PAS_AVG +
                    ", avg(" + COURSE_VITESSE + ") as " + STATS_VITESSE_AVG +
                    ", sum(" + COURSE_DISTANCE + ") as " + STATS_DISTANCE_SUM +
                    ", avg(" + COURSE_DISTANCE + ") as " + STATS_DISTANCE_AVG +
                    ", sum(" + COURSE_DUREE + ") as " + STATS_DUREE_SUM +
                    ", avg(" + COURSE_DUREE + ") as " + STATS_DUREE_AVG +
                    " from " + TABLE_COURSE + " where " + COURSE_TYPE + "= ?";
    // La requête pour obtenir toutes les statistiques associées à un trajet
    private static final String REQUETE_STATISTIQUES_TRAJET =
            "select sum(" + COURSE_CALORIES + ") as " + STATS_CALORIES_SUM +
                    ", avg(" + COURSE_CALORIES + ") as " + STATS_CALORIES_AVG +
                    ", avg(" + COURSE_VITESSE + ") as " + STATS_VITESSE_AVG +
                    ", sum(" + COURSE_DUREE + ") as " + STATS_DUREE_SUM +
                    ", avg(" + COURSE_DUREE + ") as " + STATS_DUREE_AVG +
                    ", count(" + COURSE_ID + ") as " + STATS_COUNT_TRAJET +
                    " from " + TABLE_COURSE + " where " + COURSE_TRAJET_ID + " = ?";
    // Script de création de la table course
    private static final String CREATE_TABLE_COURSE = "CREATE TABLE " + TABLE_COURSE + "("
            + COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COURSE_TRAJET_ID + " INTEGER NULL,"
            + COURSE_TYPE + " TEXT NOT NULL,"
            + COURSE_DATE + " TEXT NOT NULL,"
            + COURSE_DISTANCE + " REAL NOT NULL,"
            + COURSE_DUREE + " INTEGER NOT NULL,"
            + COURSE_VITESSE + " REAL NOT NULL,"
            + COURSE_CALORIES + " INTEGER NOT NULL,"
            + COURSE_PAS + " INTEGER NOT NULL," +
            "FOREIGN KEY (" + COURSE_TRAJET_ID + ") REFERENCES " + TABLE_TRAJET + "(" + TRAJET_ID + ")" +
            ")";
    // Script de création de la table trajet
    private static final String CREATE_TABLE_TRAJET = "CREATE TABLE " + TABLE_TRAJET + "("
            + TRAJET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TRAJET_NOM + " TEXT NOT NULL,"
            + TRAJET_DISTANCE + " REAL NOT NULL,"
            + TRAJET_MEILLEUR_TEMPS + " INTEGER NOT NULL)";
    // Script de création de la table point
    private static final String CREATE_TABLE_POINT = "CREATE TABLE " + TABLE_POINT + "("
            + POINT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + POINT_TRAJET_ID + " INTEGER NOT NULL,"
            + POINT_LONGITUDE + " REAL NOT NULL,"
            + POINT_LATITUDE + " REAL NOT NULL,"
            + POINT_DISTANCE + " REAL NOT NULL," +
            "FOREIGN KEY (" + POINT_TRAJET_ID + ") REFERENCES " + TABLE_TRAJET + "(" + TRAJET_ID + ")" +
            ")";
    /**
     * Script de création de la table point.
     * Elle est batie sur le modèle d'une HashMap : une clée et sa valeur.
     */
    private static final String CREATE_TABLE_PROFIL = "CREATE TABLE " + TABLE_PROFIL + "("
            + PROFIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PROFIL_KEY + " TEXT NOT NULL,"
            + PROFIL_VALUE + " INTEGER NOT NULL)";

    public static DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Constructeur de DBHelper
     */
    private DbHelper(Context context) {
        super(context, DB_NAME, null, DBVERSION);
        this.context = context;
    }

    /**
     * Appeler lors de l’appel « normal » de la création de BD
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRAJET);
        db.execSQL(CREATE_TABLE_COURSE);
        db.execSQL(CREATE_TABLE_POINT);
        db.execSQL(CREATE_TABLE_PROFIL);

        insererProfilParDefaut(db);
    }

    /**
     * Insère les valeurs du profil par défaut
     */
    private void insererProfilParDefaut(SQLiteDatabase db){
        ContentValues profilPoids = new ContentValues();
        profilPoids.put(PROFIL_KEY, PROFIL_KEY_POIDS);
        profilPoids.put(PROFIL_VALUE, POIDS_PAR_DEFAUT);

        ContentValues profilTaille = new ContentValues();
        profilTaille.put(PROFIL_KEY, PROFIL_KEY_TAILLE);
        profilTaille.put(PROFIL_VALUE, TAILLE_PAR_DEFAUT);

        ContentValues profilAge = new ContentValues();
        profilAge.put(PROFIL_KEY, PROFIL_KEY_AGE);
        profilAge.put(PROFIL_VALUE, AGE_PAR_DEFAUT);

        ContentValues profilSexe = new ContentValues();
        profilSexe.put(PROFIL_KEY, PROFIL_KEY_SEXE);
        profilSexe.put(PROFIL_VALUE, SEXE_PAR_DEFAUT);

        db.insert(TABLE_PROFIL, null, profilPoids);
        db.insert(TABLE_PROFIL, null, profilTaille);
        db.insert(TABLE_PROFIL, null, profilAge);
        db.insert(TABLE_PROFIL, null, profilSexe);
    }

    /**
     * Appeler lors d’un changement de version de notre BD
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Retourne le profil
     */
    public Profil getProfil() {
        SQLiteDatabase db = this.getReadableDatabase();
        Profil profil = null;
        HashMap<String, Integer> mapProfil = new HashMap<>();
        Cursor cursor = db.query(TABLE_PROFIL, null, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                mapProfil.put(cursor.getString(cursor.getColumnIndex(PROFIL_KEY)),
                        new Integer(cursor.getString(cursor.getColumnIndex(PROFIL_VALUE))));
            } while (cursor.moveToNext());

            profil = new Profil(
                    mapProfil.get(PROFIL_KEY_POIDS),
                    Sexe.getSexeByIndex(mapProfil.get(PROFIL_KEY_SEXE)),
                    mapProfil.get(PROFIL_KEY_TAILLE),
                    mapProfil.get(PROFIL_KEY_AGE));
        }
        return profil;
    }

    /**
     * Mets à jour les valeurs du profil
     */
    public boolean updateProfil(Profil profil) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success;

        ContentValues profilPoids = new ContentValues();
        profilPoids.put(PROFIL_KEY, PROFIL_KEY_POIDS);
        profilPoids.put(PROFIL_VALUE, profil.getPoidsLbs());

        ContentValues profilTaille = new ContentValues();
        profilTaille.put(PROFIL_KEY, PROFIL_KEY_TAILLE);
        profilTaille.put(PROFIL_VALUE, profil.getTailleCm());

        ContentValues profilAge = new ContentValues();
        profilAge.put(PROFIL_KEY, PROFIL_KEY_AGE);
        profilAge.put(PROFIL_VALUE, profil.getAge());

        ContentValues profilSexe = new ContentValues();
        profilSexe.put(PROFIL_KEY, PROFIL_KEY_SEXE);
        profilSexe.put(PROFIL_VALUE, profil.getSexe().getINDEX());

        db.delete(TABLE_PROFIL, null, null);
        success = db.insert(TABLE_PROFIL, null, profilPoids) > 0;
        success &= db.insert(TABLE_PROFIL, null, profilTaille) > 0;
        success &= db.insert(TABLE_PROFIL, null, profilAge) > 0;
        success &= db.insert(TABLE_PROFIL, null, profilSexe) > 0;

        return success;
    }

    /**
     * Insère une nouvelle course
     */
    public void insertCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (course.getTrajet() != null) {
            values.put(COURSE_TRAJET_ID, course.getTrajet().getId());
        }
        values.put(COURSE_TYPE, course.getTYPE_COURSE().name());
        values.put(COURSE_DATE, course.getDATE().toString());
        values.put(COURSE_DISTANCE, course.getDistance());
        values.put(COURSE_DUREE, course.getDuree());
        values.put(COURSE_VITESSE, course.getVitesse());
        values.put(COURSE_CALORIES, course.getDistance());
        values.put(COURSE_PAS, course.getPas());

        db.insert(TABLE_COURSE, null, values);
        db.close();
    }

    /**
     * Mets à jour les données d'un trajet
     */
    public void updateTrajet(Trajet trajet) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRAJET_NOM, trajet.getNom());
        values.put(TRAJET_DISTANCE, trajet.getDistance());
        values.put(TRAJET_MEILLEUR_TEMPS, trajet.getMeilleurTemps());
        db.update(TABLE_TRAJET, values, TRAJET_ID + " = ?",
                new String[]{String.valueOf(trajet.getId())});

        deletePoints(db, trajet.getId());
        ajouterTousPoints(db, trajet.getId(), trajet.getListePoints());

        db.close();
    }

    /**
     * Supprimer tous les points liés à un trajet
     */
    private void deletePoints(SQLiteDatabase db, long trajetId) {
        db.delete(TABLE_POINT, POINT_TRAJET_ID + " = ?", new String[]{String.valueOf(trajetId)});
    }

    /**
     * Retourne un liste de tous les trajets
     */
    public ArrayList<Trajet> getTousTrajets() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Trajet> trajets = new ArrayList<>();
        Cursor cursor = db.query(TABLE_TRAJET, null, null, null, null, null, TRAJET_NOM, null);

        if (cursor.moveToFirst()) {
            do {
                int idTrajet = Integer.parseInt(cursor.getString(cursor.getColumnIndex(TRAJET_ID)));
                Trajet trajet = new Trajet(idTrajet, cursor.getString(cursor.getColumnIndex(TRAJET_NOM)),
                        cursor.getDouble(cursor.getColumnIndex(TRAJET_DISTANCE)),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(TRAJET_MEILLEUR_TEMPS))),
                        getListePointsParIdTrajet(db, idTrajet));
                trajets.add(trajet);
            } while (cursor.moveToNext());
        }

        return trajets;
    }

    /**
     * Retourne un trajet spécifique par id
     */
    public Trajet getTrajetById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Trajet trajet = null;
        Cursor cursor = db.query(TABLE_TRAJET, null, TRAJET_ID + " = ?", new String[]{Integer.toString(id)}, null, null, null, null);

        if (cursor.moveToFirst()) {
            trajet = new Trajet(id, cursor.getString(cursor.getColumnIndex(TRAJET_NOM)),
                    Double.parseDouble(cursor.getString(cursor.getColumnIndex(TRAJET_DISTANCE))),
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(TRAJET_MEILLEUR_TEMPS))),
                    getListePointsParIdTrajet(db, id));
        }

        return trajet;
    }

    /**
     * Retourne une liste de points associée à un trajet par id
     */
    private LinkedList<PointCourse> getListePointsParIdTrajet(SQLiteDatabase db, int idTrajet) {
        Cursor cursor = db.query(TABLE_POINT, null, POINT_TRAJET_ID + " = ?", new String[]{String.valueOf(idTrajet)},
                null, null, null, null);
        LinkedList<PointCourse> listePoints = new LinkedList<>();

        if (cursor.moveToFirst()) {
            do {
                PointCourse pointCourse = new PointCourse(cursor.getDouble(cursor.getColumnIndex(POINT_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(POINT_LONGITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(POINT_DISTANCE)));
                listePoints.add(pointCourse);
            } while (cursor.moveToNext());
        }

        return listePoints;
    }

    /**
     * Retourne une liste de toutes les courses et les classes par le classement sélectionné
     */
    public List<Course> getTousCourses(String type, HistorySorts historySorts) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Course> courses = new ArrayList<>();
        Cursor cursor = db.query(TABLE_COURSE, null, COURSE_TYPE + " = ?", new String[]{type}, null, null, historySorts.getORDER_BY_STRING(), null);

        if (cursor.moveToFirst()) {
            do {
                Course course = new Course(
                        cursor.getInt(cursor.getColumnIndex(COURSE_ID)),
                        getTrajetById(cursor.getInt(cursor.getColumnIndex(COURSE_TRAJET_ID))),
                        TypeCourse.valueOf(cursor.getString(cursor.getColumnIndex(COURSE_TYPE))),
                        new Date(cursor.getString(cursor.getColumnIndex(COURSE_DATE))),
                        cursor.getInt(cursor.getColumnIndex(COURSE_DUREE)),
                        cursor.getDouble(cursor.getColumnIndex(COURSE_DISTANCE)),
                        cursor.getDouble(cursor.getColumnIndex(COURSE_VITESSE)),
                        cursor.getInt(cursor.getColumnIndex(COURSE_CALORIES)),
                        cursor.getInt(cursor.getColumnIndex(COURSE_PAS)));
                courses.add(course);
            } while (cursor.moveToNext());
        }

        return courses;
    }

    /**
     * Enlève toutes les courses de la table par type
     */
    public void viderHistorique(TypeCourse typeCourse) {
        getWritableDatabase().delete(TABLE_COURSE, COURSE_TYPE + " = ?", new String[]{typeCourse.name()});
    }

    /**
     * Insère un trajet et tous les points associées
     */
    public void ajouterTrajet(Trajet trajet) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues nouveauTrajet = new ContentValues();
        nouveauTrajet.put(TRAJET_DISTANCE, trajet.getDistance());
        nouveauTrajet.put(TRAJET_MEILLEUR_TEMPS, trajet.getMeilleurTemps());
        nouveauTrajet.put(TRAJET_NOM, trajet.getNom());

        trajet.setId(db.insert(TABLE_TRAJET, null, nouveauTrajet));

        ajouterTousPoints(db, trajet.getId(), trajet.getListePoints());
    }

    /**
     * Insère une liste de point liée à un trajet dans une seule transaction par soucie de performance
     */
    private void ajouterTousPoints(SQLiteDatabase db, long id, LinkedList<PointCourse> listePoints) {
        ContentValues nouveauPoint = new ContentValues();

        try {
            db.beginTransaction();
            for (PointCourse pointCourse : listePoints) {
                nouveauPoint.put(POINT_TRAJET_ID, id);
                nouveauPoint.put(POINT_DISTANCE, pointCourse.getDistance());
                nouveauPoint.put(POINT_LATITUDE, pointCourse.getLatitude());
                nouveauPoint.put(POINT_LONGITUDE, pointCourse.getLongitude());

                db.insert(TABLE_POINT, null, nouveauPoint);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Sélectionne toutes les statistiques en une série de fonctions de groupement
     */
    public Statistiques getStats(TypeCourse typeCourse) {
        SQLiteDatabase db = getReadableDatabase();
        Statistiques statistiques = new Statistiques();
        Cursor cursor = db.rawQuery(REQUETE_STATISTIQUES, new String[]{typeCourse.name()});

        if (cursor != null && cursor.moveToFirst()) {
            statistiques.setCaloriesMoyennes(cursor.getInt(cursor.getColumnIndex(STATS_CALORIES_AVG)));
            statistiques.setCaloriesTotales(cursor.getInt(cursor.getColumnIndex(STATS_CALORIES_SUM)));

            statistiques.setDistanceMoyenne(cursor.getDouble(cursor.getColumnIndex(STATS_DISTANCE_AVG)));
            statistiques.setDistanceTotale(cursor.getDouble(cursor.getColumnIndex(STATS_DISTANCE_SUM)));

            statistiques.setDureeMoyenne(cursor.getLong(cursor.getColumnIndex(STATS_DUREE_AVG)));
            statistiques.setDureeTotale(cursor.getLong(cursor.getColumnIndex(STATS_DUREE_SUM)));

            statistiques.setPasMoyens(cursor.getInt(cursor.getColumnIndex(STATS_PAS_AVG)));
            statistiques.setPasTotaux(cursor.getInt(cursor.getColumnIndex(STATS_PAS_SUM)));

            statistiques.setVitesseMoyenne(cursor.getDouble(cursor.getColumnIndex(STATS_VITESSE_AVG)));
        }

        return statistiques;
    }

    /**
     * Sélectionne toutes les statistiques associées à un trajet
     */
    public Statistiques getStatsTrajet(Trajet trajet) {
        SQLiteDatabase db = getReadableDatabase();
        Statistiques statistiques = new Statistiques();
        Cursor cursor = db.rawQuery(REQUETE_STATISTIQUES_TRAJET, new String[]{Long.toString(trajet.getId())});

        if (cursor != null && cursor.moveToFirst()) {
            statistiques.setCaloriesMoyennes(cursor.getInt(cursor.getColumnIndex(STATS_CALORIES_AVG)));
            statistiques.setCaloriesTotales(cursor.getInt(cursor.getColumnIndex(STATS_CALORIES_SUM)));

            statistiques.setDureeMoyenne(cursor.getLong(cursor.getColumnIndex(STATS_DUREE_AVG)));
            statistiques.setDureeTotale(cursor.getLong(cursor.getColumnIndex(STATS_DUREE_SUM)));

            statistiques.setVitesseMoyenne(cursor.getDouble(cursor.getColumnIndex(STATS_VITESSE_AVG)));

            statistiques.setCountTrajet(cursor.getInt(cursor.getColumnIndex(STATS_COUNT_TRAJET)));
        }

        return statistiques;
    }

    /**
     * Supprime un trajet
     */
    public void deleteTrajet(Trajet trajet) {
        SQLiteDatabase db = getWritableDatabase();
        Integer intNull = null;

        deletePoints(db, trajet.getId());

        ContentValues values = new ContentValues();
        values.put(COURSE_TRAJET_ID, intNull);
        db.update(TABLE_COURSE, values, COURSE_TRAJET_ID + " = ?",
                new String[]{String.valueOf(trajet.getId())});

        db.delete(TABLE_TRAJET, TRAJET_ID + " = ?", new String[]{String.valueOf(trajet.getId())});
    }

    public void deleteCourse(Course course) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_COURSE, COURSE_ID + " = ?", new String[]{String.valueOf(course.getId())});
    }
}
