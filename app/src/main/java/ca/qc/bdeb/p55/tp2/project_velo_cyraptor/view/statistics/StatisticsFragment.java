package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.statistics;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.Statistiques;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.TypeCourse;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.persistance.DbHelper;

public class StatisticsFragment extends Fragment {

    private static final String CLEE_TYPE = "typeCourse";
    private static final int NOMBRE_MILLIS_DANS_SECONDE = 1000;
    private static final int NOMBRE_SECONDES_DANS_MINUTE = 60;
    private TypeCourse typeCourse;
    private Statistiques statistiques;

    private TextView lblDureeSum;
    private TextView lblDureeAvg;
    private TextView lblPasSum;
    private TextView lblPasAvg;
    private TextView lblDistanceSum;
    private TextView lblDistanceAvg;
    private TextView lblCaloriesSum;
    private TextView lblCaloriesAvg;
    private TextView lblVitesseAvg;
    private LinearLayout layPasAvg;
    private LinearLayout layPasSum;
    private Space spcPas1;
    private Space spcPas2;

    public static StatisticsFragment newInstance(TypeCourse typeCourse) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putSerializable(CLEE_TYPE, typeCourse);
        fragment.setArguments(args);
        return fragment;
    }

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeCourse = (TypeCourse) getArguments().getSerializable(CLEE_TYPE);
        statistiques = DbHelper.getInstance(getContext()).getStats(typeCourse);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        assignerComposants(view);
        affecterValeursEtModifierVue();
        return view;
    }

    /**
     * Assigne tous les composants à leurs variables
     */
    private void assignerComposants(View view) {
        lblCaloriesAvg = (TextView) view.findViewById(R.id.stats_fragment_lbl_calories_avg);
        lblCaloriesSum = (TextView) view.findViewById(R.id.stats_fragment_lbl_calories_totaux);
        lblDistanceAvg = (TextView) view.findViewById(R.id.stats_fragment_lbl_distance_avg);
        lblDistanceSum = (TextView) view.findViewById(R.id.stats_fragment_lbl_distance_totale);
        lblDureeAvg = (TextView) view.findViewById(R.id.stats_fragment_lbl_duree_avg);
        lblDureeSum = (TextView) view.findViewById(R.id.stats_fragment_lbl_duree_totale);
        lblPasSum = (TextView) view.findViewById(R.id.stats_fragment_lbl_pas_avg);
        lblPasAvg = (TextView) view.findViewById(R.id.stats_fragment_lbl_pas_totaux);
        lblVitesseAvg = (TextView) view.findViewById(R.id.stats_fragment_lbl_vitesse_avg);
        layPasAvg = (LinearLayout) view.findViewById(R.id.stats_fragment_lay_pas_avg);
        layPasSum = (LinearLayout) view.findViewById(R.id.stats_fragment_lay_pas_totaux);
        spcPas1 = (Space) view.findViewById(R.id.stats_fragment_spc_pas_1);
        spcPas2 = (Space) view.findViewById(R.id.stats_fragment_spc_pas_2);
    }

    /**
     * Affecte toutes les valeurs à leurs composants si c'est possible, cache les composants si c'est impossible
     */
    private void affecterValeursEtModifierVue() {
        if (typeCourse == TypeCourse.A_VELO
                || !getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
            layPasAvg.setVisibility(View.GONE);
            layPasSum.setVisibility(View.GONE);
            spcPas1.setVisibility(View.GONE);
            spcPas2.setVisibility(View.GONE);
        } else {
            lblPasSum.setText(Integer.toString(statistiques.getPasTotaux()));
            lblPasAvg.setText(Integer.toString(statistiques.getPasMoyens()));
        }
        lblCaloriesAvg.setText(Integer.toString(statistiques.getCaloriesMoyennes()));
        lblCaloriesSum.setText(Integer.toString(statistiques.getCaloriesTotales()));
        lblDistanceAvg.setText(String.format(getString(R.string.fragment_stats_lbl_distance_defaut),
                statistiques.getDistanceMoyenne()));
        lblDistanceSum.setText(String.format(getString(R.string.fragment_stats_lbl_distance_defaut),
                statistiques.getDistanceTotale()));
        lblDureeAvg.setText(String.format(getString(R.string.fragment_stats_lbl_duree_defaut),
                millisToMin(statistiques.getDureeMoyenne())));
        lblDureeSum.setText(String.format(getString(R.string.fragment_stats_lbl_duree_defaut),
                millisToMin(statistiques.getDureeTotale())));
        lblVitesseAvg.setText(String.format(getString(R.string.fragment_stats_lbl_vitesse_defaut),
                statistiques.getVitesseMoyenne()));
    }

    /**
     * Converti un temps de millisecondes en minutes
     */
    private double millisToMin(double millis) {
        double secondes = millis / NOMBRE_MILLIS_DANS_SECONDE;
        double minutes = secondes / NOMBRE_SECONDES_DANS_MINUTE;
        return minutes;
    }
}
