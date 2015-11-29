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

import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.PointCourse;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.util.CustomChronometer;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.util.OnFragmentInteractionListener;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Run_Bike_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Run_Bike_Fragment extends Fragment implements OnMapReadyCallback {
    private static final float ZOOM_INITIAL = 15.0f;
    private static final int INTERVALLE_MOUVEMENT_SAUVEGARD = 5;
    private static final float LARGEUR_LIGNE = 7.5f;

    private LinkedList<PointCourse> listePoints = new LinkedList<>();
    private boolean onMapAnimationFinished = false;
    private boolean firstTime = true;

    private GoogleMap mMap;
    private Polyline polyline;
    private CustomChronometer chronoTime;
    private OnFragmentInteractionListener mListener;
    private Button btnStart;
    private Button btnStop;
    private Button btnResume;
    private LinearLayout layStartPause;
    private LinearLayout layStopResume;

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

    public Run_Bike_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.run_fragment_run_bike, container, false);

        initialiserComposants(view);
        initialiserListeners();

        return view;
    }

    private void initialiserComposants(View view) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.activity_bike_map);
        mapFragment.getMapAsync(this);

        chronoTime = (CustomChronometer) view.findViewById(R.id.activity_bike_chrono);
        btnStart = (Button) view.findViewById(R.id.activity_bike_btn_start);
        btnStop = (Button) view.findViewById(R.id.activity_bike_btn_stop);
        btnResume = (Button) view.findViewById(R.id.activity_bike_btn_resume);
        layStartPause = (LinearLayout) view.findViewById(R.id.activity_bike_lay_start_pause);
        layStopResume = (LinearLayout) view.findViewById(R.id.activity_bike_lay_stop_resume);
    }

    private void initialiserListeners() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chronoTime.isRunning()) {
                    chronoTime.stop();
                    toggleLayouts(EtatLayoutsRun.STOP_RESUME);
                } else {
                    chronoTime.start();
                    if (listePoints.size() == 0) {
                        Location location = mMap.getMyLocation();
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        listePoints.add(new PointCourse(latLng, chronoTime.getElapsedTimeInMillis()));
                        polyline = mMap.addPolyline(new PolylineOptions().add(latLng)
                                .width(LARGEUR_LIGNE)
                                .color(getResources().getColor(R.color.colorAccent)));
                    }
                    toggleLayouts(EtatLayoutsRun.PAUSE);
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronoTime.reset();
                toggleLayouts(EtatLayoutsRun.START);
                listePoints.clear();
                polyline.remove();
            }
        });
        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronoTime.start();
                toggleLayouts(EtatLayoutsRun.PAUSE);
            }
        });
    }

    private void toggleLayouts(EtatLayoutsRun etatLayoutsRun) {
        switch (etatLayoutsRun) {
            case PAUSE:
                btnStart.setText(R.string.activity_run_btn_pause);
                layStopResume.setVisibility(View.GONE);
                layStartPause.setVisibility(View.VISIBLE);
                break;
            case START:
                btnStart.setText(R.string.activity_bike_btn_start);
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
                } else if (onMapAnimationFinished && compteurSecondes >= INTERVALLE_MOUVEMENT_SAUVEGARD
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
        PointCourse currentPosition = new PointCourse(position, chronoTime.getElapsedTimeInMillis());
        List<LatLng> listePoints = polyline.getPoints();
        listePoints.add(position);
        polyline.setPoints(listePoints);
        this.listePoints.add(currentPosition);
    }

}
