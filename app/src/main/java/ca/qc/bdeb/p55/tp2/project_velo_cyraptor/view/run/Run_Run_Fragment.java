package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.run;

import android.app.Activity;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.Course;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.Podometre;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.PointCourse;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.TypeCourse;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.util.CustomChronometer;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.util.OnFragmentInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Run_Run_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Run_Run_Fragment extends Fragment implements OnMapReadyCallback {
    private static final float ZOOM_INITIAL = 15.0f;
    private static final int INTERVALLE_SAUVEGARDE_MOUVEMENT = 5;
    private static final float LARGEUR_LIGNE = 7.5f;
    private static final int NOMBRE_RESULTATS = 3;
    private static final int NOMBRE_DE_METRES_PAR_KM = 1000;
    private static final int INDICE_RESULTAT = 0;

    private LinkedList<PointCourse> listePoints = new LinkedList<>();
    private boolean onMapAnimationFinished = false;
    private boolean firstTime = true;

    private Course course;
    private Podometre podometre;
    private GoogleMap mMap;
    private Polyline polyline;
    private CustomChronometer chronoTime;
    private OnFragmentInteractionListener mListener;
    private Button btnStart;
    private Button btnStop;
    private Button btnResume;
    private LinearLayout layStartPause;
    private LinearLayout layStopResume;
    private TextView lblDistance;
    private TextView lblCalories;
    private TextView lblSpeed;
    private TextView lblSteps;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @@return A new instance of fragment Run_Run_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Run_Run_Fragment newInstance() {
        Run_Run_Fragment fragment = new Run_Run_Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public Run_Run_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        podometre = new Podometre(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.run_fragment_run_run, container, false);

        initialiserComposants(view);
        initialiserListeners();

        return view;
    }

    private void initialiserComposants(View view) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.activity_run_map);
        mapFragment.getMapAsync(this);

        chronoTime = (CustomChronometer) view.findViewById(R.id.activity_run_chrono);
        btnStart = (Button) view.findViewById(R.id.activity_run_btn_start);
        btnStop = (Button) view.findViewById(R.id.activity_run_btn_stop);
        btnResume = (Button) view.findViewById(R.id.activity_run_btn_resume);
        layStartPause = (LinearLayout) view.findViewById(R.id.activity_run_lay_start_pause);
        layStopResume = (LinearLayout) view.findViewById(R.id.activity_run_lay_stop_resume);
        lblDistance = (TextView) view.findViewById(R.id.activity_run_lbl_distance);
        lblCalories = (TextView) view.findViewById(R.id.activity_run_lbl_calories);
        lblSteps = (TextView) view.findViewById(R.id.activity_run_lbl_pas);
        lblSpeed = (TextView) view.findViewById(R.id.activity_run_lbl_vitesse);
    }

    private void initialiserListeners() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chronoTime.isRunning()) {
                    pause();
                } else {
                    start();
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });
        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resume();
            }
        });
    }

    private void start() {
        chronoTime.start();
        podometre.start();
        toggleLayouts(EtatLayoutsRun.PAUSE);
        course = new Course(TypeCourse.A_PIED);
        if (listePoints.size() == 0) {
            Location location = mMap.getMyLocation();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            listePoints.add(new PointCourse(latLng, chronoTime.getElapsedTimeInMillis()));
            polyline = mMap.addPolyline(new PolylineOptions().add(latLng)
                    .width(LARGEUR_LIGNE)
                    .color(getResources().getColor(R.color.colorAccent)));
        }
    }

    private void pause() {
        chronoTime.stop();
        podometre.pause();
        toggleLayouts(EtatLayoutsRun.STOP_RESUME);
    }

    private void stop() {
        chronoTime.reset();
        podometre.stop();
        toggleLayouts(EtatLayoutsRun.START);
        listePoints.clear();
        polyline.remove();
        resetInfos();
    }

    private void resume() {
        chronoTime.start();
        podometre.resume();
        toggleLayouts(EtatLayoutsRun.PAUSE);
    }

    private void toggleLayouts(EtatLayoutsRun etatLayoutsRun) {
        switch (etatLayoutsRun) {
            case PAUSE:
                btnStart.setText(R.string.activity_run_btn_pause);
                layStopResume.setVisibility(View.GONE);
                layStartPause.setVisibility(View.VISIBLE);
                break;
            case START:
                btnStart.setText(R.string.activity_run_btn_start);
                layStopResume.setVisibility(View.GONE);
                layStartPause.setVisibility(View.VISIBLE);
                break;
            case STOP_RESUME:
                layStopResume.setVisibility(View.VISIBLE);
                layStartPause.setVisibility(View.GONE);
                break;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings uiSettings = mMap.getUiSettings();
        mMap.setMyLocationEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            private int compteurSecondes = 0;

            @Override
            public void onMyLocationChange(Location location) {
                LatLng myLatLng;
                if (firstTime) {
                    firstTime = false;
                    myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    animerCameraMap(CameraUpdateFactory.newLatLngZoom(myLatLng, ZOOM_INITIAL));
                } else if (onMapAnimationFinished && compteurSecondes >= INTERVALLE_SAUVEGARDE_MOUVEMENT
                        && chronoTime.isRunning()) {
                    myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    animerCameraMap(CameraUpdateFactory.newLatLng(myLatLng));
                    compteurSecondes = 0;
                    addPointAndUpdateView(myLatLng);
                }
                compteurSecondes++;
            }
        });
    }

    private void animerCameraMap(CameraUpdate cameraUpdate) {
        mMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                onMapAnimationFinished = true;
            }

            @Override
            public void onCancel() {
                onMapAnimationFinished = true;
            }
        });
    }

    private void addPointAndUpdateView(LatLng position) {
        updatePath(position);
        updateInfos();
    }

    private void resetInfos() {
        this.lblDistance.setText(getResources().getString(R.string.activity_run_lbl_distance_defaut));
        this.lblSpeed.setText(getResources().getString(R.string.activity_run_lbl_vitesse_defaut));
        this.lblCalories.setText(getResources().getString(R.string.activity_run_lbl_calories_defaut));
        this.lblSteps.setText(getResources().getString(R.string.activity_run_lbl_steps_defaut));
    }

    private void updateInfos() {
        updateDistance();
        updateVitesse();
        updateCalories();
        updatePas();
    }

    private void updatePas() {
        course.setPas(podometre.getNombrePas());
        this.lblSteps.setText(Integer.toString(course.getPas()));
    }

    private void updateCalories() {
        course.setCalories(course.getCalories() + new Random().nextInt(100));
        this.lblCalories.setText(Integer.toString(course.getCalories()));
    }

    private void updateVitesse() {
        double vitesse;

        vitesse = course.getDistance() / chronoTime.getElapsedTimeInHours();

        this.course.setVitesse(vitesse);
        String vitesseStr = String.format(getResources().getString(R.string.activity_run_lbl_vitesse_valeur), vitesse);
        this.lblSpeed.setText(vitesseStr);
    }

    private void updateDistance() {
        float[] results = new float[NOMBRE_RESULTATS];
        float resultat = 0.0f;

        ListIterator<PointCourse> iteratoryPoints = listePoints.listIterator();
        if (listePoints.size() > 1) {
            PointCourse point1 = iteratoryPoints.next();
            while (iteratoryPoints.nextIndex() < listePoints.size()) {
                PointCourse point2 = iteratoryPoints.next();
                resultat += calculerDistance(point1.getLatLng(), point2.getLatLng(), results);
                point1 = point2;
            }
        }
        this.course.setDistance(resultat);
        String distance = String.format(getResources().getString(R.string.activity_run_lbl_distance_valeur), resultat);
        this.lblDistance.setText(distance);
    }

    private float calculerDistance(LatLng latLng1, LatLng latLng2, float[] results) {
        Location.distanceBetween(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude, results);
        return results[INDICE_RESULTAT] / NOMBRE_DE_METRES_PAR_KM;
    }

    private void updatePath(LatLng position) {
        PointCourse currentPosition = new PointCourse(position, chronoTime.getElapsedTimeInMillis());
        List<LatLng> listePoints = polyline.getPoints();
        listePoints.add(position);
        polyline.setPoints(listePoints);
        this.listePoints.add(currentPosition);
    }
}
