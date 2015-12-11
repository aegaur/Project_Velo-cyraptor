package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.run;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.*;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.persistance.DbHelper;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.util.CustomChronometer;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.util.OnFragmentInteractionListener;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunFragment extends Fragment implements CallbackMap {
    private static final String CLEE_ARGUMENT_RUNNING = "running";
    private static final double MULTIPLICATEUR_POIDS_CALORIES = 0.72;
    private static final double MULTIPLICATEUR_CONVERTION_KM_TO_MILES = 0.62137;
    private static final double DIVIDANTE_CONVERTION_DISTANCE_VELO = 5.63;
    private static final double DIVIDANTE_CONVERTION_DISTANCE_COURSE = 1;
    private static final int NOMBRE_METRES_DANS_KILOMETRE = 1000;

    private Course course;
    private Podometre podometre;
    private GestionnaireMap gestionnaireMap;
    private DbHelper dbHelper;

    private boolean running;
    private boolean androidKitKatOrHigher;
    private Profil profil;
    private CustomChronometer chronoTime;
    private OnFragmentInteractionListener mListener;
    private Button btnStart;
    private Button btnStop;
    private Button btnResume;
    private Button btnPath;
    private LinearLayout layStartPause;
    private LinearLayout layStopResume;
    private LinearLayout laySteps;
    private TextView lblDistance;
    private TextView lblCalories;
    private TextView lblSpeed;
    private TextView lblSteps;
    private ProgressBar pgsProgresTrajet;
    private int indiceTrajetChoisi = 0;
    private Trajet trajet;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @@return A new instance of fragment RunFragment.
     */
    public static RunFragment newInstance(boolean running) {
        RunFragment fragment = new RunFragment();
        Bundle args = new Bundle();
        args.putBoolean(CLEE_ARGUMENT_RUNNING, running);
        fragment.setArguments(args);
        return fragment;
    }

    public RunFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DbHelper.getInstance(getContext());
        podometre = new Podometre(getContext());
        gestionnaireMap = new GestionnaireMap(getContext(), this);
        this.running = getArguments().getBoolean(CLEE_ARGUMENT_RUNNING);
        this.androidKitKatOrHigher = true;
        this.profil = dbHelper.getProfil();
        this.trajet = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.run_fragment_run_run, container, false);

        initialiserComposants(view);
        initialiserListeners();
        adapterView();

        return view;
    }

    private void adapterView() {
        if (!running) {
            btnStart.setText(R.string.activity_bike_btn_start);
        }
        if (!(running && androidKitKatOrHigher)) {
            laySteps.setVisibility(View.GONE);
        }
    }

    private void initialiserComposants(View view) {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.activity_run_map);
        mapFragment.getMapAsync(gestionnaireMap);

        chronoTime = (CustomChronometer) view.findViewById(R.id.activity_run_chrono);
        btnStart = (Button) view.findViewById(R.id.activity_run_btn_start);
        btnStop = (Button) view.findViewById(R.id.activity_run_btn_stop);
        btnResume = (Button) view.findViewById(R.id.activity_run_btn_resume);
        btnPath = (Button) view.findViewById(R.id.activity_run_btn_path);
        layStartPause = (LinearLayout) view.findViewById(R.id.activity_run_lay_start_pause);
        layStopResume = (LinearLayout) view.findViewById(R.id.activity_run_lay_stop_resume);
        lblDistance = (TextView) view.findViewById(R.id.activity_run_lbl_distance);
        lblCalories = (TextView) view.findViewById(R.id.activity_run_lbl_calories);
        lblSteps = (TextView) view.findViewById(R.id.activity_run_lbl_pas);
        lblSpeed = (TextView) view.findViewById(R.id.activity_run_lbl_vitesse);
        laySteps = (LinearLayout) view.findViewById(R.id.activity_run_lay_steps);
        pgsProgresTrajet = (ProgressBar) view.findViewById(R.id.activity_run_pgs_progres_trajet);
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
        btnPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choisirTrajet();
            }
        });
    }

    private void choisirTrajet() {
        final ArrayList<Trajet> listeTrajets = dbHelper.getTousTrajets();
        AlertDialog.Builder constructeurDialog = new AlertDialog.Builder(getContext());

        if (listeTrajets.size() > 0) {
            constructeurDialog.setTitle(R.string.activity_run_path_selection_title);

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.select_dialog_singlechoice);

            arrayAdapter.add(getString(R.string.activity_run_path_selection_none));
            for (Trajet trajet : listeTrajets) {
                arrayAdapter.add(trajet.getNom());
            }

            constructeurDialog.setNegativeButton(R.string.activity_run_path_selection_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            constructeurDialog.setPositiveButton(getString(R.string.activity_run_path_selection_positive), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    trajet = --indiceTrajetChoisi >= 0 ? trajet = listeTrajets.get(indiceTrajetChoisi) : null;
                    refreshAffichageTrajet();
                }
            });
            constructeurDialog.setSingleChoiceItems(arrayAdapter, indiceTrajetChoisi, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    indiceTrajetChoisi = i;
                }
            });
            constructeurDialog.show();
        } else {
            constructeurDialog.setMessage(R.string.activity_run_path_selection_no_path);
            constructeurDialog.setTitle(R.string.activity_run_path_selection_title);
            constructeurDialog.setPositiveButton(R.string.activity_run_path_selection_ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            constructeurDialog.show();
            trajet = null;
            refreshAffichageTrajet();
        }
    }

    private void refreshAffichageTrajet() {
        if (trajet == null) {
            btnPath.setText(getString(R.string.activity_run_btn_path));
            pgsProgresTrajet.setVisibility(View.INVISIBLE);
        } else {
            btnPath.setText(String.format(getString(R.string.activity_run_btn_path_name), trajet.getNom()));
            pgsProgresTrajet.setVisibility(View.VISIBLE);
            gestionnaireMap.setPointsTrajet(trajet.getListePoints());
            pgsProgresTrajet.setProgress(0);
            pgsProgresTrajet.setSecondaryProgress(0);
            pgsProgresTrajet.setMax(convertirEnMetres(trajet.getDistance()));
        }
    }

    private void start() {
        if (checkGpsEnabled()) {
            chronoTime.start();
            podometre.start();
            gestionnaireMap.start();
            toggleLayouts(EtatLayoutsRun.PAUSE);
            course = new Course(running ? TypeCourse.A_PIED : TypeCourse.A_VELO);
            gestionnaireMap.start();
            btnPath.setEnabled(false);
        }
    }

    private boolean checkGpsEnabled() {
        LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gpsIsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsIsEnabled) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.activity_run_dialog_gps_message)
                    .setPositiveButton(R.string.activity_run_dialog_gps_settings,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }
                            })
                    .setNegativeButton(R.string.activity_run_dialog_gps_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
        }

        return gpsIsEnabled;
    }

    private void pause() {
        chronoTime.stop();
        podometre.pause();
        gestionnaireMap.pause();
        toggleLayouts(EtatLayoutsRun.STOP_RESUME);
    }

    private void stop() {
        course.setDuree(chronoTime.getElapsedTimeInMillis());
        gererFinTrajet();
        refreshAffichageTrajet();
    }

    private void reinitialiserActivity() {
        chronoTime.reset();
        podometre.stop();
        gestionnaireMap.stop();
        toggleLayouts(EtatLayoutsRun.START);
        resetInfos();
        dbHelper.insertCourse(course);
        btnPath.setEnabled(true);
    }

    private void gererFinTrajet() {
        if (this.trajet == null) {
            confirmerCreationNouveauTrajet();
        } else {
            reinitialiserActivity();
        }
        this.trajet = null;
    }

    private void confirmerCreationNouveauTrajet() {
        final EditText input = new EditText(getContext());
        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.activity_run_path_creation_title)
                .setMessage(R.string.activity_run_path_creation_message)
                .setPositiveButton(R.string.activity_run_path_creation_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        creerNouveauTrajer(input.getText().toString());
                        reinitialiserActivity();
                    }
                })
                .setNegativeButton(R.string.activity_run_path_creation_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reinitialiserActivity();
                    }
                })
                .setView(input)
                .show();
    }

    private void creerNouveauTrajer(String nomTrajet) {
        dbHelper.ajouterTrajet(new Trajet(nomTrajet, gestionnaireMap.getDistanceTotale(),
                chronoTime.getElapsedTimeInMillis(), gestionnaireMap.getListePoints()));
    }

    private void resume() {
        chronoTime.start();
        podometre.resume();
        gestionnaireMap.resume();
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
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        updateProgress();
    }

    private void updateProgress() {
        if (trajet != null) {
            pgsProgresTrajet.setProgress(convertirEnMetres(course.getDistance()));
            pgsProgresTrajet.setSecondaryProgress(convertirEnMetres(gestionnaireMap.getDistanceCouranteFantome()));
        }
    }

    private void updatePas() {
        course.setPas(podometre.getNombrePas());
        this.lblSteps.setText(Integer.toString(course.getPas()));
    }

    private void updateCalories() {
        course.setCalories(calculerCalories());
        this.lblCalories.setText(Integer.toString(course.getCalories()));
    }

    private int calculerCalories() {
        return (int) Math.round((distanceKmToMiles(course.getDistance()) /
                (running ? DIVIDANTE_CONVERTION_DISTANCE_COURSE : DIVIDANTE_CONVERTION_DISTANCE_VELO))
                * (MULTIPLICATEUR_POIDS_CALORIES * profil.getPoidsLbs()));
    }

    private double distanceKmToMiles(double distanceKm) {
        return distanceKm * MULTIPLICATEUR_CONVERTION_KM_TO_MILES;
    }

    private void updateVitesse() {
        double vitesse;

        vitesse = course.getDistance() / chronoTime.getElapsedTimeInHours();

        this.course.setVitesse(vitesse);
        String vitesseStr = String.format(getResources().getString(R.string.activity_run_lbl_vitesse_valeur), vitesse);
        this.lblSpeed.setText(vitesseStr);
    }

    private void updateDistance() {
        double resultat = gestionnaireMap.getDistanceTotale();
        this.course.setDistance(resultat);
        String distance = String.format(getResources().getString(R.string.activity_run_lbl_distance_valeur), resultat);
        this.lblDistance.setText(distance);
    }

    @Override
    public void callbackMap() {
        updateInfos();
    }

    private int convertirEnMetres(double distanceEnKm) {
        return (int) Math.round(distanceEnKm * NOMBRE_METRES_DANS_KILOMETRE);
    }
}
