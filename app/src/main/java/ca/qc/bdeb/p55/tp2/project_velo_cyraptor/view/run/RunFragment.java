package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.run;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
    /**
     * Clee de l'argument de la variable boolean running passée en paramètre du fragment
     */
    private static final String CLEE_ARGUMENT_RUNNING = "running";
    private static final double MULTIPLICATEUR_POIDS_CALORIES = 0.72;
    private static final double MULTIPLICATEUR_CONVERTION_KM_TO_MILES = 0.62137;
    private static final double DIVIDANTE_CONVERTION_DISTANCE_VELO = 5.63;
    private static final double DIVIDANTE_CONVERTION_DISTANCE_COURSE = 1;
    private static final int NOMBRE_METRES_DANS_KILOMETRE = 1000;

    /**
     * Variable déterminant s'il s'agit d'une course à pied (true) ou à vélo (false)
     */
    private boolean running;
    /**
     * Variable déterminant si la versin d'android est KitKat ou plus
     */
    private boolean androidKitKatOrHigher;

    private Course course;
    private Podometre podometre;
    private GestionnaireMap gestionnaireMap;
    private DbHelper dbHelper;
    private Profil profil;
    /**
     * L'indice du trajet choisi s'il y en a un, 0 signifie aucun trajet
     */
    private int indiceTrajetChoisi = 0;

    private CustomChronometer choChronometre;
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
        this.dbHelper = DbHelper.getInstance(getContext());
        this.podometre = new Podometre(getContext());
        this.gestionnaireMap = new GestionnaireMap(getContext(), this);
        this.running = getArguments().getBoolean(CLEE_ARGUMENT_RUNNING);
        this.course = new Course(running ? TypeCourse.A_PIED : TypeCourse.A_VELO);
        this.androidKitKatOrHigher = getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
        this.profil = dbHelper.getProfil();
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

    /**
     * Cache ou affiche certains composants selon les conditions
     */
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

        choChronometre = (CustomChronometer) view.findViewById(R.id.activity_run_chrono);
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
                if (choChronometre.isRunning()) {
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

    /**
     * Permet de choisir un trajet
     */
    private void choisirTrajet() {
        final ArrayList<Trajet> listeTrajets = dbHelper.getTousTrajets();
        final AlertDialog.Builder constructeurDialog = new AlertDialog.Builder(getContext());

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
                    course.setTrajet(indiceTrajetChoisi - 1 >= 0 ? listeTrajets.get(indiceTrajetChoisi - 1) : null);
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
            this.course.setTrajet(null);
            refreshAffichageTrajet();
        }
    }

    /**
     * Appelée au changement de trajet, la méthode mets l'affichage à jour
     */
    private void refreshAffichageTrajet() {
        Trajet trajet = course.getTrajet();
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

    /**
     * Démarre une nouvelle course
     */
    private void start() {
        if (checkGpsEnabled()) {
            if (gestionnaireMap.isReady()) {
                course = new Course(running ? TypeCourse.A_PIED : TypeCourse.A_VELO, course.getTrajet());
                choChronometre.start();
                podometre.start();
                gestionnaireMap.start();
                toggleLayouts(EtatLayoutsRun.PAUSE);
                btnPath.setEnabled(false);
            } else {
                Toast.makeText(getContext(), R.string.activity_run_toast_location_not_ready, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Vérifie si le gps est activé, sinon affiche un message proposant d'aller dans les options
     */
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

    /**
     * Appelée sur le click sur pause
     */
    private void pause() {
        choChronometre.stop();
        podometre.pause();
        gestionnaireMap.pause();
        toggleLayouts(EtatLayoutsRun.STOP_RESUME);
    }

    /**
     * Appelée sur le click sur stop
     */
    private void stop() {
        course.setDuree(choChronometre.getElapsedTimeInMillis());
        gererFinTrajet();
        refreshAffichageTrajet();
    }

    /**
     * Gère la fin de la course
     */
    private void gererFinTrajet() {
        if (this.course.getTrajet() == null) {
            confirmerCreationNouveauTrajet();
        } else if (this.course.getTrajet().getMeilleurTemps() > this.choChronometre.getElapsedTimeInMillis()) {
            confirmerMiseAJourTrajet();
        } else {
            reinitialiserActivity();
        }
    }

    /**
     * Appelée si le coureur a fait un meilleur temps que celui sauvegarder dans le trajet
     */
    private void confirmerMiseAJourTrajet() {
        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.activity_run_path_mise_a_jour_title)
                .setMessage(R.string.activity_run_path_mise_a_jour_message)
                .setPositiveButton(R.string.activity_run_path_mise_a_jour_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mettreTrajetAJour();
                        reinitialiserActivity();
                    }
                })
                .setNegativeButton(R.string.activity_run_path_mise_a_jour_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reinitialiserActivity();
                    }
                })
                .show();
    }

    private void mettreTrajetAJour() {
        Trajet trajet = this.course.getTrajet();
        trajet.setMeilleurTemps(this.choChronometre.getElapsedTimeInMillis());
        trajet.setListePoints(this.gestionnaireMap.getListePoints());
        trajet.setDistance(this.gestionnaireMap.getDistanceTotale());
        dbHelper.updateTrajet(trajet);
    }

    /**
     * Appelée si le coureur ne suivait pas de trajet, elle suggère la création d'un nouveau trajet
     */
    private void confirmerCreationNouveauTrajet() {
        final EditText input = new EditText(getContext());
        input.setHint(R.string.activity_run_path_creation_placeholder);
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

    /**
     * Réinitialise les composants graphiques à ce qu'il étaient au début
     */
    private void reinitialiserActivity() {
        dbHelper.insertCourse(course);
        choChronometre.reset();
        podometre.stop();
        gestionnaireMap.stop();
        toggleLayouts(EtatLayoutsRun.START);
        pgsProgresTrajet.setVisibility(View.INVISIBLE);
        btnPath.setEnabled(true);
        course.setTrajet(null);
        indiceTrajetChoisi = 0;
        resetInfos();
    }

    /**
     * Créer un nouveau trajet
     */
    private void creerNouveauTrajer(String nomTrajet) {
        dbHelper.ajouterTrajet(new Trajet(nomTrajet, gestionnaireMap.getDistanceTotale(),
                choChronometre.getElapsedTimeInMillis(), gestionnaireMap.getListePoints()));
    }

    /**
     * Appelée au click sur resume
     */
    private void resume() {
        choChronometre.start();
        podometre.resume();
        gestionnaireMap.resume();
        toggleLayouts(EtatLayoutsRun.PAUSE);
    }

    /**
     * Altère la visibilité des layouts selon l'état passée en paramètre
     */
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

    /**
     * Réinitialise les labels d'information à leurs valeurs par défaut
     */
    private void resetInfos() {
        this.lblDistance.setText(getResources().getString(R.string.activity_run_lbl_distance_defaut));
        this.lblSpeed.setText(getResources().getString(R.string.activity_run_lbl_vitesse_defaut));
        this.lblCalories.setText(getResources().getString(R.string.activity_run_lbl_calories_defaut));
        this.lblSteps.setText(getResources().getString(R.string.activity_run_lbl_steps_defaut));
        this.btnPath.setText(getString(R.string.activity_run_btn_path));
    }

    /**
     * Appel toutes les méthodes d'update d'informations
     */
    private void updateInfos() {
        updateDistance();
        updateVitesse();
        updateCalories();
        updatePas();
        updateProgress();
    }

    /**
     * Met la barre de progression à jour selon la distance parcourue
     */
    private void updateProgress() {
        if (course.getTrajet() != null) {
            pgsProgresTrajet.setProgress(convertirEnMetres(course.getDistance()));
            pgsProgresTrajet.setSecondaryProgress(convertirEnMetres(gestionnaireMap.getDistanceCouranteFantome()));
        }
    }

    /**
     * Met à jour le nombre de pas si applicable
     */
    private void updatePas() {
        if (running && androidKitKatOrHigher) {
            course.setPas(podometre.getNombrePas());
            this.lblSteps.setText(Integer.toString(course.getPas()));
        }
    }

    /**
     * Met à jour le nombre de calories consommées
     */
    private void updateCalories() {
        course.setCalories(calculerCalories());
        this.lblCalories.setText(Integer.toString(course.getCalories()));
    }

    /**
     * Calcule le nombre de calories consommées
     */
    private int calculerCalories() {
        return (int) Math.round((distanceKmToMiles(course.getDistance()) /
                (running ? DIVIDANTE_CONVERTION_DISTANCE_COURSE : DIVIDANTE_CONVERTION_DISTANCE_VELO))
                * (MULTIPLICATEUR_POIDS_CALORIES * profil.getPoidsLbs()));
    }

    /**
     * Transforme une distance de km à miles.
     */
    private double distanceKmToMiles(double distanceKm) {
        return distanceKm * MULTIPLICATEUR_CONVERTION_KM_TO_MILES;
    }

    /**
     * Met à jour la vitesse
     */
    private void updateVitesse() {
        double vitesse = 0;

        double elapsedTimeInHours = choChronometre.getElapsedTimeInHours();
        if (elapsedTimeInHours > 0.0) {
            vitesse = course.getDistance() / elapsedTimeInHours;
        }

        this.course.setVitesse(vitesse);
        this.lblSpeed.setText(String.format(getResources().getString(R.string.activity_run_lbl_vitesse_valeur),
                vitesse));
    }

    /**
     * Met à jour la distance parcourue
     */
    private void updateDistance() {
        double resultat = gestionnaireMap.getDistanceTotale();
        this.course.setDistance(resultat);
        this.lblDistance.setText(String.format(getResources().getString(R.string.activity_run_lbl_distance_valeur),
                resultat));
    }

    /**
     * Appel de la map provoquant une mise à jour des informations
     */
    @Override
    public void callbackMap() {
        updateInfos();
    }

    /**
     * Converti une distance de km à mettre
     */
    private int convertirEnMetres(double distanceEnKm) {
        return (int) Math.round(distanceEnKm * NOMBRE_METRES_DANS_KILOMETRE);
    }
}
