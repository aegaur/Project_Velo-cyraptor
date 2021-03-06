package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.history;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.Course;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.PointCourse;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.TypeCourse;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.persistance.DbHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

import java.util.LinkedList;

public class CourseDetails extends AppCompatActivity implements GoogleMap.OnMapLoadedCallback {

    private static final String CLEE_COURSE = "course";
    private static final int NOMBRE_MILLI_DANS_SEC = 1000;
    private static final int NOMBRE_SECONDES_DANS_MIN = 60;
    private static final float LARGEUR_LIGNE = 7.5f;
    private static final int PADDING_CENTER_ALL_POINTS = 50;

    private Course course;

    private TextView lblCalories;
    private TextView lblDate;
    private TextView lblDistance;
    private TextView lblDuree;
    private TextView lblPas;
    private TextView lblVitesse;
    private SupportMapFragment fragMap;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_run);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        course = (Course) getIntent().getSerializableExtra(CLEE_COURSE);

        getSupportActionBar().setIcon(course.getTYPE_COURSE() == TypeCourse.A_PIED ? R.drawable.ic_running
                : R.drawable.ic_biking);

        assignerComposants();
        assignerValeurs();
        setUpMapIfNeeded();
        modifierVueSiNecessaire();
    }

    /**
     * Assigne les valeurs du modèle à l'interface
     */
    private void assignerValeurs() {
        lblCalories.setText(Integer.toString(course.getCalories()));
        lblDate.setText(course.getDATE().toString());
        lblDistance.setText(String.format(getString(R.string.content_course_lbl_distance_defaut), course.getDistance()));
        lblDuree.setText(String.format(getString(R.string.content_course_lbl_duree_defaut), formatDuree(course.getDuree())));
        lblVitesse.setText(String.format(getString(R.string.content_course_lbl_speed_defaut), course.getVitesse()));
        if (course.getTrajet() != null && course.getTYPE_COURSE() == TypeCourse.A_VELO) {
            lblPas.setText(course.getPas());
        }
    }

    /**
     * Convertit la durée de millisecondes à minutes
     *
     * @param millisecondes la durée en milisecondes
     * @return
     */
    private double formatDuree(double millisecondes) {
        double secondes = millisecondes / NOMBRE_MILLI_DANS_SEC;
        double minutes = secondes / NOMBRE_SECONDES_DANS_MIN;
        return minutes;
    }

    /**
     * Modifie la vue en fonction du type de course et de la présence d'un trajet
     */
    private void modifierVueSiNecessaire() {
        if (course.getTrajet() == null) {
            fragMap.getView().setVisibility(View.INVISIBLE);
        }
        if (course.getTYPE_COURSE() == TypeCourse.A_VELO) {
            lblPas.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.activity_course_detail_suppr) {
            confirmerSuppressionCourse();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialize les variables de classe avec la vue associés
     */
    private void assignerComposants() {
        lblCalories = (TextView) findViewById(R.id.content_course_details_lbl_calories);
        lblDate = (TextView) findViewById(R.id.content_course_details_lbl_date);
        lblDistance = (TextView) findViewById(R.id.content_course_details_lbl_distance);
        lblDuree = (TextView) findViewById(R.id.content_course_details_lbl_duree);
        lblPas = (TextView) findViewById(R.id.content_course_details_lbl_pas);
        lblVitesse = (TextView) findViewById(R.id.content_course_details_lbl_vitesse);
        fragMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.content_course_details_map);
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
        if (course.getTrajet() != null) {
            map.setOnMapLoadedCallback(this);
        }
    }

    /**
     * Initialise les paramètres de la map
     */
    @Override
    public void onMapLoaded() {
        LinkedList<PointCourse> listePoint = course.getTrajet().getListePoints();
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

    /**
     * Confirme la suppression du de la course
     */
    private void confirmerSuppressionCourse() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.activity_course_dialog_suppr_title)
                .setMessage(R.string.activity_course_dialog_suppr_message)
                .setPositiveButton(R.string.activity_course_dialog_suppr_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DbHelper.getInstance(CourseDetails.this).deleteCourse(course);
                        finish();
                    }
                })
                .setNegativeButton(R.string.activity_course_dialog_suppr_no, null).show();
    }
}
