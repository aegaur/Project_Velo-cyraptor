package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.history;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.Course;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.TypeCourse;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CourseDetails extends AppCompatActivity {

    private static final String CLEE_COURSE = "course";
    private static final int NOMBRE_MILLI_DANS_SEC = 1000;
    private static final int NOMBRE_SECONDES_DANS_MIN = 60;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

    private double formatDuree(double duree1) {
        double duree2 = duree1 / NOMBRE_MILLI_DANS_SEC;
        double duree3 = duree2 / NOMBRE_SECONDES_DANS_MIN;
        return duree3;
    }

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

    private void assignerComposants() {
        lblCalories = (TextView) findViewById(R.id.content_course_details_lbl_calories);
        lblDate = (TextView) findViewById(R.id.content_course_details_lbl_date);
        lblDistance = (TextView) findViewById(R.id.content_course_details_lbl_distance);
        lblDuree = (TextView) findViewById(R.id.content_course_details_lbl_duree);
        lblPas = (TextView) findViewById(R.id.content_course_details_lbl_pas);
        lblVitesse = (TextView) findViewById(R.id.content_course_details_lbl_vitesse);
        fragMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.content_course_details_map);
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = fragMap.getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}
