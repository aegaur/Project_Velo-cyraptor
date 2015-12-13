package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.statistics;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.PointCourse;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.Statistiques;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.Trajet;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.TypeCourse;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.persistance.DbHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.LinkedList;

public class TrajetDetail extends AppCompatActivity implements GoogleMap.OnMapLoadedCallback {

    public static final String CLEE_TRAJET = "trajet";
    private static final int NOMBRE_MILLIS_DANS_SECONDE = 1000;
    private static final int NOMBRE_SECONDES_DANS_MINUTE = 60;
    private static final float LARGEUR_LIGNE = 7.5f;
    private static final int PADDING_CENTER_ALL_POINTS = 50;

    private Statistiques statistiques;
    private Trajet trajet;

    private TextView lblNomTrajet;
    private TextView lblDureeSum;
    private TextView lblDureeAvg;
    private TextView lblDistanceTrajet;
    private TextView lblCountTrajet;
    private TextView lblCaloriesSum;
    private TextView lblCaloriesAvg;
    private TextView lblVitesseAvg;
    private SupportMapFragment fragMap;
    private GoogleMap map;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajet_detail);

        trajet = (Trajet) getIntent().getSerializableExtra(CLEE_TRAJET);
        dbHelper = DbHelper.getInstance(this);
        statistiques = dbHelper.getStatsTrajet(trajet);
        assignerCompostants();
        affecterValeurs();
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trajet_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.fragment_stats_trajet_menu_suppr) {
            confirmerSuppressionTrajet();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmerSuppressionTrajet(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.fragment_stats_trajet_dialog_suppr_title)
                .setMessage(R.string.fragment_stats_trajet_dialog_suppr_message)
                .setPositiveButton(R.string.fragment_stats_trajet_dialog_suppr_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteTrajet(trajet);
                        finish();
                    }
                })
                .setNegativeButton(R.string.fragment_stats_trajet_dialog_suppr_no, null).show();
    }

    private void assignerCompostants() {
        lblNomTrajet = (TextView) findViewById(R.id.stats_fragment_trajet_lbl_nom);
        lblCaloriesAvg = (TextView) findViewById(R.id.stats_fragment_trajet_lbl_calories_avg);
        lblCaloriesSum = (TextView) findViewById(R.id.stats_fragment_trajet_lbl_calories_totaux);
        lblDistanceTrajet = (TextView) findViewById(R.id.stats_fragment_trajet_lbl_path_distance);
        lblCountTrajet = (TextView) findViewById(R.id.stats_fragment_trajet_lbl_count);
        lblDureeAvg = (TextView) findViewById(R.id.stats_fragment_trajet_lbl_duree_avg);
        lblDureeSum = (TextView) findViewById(R.id.stats_fragment_trajet_lbl_duree_totale);
        lblVitesseAvg = (TextView) findViewById(R.id.stats_fragment_trajet_lbl_vitesse_avg);
        fragMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.stats_fragment_trajet_map);
    }

    private void affecterValeurs() {
        lblNomTrajet.setText(trajet.getNom());
        lblCaloriesAvg.setText(Integer.toString(statistiques.getCaloriesMoyennes()));
        lblCaloriesSum.setText(Integer.toString(statistiques.getCaloriesTotales()));
        lblDistanceTrajet.setText(String.format(getString(R.string.fragment_stats_trajet_lbl_distance_defaut),
                trajet.getDistance()));
        lblCountTrajet.setText(Integer.toString(statistiques.getCountTrajet()));
        lblDureeAvg.setText(String.format(getString(R.string.fragment_stats_trajet_lbl_duree_defaut),
                millisToMin(statistiques.getDureeMoyenne())));
        lblDureeSum.setText(String.format(getString(R.string.fragment_stats_trajet_lbl_duree_defaut),
                millisToMin(statistiques.getDureeTotale())));
        lblVitesseAvg.setText(String.format(getString(R.string.fragment_stats_trajet_lbl_vitesse_defaut),
                statistiques.getVitesseMoyenne()));
    }

    private double millisToMin(double millis) {
        double secondes = millis / NOMBRE_MILLIS_DANS_SECONDE;
        double minutes = secondes / NOMBRE_SECONDES_DANS_MINUTE;
        return minutes;
    }

    /**
     * Instancie la map si elle est nulle
     */
    private void setUpMapIfNeeded() {
        if (map == null) {
            map = fragMap.getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }

    /**
     * Démarre l'écoute de l'évènement OnMapLoaded
     */
    private void setUpMap() {
        map.setOnMapLoadedCallback(this);
    }

    /**
     * Initialise les paramètres de la map
     */
    @Override
    public void onMapLoaded() {
        LinkedList<PointCourse> listePoint = trajet.getListePoints();
        LatLng[] tabLatLng = new LatLng[listePoint.size()];
        for (int i = 0; i < listePoint.size(); i++) {
            tabLatLng[i] = listePoint.get(i).getLatLng();
        }
        map.addPolyline(new PolylineOptions().width(LARGEUR_LIGNE).add(tabLatLng)
                .color(this.getResources().getColor(R.color.colorGhost)));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : tabLatLng) {
            builder.include(latLng);
        }
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), PADDING_CENTER_ALL_POINTS));
    }
}
